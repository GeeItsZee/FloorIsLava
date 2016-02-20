package com.gmail.tracebachi.FloorIsLava;

import com.gmail.tracebachi.FloorIsLava.UtilClasses.Loadout;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 2/18/16.
 */
public class FloorGuiMenuListener implements Listener
{
    private final Arena arena;

    public FloorGuiMenuListener(Arena arena)
    {
        this.arena = arena;
    }

    @EventHandler
    public void onPlayerInteract(InventoryClickEvent event)
    {
        Inventory inventory = event.getInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if(!inventory.getName().equals("Floor Is Lava Menu")) return;

        event.setCancelled(true);

        HashMap<String, Loadout> loadoutMap = arena.getLoadoutMap();
        Player player = (Player) event.getWhoClicked();
        String name = player.getName();
        Loadout loadout = loadoutMap.get(name);

        if(clickedItem == null) return;

        if(loadout == null)
        {
            loadout = new Loadout();
            loadout.tntCount = 1;
            loadout.hookCount = 0;
            loadout.webCount = 0;
            loadout.invisCount = 0;
            loadout.boostCount = 0;

            loadoutMap.put(name, loadout);
        }

		/* Menu Items */
        if(matchesItemStack(FloorGuiMenu.JOIN_ITEM, clickedItem))
        {
            player.closeInventory();
            arena.add(player);
            return;
        }
        else if(matchesItemStack(FloorGuiMenu.LEAVE_ITEM, clickedItem))
        {
            player.closeInventory();
            arena.remove(player);
            return;
        }
        else if(matchesItemStack(FloorGuiMenu.WATCH_ITEM, clickedItem))
        {
            player.closeInventory();
            player.sendMessage(Arena.GOOD + "Teleporting to watch location ... ");
            player.teleport(arena.getWatchLocation());
            return;
        }
        else if(matchesItemStack(FloorGuiMenu.HELP_ITEM, clickedItem))
        {
            player.closeInventory();
            return;
        }

		/* Loadout Items */
        int change = event.getClick().equals(ClickType.LEFT) ? 1 : -1;

        if(change == 1 && loadout.countSum() == 5)
        {
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            return;
        }
        else if(change == -1 && loadout.countSum() == 0)
        {
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            return;
        }

        if(matchesItemStack(FloorGuiMenu.TNT_ITEM, clickedItem))
        {
            int oldCount = loadout.tntCount;
            loadout.tntCount = Math.max(0, loadout.tntCount + change);
            updateLoadoutCounts(loadout, inventory);

            if(loadout.tntCount != oldCount)
            {
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
            }
            else
            {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            }
        }
        else if(matchesItemStack(FloorGuiMenu.HOOK_ITEM, clickedItem))
        {
            int oldCount = loadout.hookCount;
            loadout.hookCount = Math.max(0, loadout.hookCount + change);
            updateLoadoutCounts(loadout, inventory);

            if(loadout.hookCount != oldCount)
            {
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
            }
            else
            {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            }
        }
        else if(matchesItemStack(FloorGuiMenu.WEB_ITEM, clickedItem))
        {
            int oldCount = loadout.webCount;
            loadout.webCount = Math.max(0, loadout.webCount + change);
            updateLoadoutCounts(loadout, inventory);

            if(loadout.webCount != oldCount)
            {
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
            }
            else
            {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            }
        }
        else if(matchesItemStack(FloorGuiMenu.INVIS_ITEM, clickedItem))
        {
            int oldCount = loadout.invisCount;
            loadout.invisCount = Math.max(0, loadout.invisCount + change);
            updateLoadoutCounts(loadout, inventory);

            if(loadout.invisCount != oldCount)
            {
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
            }
            else
            {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            }
        }
        else if(matchesItemStack(FloorGuiMenu.BOOST_ITEM, clickedItem))
        {
            int oldCount = loadout.boostCount;
            loadout.boostCount = Math.max(0, loadout.boostCount + change);
            updateLoadoutCounts(loadout, inventory);

            if(loadout.boostCount != oldCount)
            {
                player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
            }
            else
            {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            }
        }
    }

    private void updateLoadoutCounts(Loadout loadout, Inventory inventory)
    {
        int tntAmount = 1;
        int hookAmount = 0;
        int webAmount = 0;
        int invisAmount = 0;
        int boostAmount = 0;
        int pointsAmount = 4;

        if(loadout != null)
        {
            tntAmount = loadout.tntCount;
            hookAmount = loadout.hookCount;
            webAmount = loadout.webCount;
            invisAmount = loadout.invisCount;
            boostAmount = loadout.boostCount;
            pointsAmount = 5 - loadout.countSum();
        }

        FloorGuiMenu.POINTS_ITEM.setAmount(pointsAmount);
        FloorGuiMenu.TNT_ITEM.setAmount(tntAmount);
        FloorGuiMenu.HOOK_ITEM.setAmount(hookAmount);
        FloorGuiMenu.WEB_ITEM.setAmount(webAmount);
        FloorGuiMenu.INVIS_ITEM.setAmount(invisAmount);
        FloorGuiMenu.BOOST_ITEM.setAmount(boostAmount);

        inventory.setItem(13, FloorGuiMenu.POINTS_ITEM);
        inventory.setItem(20, FloorGuiMenu.TNT_ITEM);
        inventory.setItem(21, FloorGuiMenu.HOOK_ITEM);
        inventory.setItem(22, FloorGuiMenu.WEB_ITEM);
        inventory.setItem(23, FloorGuiMenu.INVIS_ITEM);
        inventory.setItem(24, FloorGuiMenu.BOOST_ITEM);
    }

    private boolean matchesItemStack(ItemStack original, ItemStack input)
    {
        if(original == null || input == null)
        {
            return false;
        }

        if(input.getType() == original.getType())
        {
            boolean originalHasMeta = original.hasItemMeta();
            boolean inputHasMeta = input.hasItemMeta();

            if(originalHasMeta && inputHasMeta)
            {
                ItemMeta originalMeta = original.getItemMeta();
                ItemMeta inputMeta = input.getItemMeta();

                if(originalMeta.hasDisplayName() && inputMeta.hasDisplayName())
                {
                    return originalMeta.getDisplayName().equals(inputMeta.getDisplayName());
                }
            }
            else
            {
                return originalHasMeta == inputHasMeta;
            }
        }
        return false;
    }
}
