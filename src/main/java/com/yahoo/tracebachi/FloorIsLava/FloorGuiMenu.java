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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

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

/**
 * Created by Trace Bachi (tracebachi@yahoo.com, BigBossZee) on 11/24/15.
 */
public class FloorGuiMenu implements Listener {
	private FloorArena arena;
	private FloorLoadoutGuiMenu loadout;
	private ItemStack leaveItem;
	private ItemStack watchItem;
	private ItemStack helpItem;
	private ItemStack joinItem;
	private ItemStack kitItem;

	public FloorGuiMenu(FloorArena arena, FloorLoadoutGuiMenu loadoutMenu)
	{
		this.arena = arena;
		this.loadout = loadoutMenu;
	}

	public void showTo(Player player)
	{
		Inventory inventory = Bukkit.createInventory(null, 9, "Floor Is Lava Menu");
		createMenuItems(inventory);
		HashMap<ItemStack, Integer> stacks = arena.getLoadouts().get(player);

		ItemMeta meta = joinItem.getItemMeta();
		String hasStarted = arena.hasStarted() ? ChatColor.RED + "Started" : ChatColor.GREEN + "Waiting";

		meta.setLore(Arrays.asList(ChatColor.YELLOW + "Status: " + hasStarted, ChatColor.YELLOW + "Wager: " + ChatColor.GREEN + Integer.toString(arena.getWager())));
		joinItem.setItemMeta(meta);
		inventory.setItem(0, joinItem);

		meta = kitItem.getItemMeta();
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Click to edit your loadout", 
					ChatColor.GRAY + "Current Loadout: " + ChatColor.GREEN + (stacks == null ? "Default" : stacks.equals(arena.getDefaultLoadout()) ? "Default" : "Custom"),
					"",
					ChatColor.AQUA + "-Loadout-",
					ChatColor.GRAY + "Throwing TNT: " + (stacks == null ? "1" : stacks.get(arena.getDefaultLoadout().get(0))),
					ChatColor.GRAY + "Player Launcher: " + (stacks == null ? "1" : stacks.get(arena.getDefaultLoadout().get(1))),
					ChatColor.GRAY + "Webber: " + (stacks == null ? "1" : stacks.get(arena.getDefaultLoadout().get(2))),
					ChatColor.GRAY + "Rod Of Invisibility: " + (stacks == null ? "1" : stacks.get(arena.getDefaultLoadout().get(3))),
					ChatColor.GRAY + "Boost: " + (stacks == null ? "1" : stacks.get(arena.getDefaultLoadout().get(4)))));
		kitItem.setItemMeta(meta);
		inventory.setItem(3, kitItem);
		
		if (!arena.getLoadouts().containsKey(player))
		{
			HashMap<ItemStack, Integer> temp = new HashMap<>();
			temp.put(arena.getDefaultLoadout().get(0), 1);
			temp.put(arena.getDefaultLoadout().get(1), 1);
			temp.put(arena.getDefaultLoadout().get(2), 1);
			temp.put(arena.getDefaultLoadout().get(3), 1);
			temp.put(arena.getDefaultLoadout().get(4), 1);
			arena.getLoadouts().put(player, temp);
			arena.getLoadoutPoints().put(player, 0);
		}
		player.openInventory(inventory);
	}

	@EventHandler
	public void onPlayerInteract(InventoryClickEvent event)
	{
		Inventory inventory = event.getInventory();
		if (!inventory.getName().equals("Floor Is Lava Menu"))
		{
			return;
		}
		Player player = (Player) event.getWhoClicked();
		ItemStack clickedItem = event.getCurrentItem();

		event.setCancelled(true);
		if (matchesItemStack(this.joinItem, clickedItem))
		{
			player.closeInventory();
			player.sendMessage(arena.add(player));
		}
		else if (matchesItemStack(this.leaveItem, clickedItem))
		{
			player.closeInventory();
			player.sendMessage(this.arena.remove(player));
		}
		else if (matchesItemStack(this.watchItem, clickedItem))
		{
			player.closeInventory();
			player.sendMessage(FloorArena.GOOD + "Teleporting to watch location ... ");
			player.teleport(this.arena.getWatchLocation());
		}
		else if (matchesItemStack(this.kitItem, clickedItem))
		{
			player.closeInventory();
			loadout.showTo(player);
		}
		else if (matchesItemStack(this.helpItem, clickedItem))
		{
			player.closeInventory();
		}
	}

	private void createMenuItems(Inventory inventory)
	{

		this.joinItem = new ItemStack(Material.LEATHER_CHESTPLATE);
		ItemMeta meta = this.joinItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Join");
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to join Floor Is Lava"));
		this.joinItem.setItemMeta(meta);

		this.leaveItem = new ItemStack(Material.LEATHER_LEGGINGS);
		meta = this.leaveItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Leave");
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to leave Floor Is Lava"));
		this.leaveItem.setItemMeta(meta);

		this.watchItem = new ItemStack(Material.EYE_OF_ENDER);
		meta = this.watchItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Watch");
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to watch other players"));
		this.watchItem.setItemMeta(meta);

		this.kitItem = new ItemStack(Material.DIAMOND_SWORD);
		meta = this.kitItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Loadout Editor");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Click to edit your loadout", ChatColor.GRAY + "Current Loadout: " + ChatColor.GREEN + "Default"));
		this.kitItem.setItemMeta(meta);

		this.helpItem = new ItemStack(Material.MAP);
		meta = this.helpItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Menu");
		meta.setLore(Arrays.asList(new String[] { ChatColor.WHITE + "  /floor",
						ChatColor.YELLOW + "Wagering Money",
						ChatColor.WHITE + "  /floor wager [amount]",
						ChatColor.YELLOW + "Arena Status",
						ChatColor.WHITE + "  /floor count",}));
		this.helpItem.setItemMeta(meta);

		inventory.setItem(0, this.joinItem);
		inventory.setItem(1, this.leaveItem);
		inventory.setItem(2, this.watchItem);
		inventory.setItem(3, this.kitItem);
		inventory.setItem(8, this.helpItem);
	}

	private boolean matchesItemStack(ItemStack original, ItemStack input)
	{
		if ((original == null) || (input == null))
		{
			return false;
		}
		if (input.getType() == original.getType())
		{
			boolean originalHasMeta = original.hasItemMeta();
			boolean inputHasMeta = input.hasItemMeta();
			if ((originalHasMeta) && (inputHasMeta))
			{
				ItemMeta originalMeta = original.getItemMeta();
				ItemMeta inputMeta = input.getItemMeta();
				if ((originalMeta.hasDisplayName()) && (inputMeta.hasDisplayName()))
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
