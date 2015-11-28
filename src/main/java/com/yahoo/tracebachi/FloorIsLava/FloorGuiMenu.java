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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Trace Bachi (tracebachi@yahoo.com, BigBossZee) on 11/24/15.
 */
public class FloorGuiMenu implements Listener
{
    private FloorArena arena;
    private Inventory inventory;
    private ItemStack joinItem;
    private ItemStack leaveItem;
    private ItemStack watchItem;
    private ItemStack helpItem;

    public FloorGuiMenu(FloorArena arena)
    {
        this.arena = arena;
        this.inventory = Bukkit.createInventory(null, 9, "Floor Is Lava Menu");
        createMenuItems();
    }

    public void showTo(Player player)
    {
        ItemMeta meta = joinItem.getItemMeta();
        String hasStarted = arena.hasStarted() ? ChatColor.RED + "Started" : ChatColor.GREEN + "Waiting";

        meta.setLore(Arrays.asList(ChatColor.WHITE + "Click to join Floor Is Lava",
            ChatColor.YELLOW + "Status: " + hasStarted,
            ChatColor.YELLOW + "Wager: " + ChatColor.GREEN + Integer.toString(arena.getWager())));
        joinItem.setItemMeta(meta);

        inventory.setItem(0, joinItem);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onPlayerInteract(InventoryClickEvent event)
    {
        Inventory inventory = event.getInventory();

        if(!inventory.getName().equals("Floor Is Lava Menu")) { return; }

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        event.setCancelled(true);

        if(matchesItemStack(joinItem, clickedItem))
        {
            player.closeInventory();
            player.sendMessage(arena.add(player));
        }
        else if(matchesItemStack(leaveItem, clickedItem))
        {
            player.closeInventory();
            player.sendMessage(arena.remove(player));
        }
        else if(matchesItemStack(watchItem, clickedItem))
        {
            player.closeInventory();
            player.sendMessage(FloorArena.GOOD + "Teleporting to watch location ... ");
            player.teleport(arena.getWatchLocation());
        }
        else if(matchesItemStack(helpItem, clickedItem))
        {
            player.closeInventory();
        }
    }

    private void createMenuItems()
    {
        ItemMeta meta;

        this.joinItem = new ItemStack(Material.LEATHER_CHESTPLATE);
        meta = joinItem.getItemMeta();
        meta.setDisplayName("" + ChatColor.BOLD + ChatColor.YELLOW + "Join");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to join Floor Is Lava"));
        joinItem.setItemMeta(meta);

        this.leaveItem = new ItemStack(Material.LEATHER_LEGGINGS);
        meta = leaveItem.getItemMeta();
        meta.setDisplayName("" + ChatColor.BOLD + ChatColor.YELLOW + "Leave");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to leave Floor Is Lava"));
        leaveItem.setItemMeta(meta);

        this.watchItem = new ItemStack(Material.EYE_OF_ENDER);
        meta = watchItem.getItemMeta();
        meta.setDisplayName("" + ChatColor.BOLD + ChatColor.YELLOW + "Watch");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to watch other players"));
        watchItem.setItemMeta(meta);

        this.helpItem = new ItemStack(Material.MAP);
        meta = helpItem.getItemMeta();
        meta.setDisplayName("" + ChatColor.YELLOW + "Menu");
        meta.setLore(Arrays.asList(
            ChatColor.WHITE + "  /floor",
            ChatColor.YELLOW + "Wagering Money", ChatColor.WHITE + "  /floor wager [amount]",
            ChatColor.YELLOW + "Arena Status", ChatColor.WHITE + "  /floor count"));
        helpItem.setItemMeta(meta);

        inventory.setItem(0, joinItem);
        inventory.setItem(1, leaveItem);
        inventory.setItem(2, watchItem);
        inventory.setItem(8, helpItem);
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
