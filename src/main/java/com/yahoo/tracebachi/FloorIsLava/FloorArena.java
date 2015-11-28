/*
 * This file is part of FloorIsLava.
 *
 * FloorIsLava is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FloorIsLava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FloorIsLava.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.yahoo.tracebachi.FloorIsLava;

import com.yahoo.tracebachi.FloorIsLava.UtilClasses.PlayerState;
import com.yahoo.tracebachi.FloorIsLava.UtilClasses.Point;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class FloorArena implements Listener
{
    public static final String GOOD = ChatColor.translateAlternateColorCodes('&', "&8[&aFIL&8]&a ");
    public static final String BAD = ChatColor.translateAlternateColorCodes('&', "&8[&cFIL&8]&c ");

    private FloorIsLavaPlugin plugin;
    private ItemStack winPrize;

    private boolean started = false;
    private boolean enabled = true;
    private BukkitTask arenaTask;
    private BukkitTask countdownTask;
    private FloorArenaBlocks arenaBlocks;
    private Location watchLocation;

    private HashMap<String, PlayerState> playing = new HashMap<>();
    private ArrayList<String> watching = new ArrayList<>();

    private int minimumPlayers;
    private int baseReward;
    private int winnerReward;
    private int maxCountdown;
    private String worldName;
    private List<String> prestartCommands;
    private List<String> whitelistCommands;
    private int ticksPerCheck;
    private int startDegradeOn;
    private int degradeOn;

    private int wager = 0;
    private int countdown = 0;
    private int elapsedTicks = 0;
    private int degradeLevel = 0;

    public FloorArena(FloorIsLavaPlugin plugin)
    {
        this.plugin = plugin;
        this.winPrize = new ItemStack(Material.POTATO_ITEM);

        ItemMeta winTatoMeta = winPrize.getItemMeta();
        winTatoMeta.setDisplayName(ChatColor.GOLD + "WinTato");
        List<String> lore = new ArrayList<>();
        lore.add("You won a round of FloorIsLava!");
        lore.add("--");
        lore.add("May the WinTato be with you - Zee");
        winTatoMeta.setLore(lore);
        winPrize.setItemMeta(winTatoMeta);
    }

    public String addWager(int amount, String name)
    {
        EconomyResponse response = plugin.getEconomy().bankWithdraw(name, amount);

        if(response.transactionSuccess())
        {
            wager += amount;
            broadcast(GOOD + "+$" + amount + " by " + name +
                " ( = $" + wager + " )" );

            return GOOD + "You added $" + amount +
                " to FloorIsLava ( = $" + wager + " )";
        }
        else
        {
            return BAD + "You do not have enough funds to wager that amount.";
        }
    }

    public String add(Player player)
    {
        if(!enabled)
        {
            return BAD + "Unable to join. FloorIsLava is currently disabled.";
        }

        if(started)
        {
            return BAD + "Unable to join. FloorIsLava has already begun.";
        }

        String playerName = player.getName();
        if(playing.containsKey(playerName))
        {
            return BAD + "You are already waiting to play FloorIsLava.";
        }

        playing.put(playerName, null);
        watching.add(playerName);

        broadcast(GOOD + playerName + " has joined.");
        resetCoundown();
        return GOOD + "You have joined FloorIsLava.";
    }

    public String remove(Player player)
    {
        String name = player.getName();
        PlayerState state = playing.remove(name);
        watching.remove(name);

        if(state != null)
        {
            state.restoreInventory(player);
            state.restoreLocation(player);
            state.restoreGameMode(player);

            if(watching.size() < minimumPlayers)
            {
                resetCoundown();
            }

            return GOOD + "You have left FloorIsLava.";
        }
        else
        {
            if(started)
            {
                return BAD + "You are not part of FloorIsLava.";
            }
            else
            {
                return GOOD + "You have left FloorIsLava.";
            }
        }
    }

    public void start()
    {
        if(started)
        {
            throw new IllegalStateException("start() was called while arena has already been started!");
        }

        Iterator<Map.Entry<String, PlayerState>> iter = playing.entrySet().iterator();
        World world = Bukkit.getWorld(worldName);

        arenaBlocks.save(world);

        while(iter.hasNext())
        {
            Map.Entry<String, PlayerState> entry = iter.next();
            Player player = Bukkit.getPlayerExact(entry.getKey());

            if(player != null && player.isOnline())
            {
                PlayerState playerState = new PlayerState();
                playerState.save(player);
                playing.put(entry.getKey(), playerState);

                for(PotionEffect e : player.getActivePotionEffects())
                {
                    player.removePotionEffect(e.getType());
                }

                for(String command : prestartCommands)
                {
                    Bukkit.getServer().dispatchCommand(player, command);
                }

                player.getInventory().setArmorContents(new ItemStack[]{});
                player.getInventory().setContents(new ItemStack[]{});
                player.teleport(arenaBlocks.getRandomLocationInside(world));
            }
            else
            {
                plugin.getLogger().info(entry.getKey() + " should have been removed on logout, but was not.");
                iter.remove();
                watching.remove(entry.getKey());
            }
        }

        arenaTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 10, ticksPerCheck);

        started = true;
    }

    public void tick()
    {
        Iterator<Map.Entry<String, PlayerState>> iter = playing.entrySet().iterator();
        World world = Bukkit.getWorld(worldName);

        while(iter.hasNext())
        {
            Map.Entry<String, PlayerState> entry = iter.next();
            Player player = Bukkit.getPlayer(entry.getKey());
            PlayerState state = entry.getValue();
            Location location = player.getLocation();

            if(!arenaBlocks.isInside(location))
            {
                iter.remove();
                state.restoreInventory(player);
                state.restoreLocation(player);
                state.restoreGameMode(player);

                plugin.getEconomy().bankDeposit(entry.getKey(), baseReward);
                player.sendMessage(GOOD + "Thanks for playing! Here's $" +
                    baseReward);

                broadcast(BAD + entry.getKey() + " fell! " +
                    playing.size() + '/' + watching.size() + " left!");
            }
        }

        if(playing.size() > 1)
        {
            if(elapsedTicks >= startDegradeOn && (elapsedTicks % degradeOn) == 0)
            {
                arenaBlocks.degradeBlocks(world, degradeLevel);
                degradeLevel++;
            }

            elapsedTicks++;
        }
        else
        {
            for(Map.Entry<String, PlayerState> entry : playing.entrySet())
            {
                Player player = Bukkit.getPlayer(entry.getKey());
                PlayerState state = entry.getValue();

                state.restoreInventory(player);
                state.restoreLocation(player);
                player.getInventory().addItem(winPrize);
                state.restoreGameMode(player);

                plugin.getEconomy().bankDeposit(entry.getKey(),
                    (winnerReward + wager));

                player.sendMessage(GOOD + "You won! Here's a WinTato and $" +
                    (winnerReward + wager));

                plugin.getLogger().info(entry.getKey() + " won a round. Amount = " +
                    (winnerReward + wager));
                wager = 0;

                broadcast(GOOD + entry.getKey() + " won that round!");
            }

            postStopCleanup();
        }
    }

    public void countdownTick()
    {
        if(countdown <= 0)
        {
            countdownTask.cancel();
            countdownTask = null;
            start();
        }
        else
        {
            broadcast(GOOD + "Starting in " + countdown);
            countdown--;
        }
    }

    /*************************************************************************/
    /* Getter Methods */
    /*************************************************************************/
    public boolean hasStarted()
    {
        return started;
    }

    public int getWager()
    {
        return wager;
    }

    public int getWatchingSize()
    {
        return watching.size();
    }

    public Location getWatchLocation()
    {
        return watchLocation;
    }

    /*************************************************************************/
    /* Arena Management Methods */
    /*************************************************************************/
    public void loadConfig(FileConfiguration config)
    {
        minimumPlayers = config.getInt("MinimumPlayers");
        baseReward = config.getInt("BaseReward");
        winnerReward = config.getInt("WinnerReward");
        maxCountdown = config.getInt("CountdownInSeconds");
        worldName = config.getString("WorldName");
        prestartCommands = config.getStringList("PrestartCommands");
        whitelistCommands = config.getStringList("WhitelistCommands");
        ticksPerCheck = config.getInt("TicksPerCheck");
        startDegradeOn = config.getInt("StartDegradeOn");
        degradeOn = config.getInt("DegradeOnTick");

        arenaBlocks = new FloorArenaBlocks(
            config.getConfigurationSection("PointOne"),
            config.getConfigurationSection("PointTwo"));

        Point watchPoint = new Point(config.getConfigurationSection("WatchPoint"));
        watchLocation = new Location(Bukkit.getWorld(worldName),
            watchPoint.x(), watchPoint.y(), watchPoint.z(),
            (float) watchPoint.yaw(), (float) watchPoint.pitch());
    }

    public String forceStart()
    {
        if(started)
        {
            return BAD + "The arena has already started!";
        }
        else if(!enabled)
        {
            return BAD + "The arena is currently disabled!";
        }
        else
        {
            if(countdownTask != null)
            {
                countdownTask.cancel();
                countdownTask = null;
            }

            start();
            return GOOD + "Force-Started FloorIsLava.";
        }
    }

    public String forceStop()
    {
        if(started)
        {
            if(arenaTask != null)
            {
                arenaTask.cancel();
                arenaTask = null;
            }

            for(Map.Entry<String, PlayerState> entry : playing.entrySet())
            {
                Player player = Bukkit.getPlayer(entry.getKey());
                PlayerState state = entry.getValue();

                state.restoreInventory(player);
                state.restoreLocation(player);
                state.restoreGameMode(player);
            }

            int oldWager = wager;
            postStopCleanup();
            wager = oldWager;
        }
        return GOOD + "Force-Stopped FloorIsLava.";
    }

    public void enableArena()
    {
        enabled = true;
    }

    public void disableArena()
    {
        forceStop();
        enabled = false;
    }

    /*************************************************************************/
    /* Arena Event Methods */
    /*************************************************************************/
    @EventHandler
    public void onPlayerBlockClick(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if(playing.containsKey(playerName) && started)
        {
            event.setCancelled(true);
            if(event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                Location clicked = event.getClickedBlock().getLocation();
                if(arenaBlocks.isInside(clicked))
                {
                    event.getClickedBlock().setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        String playerName = event.getPlayer().getName();
        Location locTo = event.getTo();

        if(arenaBlocks.isInside(locTo) && !playing.containsKey(playerName))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if(playing.containsKey(playerName))
        {
            String word = event.getMessage().split( "\\s+" )[0];

            if(player.hasPermission("FloorIsLava.Staff")) { return; }
            if(whitelistCommands.contains(word)) { return; }

            player.sendMessage(BAD + "That command is not allowed while in FloorIsLava.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        remove(event.getPlayer());
    }

    /*************************************************************************/
    /* Private Methods */
    /*************************************************************************/
    private void resetCoundown()
    {
        if(countdownTask != null)
        {
            countdownTask.cancel();
            countdownTask = null;
        }

        if(playing.size() >= minimumPlayers)
        {
            countdown = maxCountdown;
            countdownTask = Bukkit.getScheduler().runTaskTimer(plugin,
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        countdownTick();
                    }
                },
                100, 10);
        }
        else
        {
            broadcast(BAD + "Too few players to start.");
        }
    }

    private void postStopCleanup()
    {
        degradeLevel = 0;
        elapsedTicks = 0;

        playing.clear();
        watching.clear();

        arenaBlocks.restore();

        if(arenaTask != null)
        {
            arenaTask.cancel();
            arenaTask = null;
        }
        if(countdownTask != null)
        {
            countdownTask.cancel();
            countdownTask = null;
        }

        started = false;
    }

    private void broadcast(String message)
    {
        for(String name : watching)
        {
            Player target = Bukkit.getPlayer(name);
            if(target != null && target.isOnline())
            {
                target.sendMessage(message);
            }
        }
    }
}
