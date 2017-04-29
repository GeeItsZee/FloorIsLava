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
package com.gmail.tracebachi.FloorIsLava.Arena;

import com.gmail.tracebachi.FloorIsLava.Booster.Booster;
import com.gmail.tracebachi.FloorIsLava.FloorIsLavaPlugin;
import com.gmail.tracebachi.FloorIsLava.Utils.*;
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
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

import static com.gmail.tracebachi.FloorIsLava.Utils.ChatStrings.BAD;
import static com.gmail.tracebachi.FloorIsLava.Utils.ChatStrings.GOOD;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class Arena implements Listener
{
    private FloorIsLavaPlugin plugin;
    private Booster booster;
    private Random random = new Random();

    private ItemStack winPrize;
    private ItemStack losePrize;

    private Map<String, PlayerState> playing = new HashMap<>();
    private Map<String, Loadout> loadoutMap = new HashMap<>();
    private Map<String, ItemUseDelay> delayMap = new HashMap<>();
    private Set<String> watching = new HashSet<>();

    private boolean started = false;
    private boolean enabled = true;
    private int wager = 0;
    private int countdown = 0;
    private int elapsedTicks = 0;
    private int degradeLevel = 0;
    private BukkitTask arenaTask;
    private BukkitTask countdownTask;
    private ArenaBlocks arenaBlocks;

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
    private int boosterBroadcastRange;
    private CuboidArea arenaCuboidArea;
    private CuboidArea watchCuboidArea;

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

    public void wager(int amount, Player player)
    {
        String name = player.getName();
        EconomyResponse response = plugin.getEconomy().withdrawPlayer(name, amount);

        if(!response.transactionSuccess())
        {
            player.sendMessage(BAD + "You do not have enough funds to wager that amount.");
            return;
        }

        wager += amount;
        player.sendMessage(GOOD + "You added $" + amount + " to FloorIsLava ( = $" + wager + " )");
        broadcast(GOOD + "+$" + amount + " by " + name + " ( = $" + wager + " )", name);
    }

    public void join(Player player)
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

        broadcast(GOOD + playerName + " has joined.", playerName);
        playing.put(playerName, null);
        resetCountdown();

        player.sendMessage(GOOD + "You have joined FloorIsLava.");

        World world = Bukkit.getWorld(worldName);
        Location location = watchCuboidArea.getRandomLocationInside(world);
        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());
        player.teleport(location);
    }

    public void watch(Player player)
    {
        if(!enabled)
        {
            player.sendMessage(BAD + "Unable to join. FloorIsLava is currently disabled.");
            return;
        }

        watching.add(player.getName());

        player.sendMessage(GOOD + "Teleporting to FloorIsLava viewing area ...");

        World world = Bukkit.getWorld(worldName);
        Location location = watchCuboidArea.getRandomLocationInside(world);
        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());
        player.teleport(location);
    }

    public void leave(Player player)
    {
        String name = player.getName();

        if(watching.contains(name))
        {
            watching.remove(name);
            player.sendMessage(GOOD + "You are no longer watching FloorIsLava.");
            return;
        }

        if(!playing.containsKey(name)) { return; }

        PlayerState state = playing.remove(name);
        Location playerLocation = player.getLocation();

        if(state != null)
        {
            state.restoreInventory(player);
            state.restoreLocation(player);
            state.restoreGameMode(player);
        }

        if(arenaBlocks.getCuboidArea().isInside(playerLocation))
        {
            World world = Bukkit.getWorld(worldName);
            player.teleport(watchCuboidArea.getRandomLocationInside(world));
        }

        if(!started && playing.size() < minimumPlayers)
        {
            resetCountdown();
        }

        player.sendMessage(GOOD + "You have left FloorIsLava.");
        player.setFireTicks(0);
        player.setHealth(player.getMaxHealth());
        broadcast(BAD + name + " has left.", null);
    }

    /**************************************************************************
     * Getter Methods
     *************************************************************************/

    public boolean hasStarted()
    {
        return started;
    }

    public int getWager()
    {
        return wager;
    }

    public int getPlayingSize()
    {
        return playing.size();
    }

    public Map<String, Loadout> getLoadoutMap()
    {
        return loadoutMap;
    }

    public String getWorldName()
    {
        return worldName;
    }

    public CuboidArea getArenaCuboidArea()
    {
        return arenaCuboidArea;
    }

    public CuboidArea getWatchCuboidArea()
    {
        return watchCuboidArea;
    }

    public Booster getBooster()
    {
        return booster;
    }

    public int getBoosterBroadcastRange()
    {
        return boosterBroadcastRange;
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
        boosterBroadcastRange = config.getInt("BoosterBroadcastRange");
        prestartCommands = config.getStringList("PrestartCommands");
        whitelistCommands = config.getStringList("WhitelistCommands");
        ticksPerCheck = config.getInt("TicksPerCheck");
        startDegradeOn = config.getInt("StartDegradeOn");
        degradeOn = config.getInt("DegradeOnTick");

        tntUseDelay = config.getInt("ItemUseDelays.ThrowingTNT");
        hookUseDelay = config.getInt("ItemUseDelays.PlayerLauncher");
        deWebUseDelay = config.getInt("ItemUseDelays.Webber");
        invisUseDelay = config.getInt("ItemUseDelays.RodOfInvisibility");
        boostUseDelay = config.getInt("ItemUseDelays.Boost");
        chikunUseDelay = config.getInt("ItemUseDelays.Chikun");
        stealUseDelay = config.getInt("ItemUseDelays.Steal");

        arenaCuboidArea = new CuboidArea(
            config.getConfigurationSection("ArenaArea.One"),
            config.getConfigurationSection("ArenaArea.Two"));

        watchCuboidArea = new CuboidArea(
            config.getConfigurationSection("WaitArea.One"),
            config.getConfigurationSection("WaitArea.Two"));

        arenaBlocks = new ArenaBlocks(arenaCuboidArea);
    }

    public void enableArena(CommandSender sender)
    {
        enabled = true;
        sender.sendMessage(GOOD + "FloorIsLava enabled.");
    }

    public void disableArena(CommandSender sender)
    {
        forceStop(sender);
        enabled = false;
        sender.sendMessage(GOOD + "FloorIsLava disabled. " +
            "Players will not be able to join until renabled.");
    }

    public void forceStart(CommandSender sender)
    {
        if(!enabled)
        {
            sender.sendMessage(BAD + "The arena is currently disabled!");
        }
        else if(started)
        {
            sender.sendMessage(BAD + "The arena has already started!");
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

            if(countdownTask != null)
            {
                countdownTask.cancel();
                countdownTask = null;
            }

            for(Map.Entry<String, PlayerState> entry : playing.entrySet())
            {
                Player player = Bukkit.getPlayerExact(entry.getKey());
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

    /**************************************************************************
     * Arena Event Methods
     *************************************************************************/

    @EventHandler
    public void onPlayerChestClick(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        Inventory inventory = player.getInventory();
        String playerName = player.getName();

        if(!started || !playing.containsKey(playerName)) { return; }

        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
            && event.getClickedBlock().getType().equals(Material.CHEST))
        {
            event.setCancelled(true);
            FireworkEffect effect = FireworkEffect.builder()
                .flicker(true)
                .trail(false)
                .with(Type.STAR)
                .withColor(Color.GREEN)
                .withFade(Color.WHITE)
                .build();
            FireworkSpark.spark(effect, event.getClickedBlock().getLocation());
            event.getClickedBlock().setType(Material.AIR);
            player.sendMessage(GOOD + "You have collected a treasure chest, enjoy your items!");
            Random random = new Random();
            int firstChoice = random.nextInt(7);
            int secondChoice = random.nextInt(7);
            ItemStack[] newItems = new ItemStack[] {
                Loadout.BOOST_ITEM.clone(),
                Loadout.CHIKUN_ITEM.clone(),
                Loadout.HOOK_ITEM.clone(),
                Loadout.INVIS_ITEM.clone(),
                Loadout.STEAL_ITEM.clone(),
                Loadout.WEB_ITEM.clone(),
                Loadout.TNT_ITEM.clone() };
            if(player.getInventory().contains(newItems[firstChoice].getType()))
            {
                Material type = newItems[firstChoice].getType();
                int size = inventory.getItem(inventory.first(type)).getAmount();
                inventory.getItem(inventory.first(type)).setAmount(size + 1);
            }
            else
            {
                inventory.addItem(newItems[firstChoice]);
            }
            if(player.getInventory().contains(newItems[secondChoice].getType()))
            {
                Material type = newItems[secondChoice].getType();
                int size = inventory.getItem(inventory.first(type)).getAmount();
                inventory.getItem(inventory.first(type)).setAmount(size + 1);
            }
            else
            {
                inventory.addItem(newItems[secondChoice]);
            }
        }
    }

    @EventHandler
    public void onPlayerBlockClick(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        String playerName = player.getName();
        ItemStack heldItem = event.getItem();

        if(!started || !playing.containsKey(playerName)) { return; }

        event.setCancelled(true);

        if(event.getAction() == Action.LEFT_CLICK_BLOCK)
        {
            Location clicked = event.getClickedBlock().getLocation();

            if(arenaBlocks.getCuboidArea().isInside(clicked))
            {
                event.getClickedBlock().setType(Material.AIR);
            }

            return;
        }

        if(heldItem == null ||
            (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK)) { return; }

        World world = Bukkit.getWorld(worldName);
        Inventory inventory = player.getInventory();
        ItemUseDelay itemUseDelay = delayMap.get(playerName);

        if(itemUseDelay == null)
        {
            itemUseDelay = new ItemUseDelay();
            delayMap.put(playerName, itemUseDelay);
        }

        if(heldItem.getType().equals(Material.TNT))
        {
            long endTime = itemUseDelay.tnt;

            if(System.currentTimeMillis() > endTime)
            {
                decrementAmountOfItemStack(inventory, heldItem);

                if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
                {
                    Location location = event.getClickedBlock().getLocation();
                    TNTPrimed tnt = world.spawn(location.add(0, 1, 0), TNTPrimed.class);
                    tnt.setMetadata("FIL", new FixedMetadataValue(plugin, "FIL"));
                }
                else if(event.getAction() == Action.RIGHT_CLICK_AIR)
                {
                    Location location = player.getLocation();
                    TNTPrimed tnt = world.spawn(location.add(0, 1, 0), TNTPrimed.class);
                    tnt.setMetadata("FIL", new FixedMetadataValue(plugin, "FIL"));

                    Vector vector = player.getLocation().getDirection();
                    vector.add(new Vector(0.0, 0.15, 0.0));
                    tnt.setVelocity(vector);
                }

                itemUseDelay.tnt = System.currentTimeMillis() + tntUseDelay;
            }
            else
            {
                player.sendMessage(BAD + "You cannot place TNT yet.");
            }
        }
        else if(heldItem.getType().equals(Material.BLAZE_ROD))
        {
            long endTime = itemUseDelay.invis;

            if(System.currentTimeMillis() > endTime)
            {
                decrementAmountOfItemStack(inventory, heldItem);

                for(Player other : Bukkit.getOnlinePlayers())
                {
                    other.hidePlayer(player);
                }

                Bukkit.getScheduler().runTaskLater(plugin, () ->
                {
                    Player playerToMakeVisible = Bukkit.getPlayerExact(playerName);

                    if(playerToMakeVisible == null) { return; }

                    playerToMakeVisible.sendMessage(GOOD + "You are now visible!");

                    for(Player other : Bukkit.getOnlinePlayers())
                    {
                        other.showPlayer(playerToMakeVisible);
                    }
                }, 60);

                player.sendMessage(GOOD + "You are now invisible!");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.1f);

                itemUseDelay.invis = System.currentTimeMillis() + invisUseDelay;
            }
            else
            {
                player.sendMessage(BAD + "You cannot go invisible yet.");
            }
        }
        else if(heldItem.getType().equals(Material.FEATHER))
        {
            long endTime = itemUseDelay.boost;

            if(System.currentTimeMillis() > endTime)
            {
                if(isPlayerNearWebs(player, 1))
                {
                    player.sendMessage(BAD + "You can not use a boost while near webs!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                    return;
                }

                decrementAmountOfItemStack(inventory, heldItem);

                Location loc = player.getLocation().clone();
                loc.setPitch(-30f);

                Vector vector = loc.getDirection();
                vector.add(new Vector(0.0, 0.15, 0.0));
                vector.multiply(2);

                player.sendMessage(GOOD + "Woooooosh ...");
                player.setVelocity(vector);
                player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);

                itemUseDelay.boost = System.currentTimeMillis() + boostUseDelay;
            }
            else
            {
                player.sendMessage(BAD + "You cannot boost yet.");
            }
        }
        else if(heldItem.getType().equals(Material.EGG))
        {
            long endTime = itemUseDelay.chikun;

            if(System.currentTimeMillis() > endTime)
            {
                event.setCancelled(false);
                itemUseDelay.chikun = System.currentTimeMillis() + chikunUseDelay;
            }
            else
            {
                player.sendMessage(BAD + "You cannot throw eggs yet.");
            }
        }
        player.updateInventory();
    }

    @EventHandler
    public void onPlayerInteractWithPlayer(PlayerInteractEntityEvent event)
    {
        Entity rightClickedEntity = event.getRightClicked();

        if(!(rightClickedEntity instanceof Player)) return;

        Player player = event.getPlayer();
        String playerName = player.getName();
        Player rightClicked = (Player) rightClickedEntity;
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if(!started || !playing.containsKey(playerName)) return;

        event.setCancelled(true);

        Inventory inventory = player.getInventory();
        ItemUseDelay itemUseDelay = delayMap.get(playerName);

        if(itemUseDelay == null)
        {
            itemUseDelay = new ItemUseDelay();
            delayMap.put(playerName, itemUseDelay);
        }

        if(heldItem.getType().equals(Material.TRIPWIRE_HOOK))
        {
            long endTime = itemUseDelay.hook;

            if(System.currentTimeMillis() > endTime)
            {
                if(isPlayerNearWebs(rightClicked, 2))
                {
                    player.sendMessage(BAD + "You can not launch a player near webs!");
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
                    return;
                }

                decrementAmountOfItemStack(inventory, heldItem);

                Location playerLoc = player.getLocation();
                playerLoc.setPitch(-30f);

                Vector playerDir = playerLoc.getDirection();
                playerDir.add(new Vector(0.0, 0.15, 0.0));
                playerDir.multiply(2);

                rightClicked.getLocation().setDirection(playerDir);
                rightClicked.setVelocity(playerDir);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1f, 1f);

                itemUseDelay.hook = System.currentTimeMillis() + hookUseDelay;
            }
            else
            {
                player.sendMessage(BAD + "You cannot launch players yet.");
            }
        }
        else if(heldItem.getType().equals(Material.WEB))
        {
            long endTime = itemUseDelay.web;

            if(System.currentTimeMillis() > endTime)
            {
                decrementAmountOfItemStack(inventory, heldItem);

                createWebsAroundPlayer(rightClicked, 2);

                itemUseDelay.web = System.currentTimeMillis() + deWebUseDelay;
            }
            else
            {
                player.sendMessage(BAD + "You cannot web players yet.");
            }
        }
        else if(heldItem.getType().equals(Material.FLINT_AND_STEEL))
        {
            long endTime = itemUseDelay.steal;

            if(System.currentTimeMillis() > endTime)
            {
                decrementAmountOfItemStack(inventory, heldItem);

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

                itemUseDelay.steal = System.currentTimeMillis() + stealUseDelay;
            }
            else
            {
                player.sendMessage(BAD + "You cannot attempt to steal abilities yet.");
            }
        }
        player.updateInventory();
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

        if(!event.getEntity().hasMetadata("FIL")) { return; }

        event.setCancelled(true);

        for(Block block : blocksToBeDestroyed)
        {
            if(arenaCuboidArea.isInside(block.getLocation()))
            {
                if(started)
                {
                    block.setType(Material.AIR);
                }
            }
        }

        blocksToBeDestroyed.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event)
    {
        Entity entity = event.getEntity();

        if(!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.EGG)
            || !arenaCuboidArea.isInside(entity.getLocation())
            || !event.getEntity().getType().equals(EntityType.CHICKEN)) { return; }

        event.setCancelled(false);

        entity.setCustomNameVisible(true);
        entity.setCustomName(ChatColor.LIGHT_PURPLE + "\\o/ CHIKUN \\o/");

        Bukkit.getScheduler().runTaskLater(plugin, () ->
        {
            FireworkEffect effect = FireworkEffect.builder()
                .flicker(true)
                .trail(false)
                .with(Type.STAR)
                .withColor(Color.GREEN)
                .withFade(Color.WHITE)
                .build();
            FireworkSpark.spark(effect, entity.getLocation());
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

        FireworkEffect effect = FireworkEffect.builder()
                .flicker(true)
                .trail(false)
                .with(Type.STAR)
                .withColor(Color.GREEN)
                .withFade(Color.WHITE)
                .build();
        FireworkSpark.spark(effect, location);
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
        Entity damager = event.getDamager();

        if(event.getEntityType().equals(EntityType.PLAYER) && damager instanceof TNTPrimed)
        {
            if(damager.hasMetadata("FIL"))
            {
                event.setCancelled(true);
                event.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if(!enabled) { return; }

        Player player = event.getPlayer();
        String playerName = player.getName();

        if(watching.contains(playerName) || (!started && playing.containsKey(playerName)))
        {
            if(!watchCuboidArea.isInside(player.getLocation()))
            {
                leave(player);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Location locTo = event.getTo();

        if(arenaCuboidArea.isInside(locTo) &&
            !playing.containsKey(playerName) &&
            !player.hasPermission("FloorIsLava.Staff"))
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
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event)
    {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (playing.containsKey(playerName))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        leave(event.getPlayer());
    }

    /*************************************************************************
     * Private Methods
     *************************************************************************/

    private void start()
    {
        if(started)
        {
            throw new IllegalStateException("Arena#start() called while arena is running!");
        }

        World world = Bukkit.getWorld(worldName);

        arenaBlocks.save(world);

        Iterator<Map.Entry<String, PlayerState>> iter = playing.entrySet().iterator();

        while(iter.hasNext())
        {
            Map.Entry<String, PlayerState> entry = iter.next();
            Player player = Bukkit.getPlayerExact(entry.getKey());

            if(player == null)
            {
                iter.remove();
            }
            else
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

                player.teleport(arenaCuboidArea.getRandomLocationInside(world));
                player.getInventory().setStorageContents(getContentsFromLoadout(loadout));
                player.getInventory().setExtraContents(new ItemStack[0]);
            }
        }

        arenaTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 1, ticksPerCheck);
        started = true;
    }

    private void tick()
    {
        Iterator<Map.Entry<String, PlayerState>> iter = playing.entrySet().iterator();
        World world = Bukkit.getWorld(worldName);
        boolean boosterActive = booster.isActive();
        int scaledBaseReward = (boosterActive? baseReward * 2 : baseReward);
        int scaledWinnerReward = (boosterActive? winnerReward * 2 : winnerReward);

        losePrize.setAmount(boosterActive ? 2 : 1);
        winPrize.setAmount(boosterActive? 2 : 1);

        while(iter.hasNext())
        {
            Map.Entry<String, PlayerState> entry = iter.next();
            Player player = Bukkit.getPlayerExact(entry.getKey());
            PlayerState state = entry.getValue();
            Location location = player.getLocation();

            if(!arenaCuboidArea.isInside(location))
            {
                iter.remove();
                state.restoreInventory(player);
                state.restoreLocation(player);
                state.restoreGameMode(player);
                player.setFireTicks(0);
                player.setHealth(player.getMaxHealth());

                player.sendMessage(GOOD + "Thanks for playing!");
                player.getInventory().addItem(losePrize);
                plugin.getEconomy().depositPlayer(entry.getKey(), scaledBaseReward);
                player.teleport(watchCuboidArea.getRandomLocationInside(world));

                watching.add(player.getName());
                broadcast(BAD + entry.getKey() + " fell! " + playing.size() + " left!");
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
            return;
        }

        for(Map.Entry<String, PlayerState> entry : playing.entrySet())
        {
            Player player = Bukkit.getPlayerExact(entry.getKey());
            PlayerState state = entry.getValue();

            plugin.getFloorLeaderboard().addOneToScore(entry.getKey());

            state.restoreInventory(player);
            state.restoreLocation(player);
            state.restoreGameMode(player);
            player.setFireTicks(0);
            player.setHealth(player.getMaxHealth());

            plugin.getLogger().info(entry.getKey() + " won a round. Amount = " +
                (scaledWinnerReward + wager));

            player.sendMessage(GOOD + "You won! Here's a prize and $" +
                (scaledWinnerReward + wager));
            broadcast(GOOD + entry.getKey() + " won that round and a prize of $" +
                (scaledWinnerReward + wager), player.getName());

            player.getInventory().addItem(winPrize);
            plugin.getEconomy().depositPlayer(entry.getKey(), (scaledWinnerReward + wager));
            wager = 0;

            Firework firework = player.getWorld().spawn(
                player.getLocation().add(0, 1, 0),
                Firework.class);
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

    private void countdownTick()
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

    private void resetCountdown()
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

        for(String playerName : playing.keySet())
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

        plugin.getFloorLeaderboard().recalculate();

        started = false;
    }

    private void broadcast(String message)
    {
        broadcast(message, null);
    }

    private void broadcast(String message, String exclude)
    {
        for(String name : watching)
        {
            if(!name.equalsIgnoreCase(exclude))
            {
                Player target = Bukkit.getPlayerExact(name);

                if(target != null)
                {
                    target.sendMessage(message);
                }
            }
        }

        for(String name : playing.keySet())
        {
            if(!name.equalsIgnoreCase(exclude))
            {
                Player target = Bukkit.getPlayerExact(name);

                if(target != null)
                {
                    target.sendMessage(message);
                }
            }
        }
    }

    private void decrementAmountOfItemStack(Inventory inventory, ItemStack itemStack)
    {
        if(itemStack.getAmount() == 1)
        {
            inventory.remove(itemStack);
        }
        else
        {
            itemStack.setAmount(itemStack.getAmount() - 1);
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
                                arenaCuboidArea.isInside(xpos, ypos, zpos))
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
                                arenaCuboidArea.isInside(xpos, ypos, zpos))
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
        for(ItemStack itemStack : player.getInventory().getStorageContents())
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
        int randomAbilitySlot = random.nextInt(7);

        while(from.getInventory().getStorageContents()[randomAbilitySlot] == null
                || from.getInventory().getStorageContents()[randomAbilitySlot].getType()
                .equals(Material.AIR))
        {
            randomAbilitySlot = random.nextInt(7);
        }

        ItemStack takenAway = from.getInventory().getStorageContents()[randomAbilitySlot];

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
        ItemStack[] contents = new ItemStack[36];
        int c = 0;

        if(loadout == null)
        {
            return contents;
        }

        if(loadout.tnt > 0)
        {
            contents[c] = Loadout.TNT_ITEM.clone();
            contents[c].setAmount(loadout.tnt);
            c++;
        }

        if(loadout.hook > 0)
        {
            contents[c] = Loadout.HOOK_ITEM.clone();
            contents[c].setAmount(loadout.hook);
            c++;
        }

        if(loadout.web > 0)
        {
            contents[c] = Loadout.WEB_ITEM.clone();
            contents[c].setAmount(loadout.web);
            c++;
        }

        if(loadout.invis > 0)
        {
            contents[c] = Loadout.INVIS_ITEM.clone();
            contents[c].setAmount(loadout.invis);
            c++;
        }

        if(loadout.boost > 0)
        {
            contents[c] = Loadout.BOOST_ITEM.clone();
            contents[c].setAmount(loadout.boost);
            c++;
        }

        if(loadout.chikun > 0)
        {
            contents[c] = Loadout.CHIKUN_ITEM.clone();
            contents[c].setAmount((loadout.chikun));
            c++;
        }

        if(loadout.steal > 0)
        {
            contents[c] = Loadout.STEAL_ITEM.clone();
            contents[c].setAmount((loadout.steal));
        }

        return contents;
    }
}
