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
package com.gmail.tracebachi.FloorIsLava.arena;

import com.gmail.tracebachi.FloorIsLava.FloorIsLavaPlugin;
import com.gmail.tracebachi.FloorIsLava.booster.Booster;
import com.gmail.tracebachi.FloorIsLava.utils.Loadout;
import com.gmail.tracebachi.FloorIsLava.utils.PlayerState;
import com.gmail.tracebachi.FloorIsLava.utils.Point;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class Arena implements Listener
{
    public static final String GOOD = ChatColor.translateAlternateColorCodes('&', "&8[&aFIL&8]&a ");
    public static final String BAD = ChatColor.translateAlternateColorCodes('&', "&8[&cFIL&8]&c ");

    private FloorIsLavaPlugin plugin;
    private Booster booster;

    private ItemStack winPrize;
    private ItemStack losePrize;

    private boolean started = false;
    private boolean enabled = true;
    private BukkitTask arenaTask;
    private BukkitTask countdownTask;
    private ArenaBlocks arenaBlocks;
    private Location watchLocation;

    private HashMap<String, PlayerState> playing = new HashMap<>();
    private HashMap<String, Loadout> loadoutMap = new HashMap<>();
    private ArrayList<String> watching = new ArrayList<>();

    private HashMap<String, Long> tntUseDelayMap = new HashMap<>();
    private HashMap<String, Long> hookUseDelayMap = new HashMap<>();
    private HashMap<String, Long> webUseDelayMap = new HashMap<>();
    private HashMap<String, Long> invisUseDelayMap = new HashMap<>();
    private HashMap<String, Long> boostUseDelayMap = new HashMap<>();
    private HashMap<String, Long> chikunUseDelayMap = new HashMap<>();
    private HashMap<String, Long> stealUseDelayMap = new HashMap<>();

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
    private int tntUseDelay;
    private int hookUseDelay;
    private int deWebUseDelay;
    private int invisUseDelay;
    private int boostUseDelay;
    private int chikunUseDelay;
    private int stealUseDelay;

    private int wager = 0;
    private int countdown = 0;
    private int elapsedTicks = 0;
    private int degradeLevel = 0;

    private int boosterBroadcastRange;

    public Arena(FloorIsLavaPlugin plugin)
    {
        this.plugin = plugin;
        this.booster = new Booster(plugin, this);
        this.winPrize = new ItemStack(Material.POTATO_ITEM);
        this.losePrize = new ItemStack(Material.POISONOUS_POTATO);

        ItemMeta winTatoMeta = winPrize.getItemMeta();
        winTatoMeta.setDisplayName(ChatColor.GOLD + "WinTato");
        List<String> winTatoLore = new ArrayList<>();
        winTatoLore.add("You won a round of FloorIsLava!");
        winTatoLore.add("--");
        winTatoLore.add("May the WinTato be with you - Zee");
        winTatoMeta.setLore(winTatoLore);
        winPrize.setItemMeta(winTatoMeta);

        ItemMeta loseTatoMeta = losePrize.getItemMeta();
        loseTatoMeta.setDisplayName(ChatColor.RED + "LoseTato");
        List<String> loseTatoLore = new ArrayList<>();
        loseTatoLore.add("You lost a round of FloorIsLava!");
        loseTatoLore.add("--");
        loseTatoLore.add("Better luck next time - Fridge");
        loseTatoMeta.setLore(loseTatoLore);
        losePrize.setItemMeta(loseTatoMeta);
    }

    public void addWager(Integer amount, Player player)
    {
        if(amount == null || amount <= 0)
        {
            player.sendMessage(BAD + "That is not a valid amount to wager.");
            return;
        }

        String name = player.getName();
        EconomyResponse response = plugin.getEconomy().withdrawPlayer(name, amount);

        if(response.transactionSuccess())
        {
            wager += amount;
            broadcast(GOOD + "+$" + amount + " by " + name + " ( = $" + wager + " )", name);
            player.sendMessage(GOOD + "You added $" + amount + " to FloorIsLava ( = $" + wager + " )");
        }
        else
        {
            player.sendMessage(BAD + "You do not have enough funds to wager that amount.");
        }
    }

    public void add(Player player)
    {
        if(!enabled)
        {
            player.sendMessage(BAD + "Unable to join. FloorIsLava is currently disabled.");
            return;
        }

        if(started)
        {
            player.sendMessage(BAD + "Unable to join. FloorIsLava has already begun.");
            return;
        }

        String playerName = player.getName();

        if(playing.containsKey(playerName))
        {
            player.sendMessage(BAD + "You are already waiting to play FloorIsLava.");
            return;
        }

        playing.put(playerName, null);
        watching.add(playerName);

        broadcast(GOOD + playerName + " has joined.", playerName);
        resetCoundown();

        player.sendMessage(GOOD + "You have joined FloorIsLava.");
    }

    public void remove(Player player)
    {
        String name = player.getName();

        if(!playing.containsKey(name) && !watching.contains(name))
        {
            player.sendMessage(BAD + "You are not part of FloorIsLava.");
            return;
        }

        PlayerState state = playing.remove(name);

        if(state != null)
        {
            state.restoreInventory(player);
            state.restoreLocation(player);
            state.restoreGameMode(player);
        }

        if(arenaBlocks.isInside(player.getLocation()))
        {
            player.teleport(watchLocation);
        }

        if(arenaBlocks.isBelow(player.getLocation()))
        {
            player.teleport(watchLocation);
        }

        if(!started && watching.size() < minimumPlayers)
        {
            resetCoundown();
        }

        watching.remove(name);
        player.sendMessage(GOOD + "You have left FloorIsLava.");
        player.setFireTicks(0);
        broadcast(BAD + name + " has left.", null);
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
            Player player = Bukkit.getPlayer(entry.getKey());

            if(player != null)
            {
                String name = player.getName();
                Loadout loadout = loadoutMap.get(name);
                PlayerState playerState = new PlayerState();

                player.closeInventory();
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

                player.getInventory().setHelmet(null);
                player.getInventory().setChestplate(null);
                player.getInventory().setLeggings(null);
                player.getInventory().setBoots(null);

                player.teleport(arenaBlocks.getRandomLocationInside(world));
                player.getInventory().setContents(getContentsFromLoadout(loadout));
            }
            else
            {
                plugin.getLogger().info(entry.getKey() + " should have been removed on logout, but was not.");
                iter.remove();
                watching.remove(entry.getKey());
            }
        }

        arenaTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1, ticksPerCheck);
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
                losePrize.setAmount(booster.isActive() ? 2 : 1);
                state.restoreInventory(player);
                state.restoreLocation(player);
                state.restoreGameMode(player);
                player.setFireTicks(0);
                player.getInventory().addItem(losePrize);

                if(arenaBlocks.isBelow(player.getLocation()))
                {
                    player.teleport(watchLocation);
                }

                if(!started && watching.size() < minimumPlayers)
                {
                    resetCoundown();
                }

                if(booster.isActive()) baseReward *= 2;
                plugin.getEconomy().depositPlayer(entry.getKey(), baseReward);

                if(booster.isActive())
                {
                    player.sendMessage(GOOD + "Thanks for playing! Here's two LoseTatoes and $" + baseReward);
                }
                else
                {
                    player.sendMessage(GOOD + "Thanks for playing! Here's a LoseTatoe and $" + baseReward);
                }
                if(booster.isActive()) baseReward /= 2;

                broadcast(BAD + entry.getKey() + " fell! " + playing.size() + '/' + watching.size() + " left!", null);
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

                winPrize.setAmount(booster.isActive() ? 2 : 1);
                state.restoreInventory(player);
                state.restoreLocation(player);
                state.restoreGameMode(player);
                player.setFireTicks(0);
                player.getInventory().addItem(winPrize);

                if(booster.isActive()) winnerReward *= 2;
                plugin.getEconomy().depositPlayer(entry.getKey(), (winnerReward + wager));

                if(booster.isActive())
                {
                    player.sendMessage(GOOD + "You won! Here's two WinTatoes and $" + (winnerReward + wager));

                }
                else
                {
                    player.sendMessage(GOOD + "You won! Here's a WinTato and $" + (winnerReward + wager));
                }

                plugin.getLogger().info(entry.getKey() + " won a round. Amount = " + (winnerReward + wager));
                if(booster.isActive()) winnerReward /= 2;
                wager = 0;

                broadcast(GOOD + entry.getKey() + " won that round!", null);

                Firework firework = player.getWorld().spawn(player.getLocation().add(0, 1, 0), Firework.class);
                FireworkMeta fireworkMeta = firework.getFireworkMeta();
                fireworkMeta.addEffects(FireworkEffect.builder()
                            .flicker(false)
                            .trail(true)
                            .with(Type.BALL_LARGE)
                            .withColor(Color.BLUE)
                            .withFade(Color.WHITE)
                            .build());
                firework.setFireworkMeta(fireworkMeta);
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
            broadcast(GOOD + "Starting in " + countdown, null);
            countdown--;
        }
    }

    /**************************************************************************
     * Getter Methods
     *************************************************************************/

    public boolean hasStarted()
    {
        return started;
    }

    public ArrayList<String> getWatching()
    {
        return watching;
    }

    public int getWager()
    {
        return wager;
    }

    public int getWatchingSize()
    {
        return watching.size();
    }

    public int getBoosterBroadcastRange()
    {
        return boosterBroadcastRange;
    }

    public Location getWatchLocation()
    {
        return watchLocation;
    }

    public HashMap<String, Loadout> getLoadoutMap()
    {
        return loadoutMap;
    }

    public Booster getBooster()
    {
        return booster;
    }

    /**************************************************************************
     * Arena Management Methods
     *************************************************************************/

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
        tntUseDelay = config.getInt("ThrowingTNTUseDelay");
        hookUseDelay = config.getInt("PlayerLauncherUseDelay");
        deWebUseDelay = config.getInt("WebberUseDelay");
        invisUseDelay = config.getInt("RodOfInvisibilityUseDelay");
        boostUseDelay = config.getInt("BoostUseDelay");
        chikunUseDelay = config.getInt("ChikunUseDelay");
        stealUseDelay = config.getInt("StealUseDelay");
        boosterBroadcastRange = config.getInt("BoosterBroadcastRange");

        arenaBlocks = new ArenaBlocks(
                    config.getConfigurationSection("PointOne"),
                    config.getConfigurationSection("PointTwo"));

        Point watchPoint = new Point(config.getConfigurationSection("WatchPoint"));
        watchLocation = watchPoint.toLocation(Bukkit.getWorld(worldName));
    }

    public void forceStart(CommandSender sender)
    {
        if(started)
        {
            sender.sendMessage(BAD + "The arena has already started!");
        }
        else if(!enabled)
        {
            sender.sendMessage(BAD + "The arena is currently disabled!");
        }
        else
        {
            if(countdownTask != null)
            {
                countdownTask.cancel();
                countdownTask = null;
            }

            start();
            sender.sendMessage(GOOD + "Force-Started FloorIsLava.");
        }
    }

    public void forceStop(CommandSender sender)
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

        sender.sendMessage(GOOD + "Force-Stopped FloorIsLava.");
    }

    public void enableArena(CommandSender sender)
    {
        enabled = true;
        sender.sendMessage(Arena.GOOD + "FloorIsLava enabled.");
    }

    public void disableArena(CommandSender sender)
    {
        forceStop(sender);
        enabled = false;
        sender.sendMessage(Arena.GOOD + "FloorIsLava disabled. " +
                    "Players will not be able to join until renabled.");
    }

    /**************************************************************************
     * Arena Event Methods
     *************************************************************************/

    @EventHandler
    public void onPlayerBlockClick(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        String playerName = player.getName();
        ItemStack heldItem = event.getItem();

        if(!started || !playing.containsKey(playerName)) return;

        event.setCancelled(true);

        if(event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            Location clicked = event.getClickedBlock().getLocation();

            if(arenaBlocks.isInside(clicked))
            {
                event.getClickedBlock().setType(Material.AIR);
            }

            return;
        }

        if(heldItem == null ||
                    (event.getAction() != Action.RIGHT_CLICK_AIR &&
                    event.getAction() != Action.RIGHT_CLICK_BLOCK)) return;

        if(heldItem.getType().equals(Material.TNT))
        {
            Long endOfDelayTime = tntUseDelayMap.getOrDefault(playerName, 0L);

            if(System.currentTimeMillis() > endOfDelayTime)
            {
                if(heldItem.getAmount() == 1)
                {
                    player.getInventory().remove(heldItem);
                }
                else
                {
                    heldItem.setAmount(heldItem.getAmount() - 1);
                }

                if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
                {
                    Location location = event.getClickedBlock().getLocation();
                    TNTPrimed tnt = Bukkit.getWorld(worldName).spawn(location.add(0, 1, 0), TNTPrimed.class);
                    tnt.setMetadata("fil", new FixedMetadataValue(plugin, "fil"));
                }
                else if(event.getAction() == Action.RIGHT_CLICK_AIR)
                {
                    Location location = player.getLocation();
                    TNTPrimed tnt = Bukkit.getWorld(worldName).spawn(location.add(0, 1, 0), TNTPrimed.class);
                    tnt.setMetadata("fil", new FixedMetadataValue(plugin, "fil"));

                    Vector vector = player.getLocation().getDirection();
                    vector.add(new Vector(0.0, 0.15, 0.0));
                    tnt.setVelocity(vector);
                }

                tntUseDelayMap.put(playerName, System.currentTimeMillis() + tntUseDelay);
            }
            else
            {
                player.sendMessage(BAD + "You cannot place TNT yet.");
            }
        }
        else if(heldItem.getType().equals(Material.BLAZE_ROD))
        {
            Long endOfDelayTime = invisUseDelayMap.getOrDefault(playerName, 0L);

            if(System.currentTimeMillis() > endOfDelayTime)
            {
                if(heldItem.getAmount() == 1)
                {
                    player.getInventory().remove(heldItem);
                }
                else
                {
                    heldItem.setAmount(heldItem.getAmount() - 1);
                }

                for(Player other : Bukkit.getOnlinePlayers())
                {
                    other.hidePlayer(player);
                }

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Player playerToMakeVisible = Bukkit.getPlayer(playerName);

                    if(playerToMakeVisible == null) return;

                    playerToMakeVisible.sendMessage(GOOD + "You are now visible!");

                    for(Player other : Bukkit.getOnlinePlayers())
                    {
                        other.showPlayer(playerToMakeVisible);
                    }
                }, 60);

                player.sendMessage(GOOD + "You are now invisible!");
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1.1f);
                invisUseDelayMap.put(playerName, System.currentTimeMillis() + invisUseDelay);
            }
            else
            {
                player.sendMessage(BAD + "You cannot go invisible yet.");
            }
        }
        else if(heldItem.getType().equals(Material.FEATHER))
        {
            Long endOfDelayTime = boostUseDelayMap.getOrDefault(playerName, 0L);

            if(System.currentTimeMillis() > endOfDelayTime)
            {
                if(isPlayerNearWebs(player, 1))
                {
                    player.sendMessage(BAD + "You can not use a boost while near webs!");
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
                    return;
                }

                if(heldItem.getAmount() == 1)
                {
                    player.getInventory().remove(heldItem);
                }
                else
                {
                    heldItem.setAmount(heldItem.getAmount() - 1);
                }

                Location loc = player.getLocation().clone();
                loc.setPitch(-30f);

                Vector vector = loc.getDirection();
                vector.add(new Vector(0.0, 0.15, 0.0));
                vector.multiply(2);

                player.setVelocity(vector);
                player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1f, 1f);

                player.sendMessage(GOOD + "Woooooosh ...");
                boostUseDelayMap.put(playerName, System.currentTimeMillis() + boostUseDelay);
            }
            else
            {
                player.sendMessage(BAD + "You cannot boost yet.");
            }
        }
        else if(heldItem.getType().equals(Material.EGG))
        {
            Long endOfDelayTime = chikunUseDelayMap.getOrDefault(playerName, 0L);

            if(System.currentTimeMillis() > endOfDelayTime)
            {
                event.setCancelled(false);
                chikunUseDelayMap.put(playerName, System.currentTimeMillis() + chikunUseDelay);
            }
            else
            {
                player.sendMessage(BAD + "You cannot throw eggs yet.");
            }
        }
    }

    @EventHandler
    public void onPlayerInteractWithPlayer(PlayerInteractEntityEvent event)
    {
        Entity rightClickedEntity = event.getRightClicked();

        if(!(rightClickedEntity instanceof Player)) return;

        Player player = event.getPlayer();
        String playerName = player.getName();
        Player rightClicked = (Player) rightClickedEntity;
        ItemStack heldItem = player.getItemInHand();

        if(!started || !playing.containsKey(playerName)) return;

        event.setCancelled(true);

        if(heldItem.getType().equals(Material.TRIPWIRE_HOOK))
        {
            Long endOfDelayTime = hookUseDelayMap.getOrDefault(playerName, 0L);

            if(System.currentTimeMillis() > endOfDelayTime)
            {
                if(isPlayerNearWebs(rightClicked, 2))
                {
                    player.sendMessage(BAD + "You can not launch a player near webs!");
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
                    return;
                }

                if(heldItem.getAmount() == 1)
                {
                    player.getInventory().remove(heldItem);
                }
                else
                {
                    heldItem.setAmount(heldItem.getAmount() - 1);
                }

                Location playerLoc = player.getLocation();
                playerLoc.setPitch(-30f);

                Vector playerDir = playerLoc.getDirection();
                playerDir.add(new Vector(0.0, 0.15, 0.0));
                playerDir.multiply(2);

                rightClicked.getLocation().setDirection(playerDir);
                rightClicked.setVelocity(playerDir);
                player.playSound(player.getLocation(), Sound.HURT_FLESH, 1f, 1f);

                hookUseDelayMap.put(playerName, System.currentTimeMillis() + hookUseDelay);
            }
            else
            {
                player.sendMessage(BAD + "You cannot launch players yet.");
            }
        }
        else if(heldItem.getType().equals(Material.WEB))
        {
            Long endOfDelayTime = webUseDelayMap.getOrDefault(playerName, 0L);

            if(System.currentTimeMillis() > endOfDelayTime)
            {
                if(heldItem.getAmount() == 1)
                {
                    player.getInventory().remove(heldItem);
                }
                else
                {
                    heldItem.setAmount(heldItem.getAmount() - 1);
                }

                createWebsAroundPlayer(rightClicked, 2);

                webUseDelayMap.put(playerName, System.currentTimeMillis() + deWebUseDelay);
            }
            else
            {
                player.sendMessage(BAD + "You cannot web players yet.");
            }
        }
        else if(heldItem.getType().equals(Material.FLINT_AND_STEEL))
        {
            Long endOfDelayTime = stealUseDelayMap.getOrDefault(playerName, 0L);

            if(System.currentTimeMillis() > endOfDelayTime)
            {
                if(heldItem.getAmount() == 1)
                {
                    player.getInventory().remove(heldItem);
                }
                else
                {
                    heldItem.setAmount(heldItem.getAmount() - 1);
                }

                Random random = new Random();
                int chance =  random.nextInt(100);
                if(chance < 50)
                {
                    player.sendMessage(BAD + "Badluck! Your attempt to steal an ability has backfired.");
                    if(!doesPlayerHaveItems(player))
                    {
                        player.sendMessage(BAD + "It appears you do not have any abilities left...");
                        launchThief(player);
                    }
                    else
                    {
                        takeAbility(null, player);
                        player.sendMessage(BAD + "A random ability has been taken away from you!");
                    }
                }
                else
                {
                    if(!doesPlayerHaveItems(rightClicked))
                    {
                        player.sendMessage(BAD + "It appears your victim does not have any abilities left...");
                        launchThief(player);
                    }
                    else
                    {
                        takeAbility(player, rightClicked);
                        player.sendMessage(GOOD + "A random ability has been taken away from " + rightClicked.getName() + "!");
                        rightClicked.sendMessage(BAD + "A random ability has been stolen by " + player.getName() + "!");
                    }
                }
                stealUseDelayMap.put(playerName, System.currentTimeMillis() + stealUseDelay);
            }
            else
            {
                player.sendMessage(BAD + "You cannot attempt to steal abilities yet.");
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event)
    {
        String name = event.getPlayer().getName();

        if(started && playing.containsKey(name))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        String name = event.getPlayer().getName();

        if(started && playing.containsKey(name))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDragItem(InventoryDragEvent event)
    {
        String name = event.getWhoClicked().getName();

        if(started && playing.containsKey(name))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClickArenaStarted(InventoryClickEvent event)
    {
        String name = event.getWhoClicked().getName();

        if(started && playing.containsKey(name))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event)
    {
        List<Block> blocksToBeDestroyed = event.blockList();
        ListIterator<Block> iterator = blocksToBeDestroyed.listIterator();

        if(!event.getEntity().hasMetadata("fil")) return;

        event.setCancelled(true);

        while(iterator.hasNext())
        {
            Block block = iterator.next();

            if(arenaBlocks.isInside(block.getLocation()))
            {
                if(started)
                {
                    block.setType(Material.AIR);
                }

                iterator.remove();
            }
        }

        if(!blocksToBeDestroyed.isEmpty())
        {
            blocksToBeDestroyed.clear();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        Entity entity = event.getEntity();

        if(!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.EGG)
                    || !arenaBlocks.isInside(entity.getLocation())
                    || !event.getEntity().getType().equals(EntityType.CHICKEN)) return;
        event.setCancelled(false);

        entity.setCustomNameVisible(true);
        entity.setCustomName(ChatColor.LIGHT_PURPLE + "\\o/ CHIKUN \\o/");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            entity.remove();
        }, 200);
    }

    @EventHandler
    public void onPlayerEggThrow(PlayerEggThrowEvent event)
    {
        Egg egg = event.getEgg();
        Location location = egg.getLocation();
        Player player = event.getPlayer();
        if(!playing.containsKey(player.getName())) return;

        event.setHatching(true);
        event.setNumHatches((byte) 4);

        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffects(FireworkEffect.builder()
                    .flicker(true)
                    .trail(false)
                    .with(Type.STAR)
                    .withColor(Color.GREEN)
                    .withFade(Color.WHITE)
                    .build());
        firework.setFireworkMeta(fireworkMeta);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event)
    {
        if(event.getEntityType().equals(EntityType.PLAYER))
        {
            String name = event.getEntity().getName();

            if(started && playing.containsKey(name))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event)
    {
        if(event.getEntityType().equals(EntityType.PLAYER) && event.getDamager() instanceof  TNTPrimed)
        {
            Entity tnt = event.getDamager();
            if(tnt.hasMetadata("fil"))
            {
                event.setCancelled(true);
                event.setDamage(0);
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
            String word = event.getMessage().split("\\s+")[0];

            if(player.hasPermission("FloorIsLava.Staff"))
            {
                return;
            }

            if(whitelistCommands.contains(word))
            {
                return;
            }

            player.sendMessage(BAD + "That command is not allowed while in FloorIsLava.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        remove(event.getPlayer());
    }

    /*************************************************************************
     * Private Methods
     *************************************************************************/

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
                        this::countdownTick, 100, 10);
        }
        else
        {
            broadcast(BAD + "Too few players to start.", null);
        }
    }

    private void postStopCleanup()
    {
        degradeLevel = 0;
        elapsedTicks = 0;

        for(String playerName : watching)
        {
            Player player = Bukkit.getPlayerExact(playerName);

            if(player != null)
            {
                for(Player other : Bukkit.getOnlinePlayers())
                {
                    other.showPlayer(player);
                }
            }
        }

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

    private void broadcast(String message, String exclude)
    {
        for(String name : watching)
        {
            if(!name.equalsIgnoreCase(exclude))
            {
                Player target = Bukkit.getPlayer(name);

                if(target != null)
                {
                    target.sendMessage(message);
                }
            }
        }
    }

    private void createWebsAroundPlayer(Player player, int radius)
    {
        int px = player.getLocation().getBlockX();
        int py = player.getLocation().getBlockY();
        int pz = player.getLocation().getBlockZ();
        World world = player.getWorld();

        for(int x = -radius; x <= radius; x++)
        {
            for(int y = -radius; y <= radius; y++)
            {
                for(int z = -radius; z <= radius; z++)
                {
                    if(x * x + y * y + z * z <= radius * radius)
                    {
                        int xpos = px + x;
                        int ypos = py + y;
                        int zpos = pz + z;

                        if(world.getBlockAt(xpos, ypos, zpos).getType().equals(Material.AIR) &&
                                    arenaBlocks.isInside(xpos, ypos, zpos))
                        {
                            world.getBlockAt(xpos, ypos, zpos).setType(Material.WEB);
                        }
                    }
                }
            }
        }
    }

    private boolean isPlayerNearWebs(Player player, int radius)
    {
        int px = player.getLocation().getBlockX();
        int py = player.getLocation().getBlockY();
        int pz = player.getLocation().getBlockZ();
        World world = player.getWorld();

        for(int x = -radius; x <= radius; x++)
        {
            for(int y = -radius; y <= radius; y++)
            {
                for(int z = -radius; z <= radius; z++)
                {
                    if(x * x + y * y + z * z <= radius * radius)
                    {
                        int xpos = px + x;
                        int ypos = py + y;
                        int zpos = pz + z;

                        if(world.getBlockAt(xpos, ypos, zpos).getType().equals(Material.WEB) &&
                                    arenaBlocks.isInside(xpos, ypos, zpos))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean doesPlayerHaveItems(Player player)
    {
        for(ItemStack itemStack : player.getInventory().getContents())
        {
            if(itemStack != null && !itemStack.getType().equals(Material.AIR)) return true;
        }
        return false;
    }

    private void launchThief(Player player)
    {
        player.sendMessage(BAD + "Go away, thief!");
        Vector dir = player.getLocation().getDirection();
        Vector vec = new Vector(-dir.getX() * 10.0D, 0.6D, -dir.getZ() * 10.0D);
        player.setVelocity(vec);
    }

    private void takeAbility(Player to, Player from)
    {
        Random random = new Random();
        int randomAbilitySlot = random.nextInt(7);
        while(from.getInventory().getContents()[randomAbilitySlot] == null
                    || from.getInventory().getContents()[randomAbilitySlot].getType()
                    .equals(Material.AIR))
        {
            randomAbilitySlot = random.nextInt(7);
        }
        ItemStack takenAway = from.getInventory().getContents()[randomAbilitySlot];
        if(takenAway.getAmount() == 1)
        {
            from.getInventory().remove(takenAway);
        }
        else
        {
            takenAway.setAmount(takenAway.getAmount() - 1);
        }
        ItemStack toGive = takenAway.clone();
        toGive.setAmount(1);
        if(to != null) to.getInventory().addItem(toGive);
    }

    private ItemStack[] getContentsFromLoadout(Loadout loadout)
    {
        ItemStack[] contents = new ItemStack[7];
        int c = 0;

        if(loadout == null)
        {
            return contents;
        }

        if(loadout.tntCount > 0)
        {
            contents[c] = Loadout.TNT_ITEM.clone();
            contents[c].setAmount(loadout.tntCount);
            c++;
        }

        if(loadout.hookCount > 0)
        {
            contents[c] = Loadout.HOOK_ITEM.clone();
            contents[c].setAmount(loadout.hookCount);
            c++;
        }

        if(loadout.webCount > 0)
        {
            contents[c] = Loadout.WEB_ITEM.clone();
            contents[c].setAmount(loadout.webCount);
            c++;
        }

        if(loadout.invisCount > 0)
        {
            contents[c] = Loadout.INVIS_ITEM.clone();
            contents[c].setAmount(loadout.invisCount);
            c++;
        }

        if(loadout.boostCount > 0)
        {
            contents[c] = Loadout.BOOST_ITEM.clone();
            contents[c].setAmount(loadout.boostCount);
            c++;
        }

        if(loadout.chikunCount > 0)
        {
            contents[c] = Loadout.CHIKUN_ITEM.clone();
            contents[c].setAmount((loadout.chikunCount));
            c++;
        }

        if(loadout.stealCount > 0)
        {
            contents[c] = Loadout.STEAL_ITEM.clone();
            contents[c].setAmount((loadout.stealCount));
            c++;
        }

        return contents;
    }
}
