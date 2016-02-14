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
	private ItemStack leaveItem;
	private ItemStack watchItem;
	private ItemStack helpItem;
	private ItemStack statusItem;
	private ItemStack kit1;
	private ItemStack kit2;
	private ItemStack kit3;

	public FloorGuiMenu(FloorArena arena)
	{
		this.arena = arena;
	}

	public void showTo(Player player)
	{
		String name = this.arena.getKits().get(player) != null ? ((FloorKits) this.arena.getKits().get(player)).getName() : "";
		Inventory inventory = Bukkit.createInventory(null, 18, "Floor Is Lava Menu");
		createKitItems(inventory);
		createMenuItems(inventory);

		ItemMeta meta = statusItem.getItemMeta();
		String hasStarted = arena.hasStarted() ? ChatColor.RED + "Started" : ChatColor.GREEN + "Waiting";

		meta.setLore(Arrays.asList(ChatColor.YELLOW + "Status: " + hasStarted, ChatColor.YELLOW + "Wager: " + ChatColor.GREEN + Integer.toString(arena.getWager())));
		statusItem.setItemMeta(meta);
		inventory.setItem(5, statusItem);

		if (name.equals("Potatonator"))
		{
			this.kit1.setType(Material.BAKED_POTATO);
			inventory.setItem(11, this.kit1);
		}
		else if (name.equals("Potato Assassin"))
		{
			this.kit2.setType(Material.BAKED_POTATO);
			inventory.setItem(13, this.kit2);
		}
		else if (name.equals("Potato Wizard"))
		{
			this.kit3.setType(Material.BAKED_POTATO);
			inventory.setItem(15, this.kit3);
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
		if (matchesItemStack(this.leaveItem, clickedItem))
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
		else if (matchesItemStack(this.helpItem, clickedItem))
		{
			player.closeInventory();
		}
		else if (matchesItemStack(this.kit1, clickedItem))
		{
			player.closeInventory();
			this.arena.getKits().put(player, this.arena.getPotatonator());
			player.sendMessage(FloorArena.GOOD + this.arena.getPotatonator().getName() + " class equipped!");
		}
		else if (matchesItemStack(this.kit2, clickedItem))
		{
			player.closeInventory();
			this.arena.getKits().put(player, this.arena.getPotatoAssassin());
			player.sendMessage(FloorArena.GOOD + this.arena.getPotatoAssassin().getName() + " class equipped!");
		}
		else if (matchesItemStack(this.kit3, clickedItem))
		{
			player.closeInventory();
			this.arena.getKits().put(player, this.arena.getPotatoWizard());
			player.sendMessage(FloorArena.GOOD + this.arena.getPotatoWizard().getName() + " class equipped!");
		}
	}

	private void createMenuItems(Inventory inventory)
	{
		this.leaveItem = new ItemStack(Material.LEATHER_LEGGINGS);
		ItemMeta meta = this.leaveItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Leave");
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to leave Floor Is Lava"));
		this.leaveItem.setItemMeta(meta);

		this.watchItem = new ItemStack(Material.EYE_OF_ENDER);
		meta = this.watchItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Watch");
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to watch other players"));
		this.watchItem.setItemMeta(meta);

		this.helpItem = new ItemStack(Material.MAP);
		meta = this.helpItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "Menu");
		meta.setLore(Arrays.asList(new String[] { ChatColor.WHITE + "  /floor",
						ChatColor.YELLOW + "Wagering Money",
						ChatColor.WHITE + "  /floor wager [amount]",
						ChatColor.YELLOW + "Arena Status",
						ChatColor.WHITE + "  /floor count",
						ChatColor.YELLOW + "Join Arena",
						ChatColor.WHITE + "  /floor join" }));
		this.helpItem.setItemMeta(meta);

		this.statusItem = new ItemStack(Material.PAPER);
		meta = this.statusItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Status");
		this.statusItem.setItemMeta(meta);

		inventory.setItem(2, this.leaveItem);
		inventory.setItem(3, this.watchItem);
		inventory.setItem(5, this.statusItem);
		inventory.setItem(6, this.helpItem);
		inventory.setItem(11, this.kit1);
		inventory.setItem(13, this.kit2);
		inventory.setItem(15, this.kit3);
	}

	private void createKitItems(Inventory inventory)
	{
		this.kit1 = new ItemStack(Material.POTATO_ITEM);
		this.kit2 = new ItemStack(Material.POTATO_ITEM);
		this.kit3 = new ItemStack(Material.POTATO_ITEM);

		ItemMeta kit1Meta = this.kit1.getItemMeta();
		ItemMeta kit2Meta = this.kit2.getItemMeta();
		ItemMeta kit3Meta = this.kit3.getItemMeta();

		kit1Meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + this.arena.getPotatonator().getName());
		kit2Meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + this.arena.getPotatoAssassin().getName());
		kit3Meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + this.arena.getPotatoWizard().getName());

		kit1Meta.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Throwing TNT: " + ChatColor.GREEN + "3",
						ChatColor.GRAY + "Player Launcher: " + ChatColor.GREEN + "2",
						ChatColor.GRAY + "De-Webber: " + ChatColor.DARK_RED + "0",
						ChatColor.GRAY + "Rod of Invisibility: " + ChatColor.DARK_RED + "0",
						ChatColor.GRAY + "Boost: " + ChatColor.DARK_RED + "0" }));

		kit2Meta.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Throwing TNT: " + ChatColor.GREEN + "1",
						ChatColor.GRAY + "Player Launcher: " + ChatColor.GREEN + "2",
						ChatColor.GRAY + "De-Webber: " + ChatColor.DARK_RED + "0",
						ChatColor.GRAY + "Rod of Invisibility: " + ChatColor.GREEN + "2",
						ChatColor.GRAY + "Boost: " + ChatColor.DARK_RED + "0" }));

		kit3Meta.setLore(Arrays.asList(new String[] { ChatColor.GRAY + "Throwing TNT: " + ChatColor.DARK_RED + "0",
						ChatColor.GRAY + "Player Launcher: " + ChatColor.DARK_RED + "0",
						ChatColor.GRAY + "De-Webber: " + ChatColor.GREEN + "2",
						ChatColor.GRAY + "Rod of Invisibility: " + ChatColor.GREEN + "1",
						ChatColor.GRAY + "Boost: " + ChatColor.GREEN + "2" }));

		this.kit1.setItemMeta(kit1Meta);
		this.kit2.setItemMeta(kit2Meta);
		this.kit3.setItemMeta(kit3Meta);
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
