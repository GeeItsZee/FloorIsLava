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
package com.gmail.tracebachi.FloorIsLava.Gui;

import com.gmail.tracebachi.FloorIsLava.Arena.Arena;
import com.gmail.tracebachi.FloorIsLava.Utils.Loadout;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

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

        if(!inventory.getName().equals("Floor Is Lava Menu")) { return; }

        if(clickedItem == null) { return; }

        event.setCancelled(true);

        Map<String, Loadout> loadoutMap = arena.getLoadoutMap();
        Player player = (Player) event.getWhoClicked();
        String name = player.getName();
        Loadout loadout = loadoutMap.get(name);

        if(loadout == null)
        {
            loadout = new Loadout();
            loadoutMap.put(name, loadout);
        }

		/* Menu Items */
        if(matchesItemStack(FloorGuiMenu.JOIN_ITEM, clickedItem))
        {
            player.closeInventory();
            arena.join(player);
            return;
        }
        else if(matchesItemStack(FloorGuiMenu.LEAVE_ITEM, clickedItem))
        {
            player.closeInventory();
            arena.leave(player);
            return;
        }
        else if(matchesItemStack(FloorGuiMenu.WATCH_ITEM, clickedItem))
        {
            player.closeInventory();
            arena.watch(player);
            return;
        }
        else if(matchesItemStack(FloorGuiMenu.HELP_ITEM, clickedItem))
        {
            player.closeInventory();
            return;
        }

		/* Loadout Items */
        ClickType clickType = event.getClick();
        int maxPoints = (arena.getBooster().isActive() ? 10 : 5);
        int change;

        if(clickType.equals(ClickType.LEFT) || clickType.equals(ClickType.DOUBLE_CLICK))
        {
            change = 1;
        }
        else
        {
            change = -1;
        }

        if(change == 1 && loadout.countSum() == maxPoints)
        {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            return;
        }
        else if(change == -1 && loadout.countSum() == 0)
        {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
            return;
        }

        if(matchesItemStack(FloorGuiMenu.TNT_ITEM, clickedItem))
        {
            int oldCount = loadout.tnt;
            loadout.tnt = Math.max(0, loadout.tnt + change);
            playSoundOnCondition(player, loadout.tnt != oldCount);

            int pointsAmount = maxPoints - loadout.countSum();
            updateItemStackAmount(inventory, FloorGuiMenu.POINTS_ITEM, 13, pointsAmount);
            updateItemStackAmount(inventory, FloorGuiMenu.TNT_ITEM, 19, loadout.tnt);
        }
        else if(matchesItemStack(FloorGuiMenu.HOOK_ITEM, clickedItem))
        {
            int oldCount = loadout.hook;
            loadout.hook = Math.max(0, loadout.hook + change);
            playSoundOnCondition(player, loadout.hook != oldCount);

            int pointsAmount = maxPoints - loadout.countSum();
            updateItemStackAmount(inventory, FloorGuiMenu.POINTS_ITEM, 13, pointsAmount);
            updateItemStackAmount(inventory, FloorGuiMenu.HOOK_ITEM, 20, loadout.hook);
        }
        else if(matchesItemStack(FloorGuiMenu.WEB_ITEM, clickedItem))
        {
            int oldCount = loadout.web;
            loadout.web = Math.max(0, loadout.web + change);
            playSoundOnCondition(player, loadout.web != oldCount);

            int pointsAmount = maxPoints - loadout.countSum();
            updateItemStackAmount(inventory, FloorGuiMenu.POINTS_ITEM, 13, pointsAmount);
            updateItemStackAmount(inventory, FloorGuiMenu.WEB_ITEM, 21, loadout.web);
        }
        else if(matchesItemStack(FloorGuiMenu.INVIS_ITEM, clickedItem))
        {
            int oldCount = loadout.invis;
            loadout.invis = Math.max(0, loadout.invis + change);
            playSoundOnCondition(player, loadout.invis != oldCount);

            int pointsAmount = maxPoints - loadout.countSum();
            updateItemStackAmount(inventory, FloorGuiMenu.POINTS_ITEM, 13, pointsAmount);
            updateItemStackAmount(inventory, FloorGuiMenu.INVIS_ITEM, 22, loadout.invis);
        }
        else if(matchesItemStack(FloorGuiMenu.BOOST_ITEM, clickedItem))
        {
            int oldCount = loadout.boost;
            loadout.boost = Math.max(0, loadout.boost + change);
            playSoundOnCondition(player, loadout.boost != oldCount);

            int pointsAmount = maxPoints - loadout.countSum();
            updateItemStackAmount(inventory, FloorGuiMenu.POINTS_ITEM, 13, pointsAmount);
            updateItemStackAmount(inventory, FloorGuiMenu.BOOST_ITEM, 23, loadout.boost);
        }
        else if(matchesItemStack(FloorGuiMenu.CHIKUN_ITEM, clickedItem))
        {
            int oldCount = loadout.chikun;
            loadout.chikun = Math.max(0, loadout.chikun + change);
            playSoundOnCondition(player, loadout.chikun != oldCount);

            int pointsAmount = maxPoints - loadout.countSum();
            updateItemStackAmount(inventory, FloorGuiMenu.POINTS_ITEM, 13, pointsAmount);
            updateItemStackAmount(inventory, FloorGuiMenu.CHIKUN_ITEM, 24, loadout.chikun);
        }
        else if(matchesItemStack(FloorGuiMenu.STEAL_ITEM, clickedItem))
        {
            int oldCount = loadout.steal;
            loadout.steal = Math.max(0, loadout.steal + change);
            playSoundOnCondition(player, loadout.steal != oldCount);

            int pointsAmount = maxPoints - loadout.countSum();
            updateItemStackAmount(inventory, FloorGuiMenu.POINTS_ITEM, 13, pointsAmount);
            updateItemStackAmount(inventory, FloorGuiMenu.STEAL_ITEM, 25, loadout.steal);
        }
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

    private void playSoundOnCondition(Player player, boolean flag)
    {
        if(flag)
        {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
        }
        else
        {
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1);
        }
    }

    private void updateItemStackAmount(Inventory inventory, ItemStack itemStack,
                                       int slot, int amount)
    {
        ItemStack cloned = itemStack.clone();
        cloned.setAmount(amount);
        inventory.setItem(slot, cloned);
    }
}
