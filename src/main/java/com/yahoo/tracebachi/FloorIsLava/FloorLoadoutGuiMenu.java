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
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FloorLoadoutGuiMenu implements Listener {
	private FloorIsLavaPlugin plugin;
	private FloorArena arena;

	private ItemStack points;
	private ItemStack exit;
	private ItemStack tnt;
	private ItemStack hook;
	private ItemStack web;
	private ItemStack invis;
	private ItemStack boost;

	public FloorLoadoutGuiMenu(FloorIsLavaPlugin plugin, FloorArena arena)
	{
		this.plugin = plugin;
		this.arena = arena;
		createMenuItems();
	}

	public void showTo(Player player)
	{
		HashMap<ItemStack, Integer> ld = arena.getLoadouts().get(player);
		List<ItemStack> def = arena.getDefaultLoadout();
		
		int tntAmount = ld.get(def.get(0));
		int hookAmount = ld.get(def.get(1));
		int webAmount = ld.get(def.get(2));
		int invisAmount = ld.get(def.get(3));
		int boostAmount = ld.get(def.get(4));
		int pointsAmount = arena.getLoadoutPoints().get(player);
		
		ItemStack points = new ItemStack(this.points);
		ItemStack tnt = new ItemStack(this.tnt);
		ItemStack hook = new ItemStack(this.hook);
		ItemStack web = new ItemStack(this.web);
		ItemStack invis = new ItemStack(this.invis);
		ItemStack boost = new ItemStack(this.boost);

		Inventory inventory = Bukkit.createInventory(null, 18, "Floor Is Lava Loadout Menu");

		ItemMeta pointsMeta = points.getItemMeta();
		pointsMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Inventory Points");
		points.setItemMeta(pointsMeta);

		tnt.setAmount(tntAmount);
		hook.setAmount(hookAmount);
		web.setAmount(webAmount);
		invis.setAmount(invisAmount);
		boost.setAmount(boostAmount);
		points.setAmount(pointsAmount);

		inventory.setItem(0, exit);
		inventory.setItem(8, points);
		inventory.setItem(11, tnt);
		inventory.setItem(12, hook);
		inventory.setItem(13, web);
		inventory.setItem(14, invis);
		inventory.setItem(15, boost);

		player.openInventory(inventory);
	}

	public void createMenuItems()
	{
		this.tnt = new ItemStack(Material.TNT);
		this.hook = new ItemStack(Material.TRIPWIRE_HOOK);
		this.web = new ItemStack(Material.WEB);
		this.invis = new ItemStack(Material.BLAZE_ROD);
		this.boost = new ItemStack(Material.FEATHER);
		this.points = new ItemStack(Material.EMERALD);
		this.exit = new ItemStack(Material.IRON_DOOR);

		ItemMeta tntMeta = tnt.getItemMeta();
		tntMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Throwing TNT ");
		tntMeta.setLore(Arrays.asList(ChatColor.WHITE + "Throw ignited tnt at players by right clicking it!",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		tnt.setItemMeta(tntMeta);

		ItemMeta hookMeta = hook.getItemMeta();
		hookMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Player Launcher");
		hookMeta.setLore(Arrays.asList(ChatColor.WHITE + "Launcher players in the air by right clicking them!", 
					ChatColor.GRAY + "Maximum distance from player: 6 blocks", 
					ChatColor.GRAY + "Minimum distance from player: 2 blocks",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		hook.setItemMeta(hookMeta);

		ItemMeta webMeta = web.getItemMeta();
		webMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Webber");
		webMeta.setLore(Arrays.asList(ChatColor.WHITE + "Create a box of webs around a player by right clicking them!", 
					ChatColor.GRAY + "Maximum distance from player: 15 blocks", 
					ChatColor.GRAY + "Minimum distance from player: 2 blocks",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		web.setItemMeta(webMeta);

		ItemMeta invisMeta = hook.getItemMeta();
		invisMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Rod of Invisibility");
		invisMeta.setLore(Arrays.asList(ChatColor.WHITE + "Become invisible and sneak up on your opponents!",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		invis.setItemMeta(invisMeta);

		ItemMeta boostMeta = boost.getItemMeta();
		boostMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Boost");
		boostMeta.setLore(Arrays.asList(ChatColor.WHITE + "Launch yourself in the air to get away from danger!",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		boost.setItemMeta(boostMeta);

		ItemMeta pointsMeta = points.getItemMeta();
		pointsMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Inventory Points");
		points.setItemMeta(pointsMeta);
		
		ItemMeta exitMeta = exit.getItemMeta();
		exitMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Exit");
		exitMeta.setLore(Arrays.asList("", ChatColor.GRAY + "<---"));
		exit.setItemMeta(exitMeta);
	}

	@EventHandler
	public void onPlayerInteract(InventoryClickEvent event)
	{
		Inventory inventory = event.getInventory();
		if (!inventory.getName().equals("Floor Is Lava Loadout Menu"))
			return;
		Player player = (Player) event.getWhoClicked();
		ItemStack clickedItem = event.getCurrentItem();

		HashMap<ItemStack, Integer> ld = arena.getLoadouts().get(player);
		List<ItemStack> def = arena.getDefaultLoadout();
		
		int tntAmount = ld.get(def.get(0));
		int hookAmount = ld.get(def.get(1));
		int webAmount = ld.get(def.get(2));
		int invisAmount = ld.get(def.get(3));
		int boostAmount = ld.get(def.get(4));
		int points = arena.getLoadoutPoints().get(player);

		event.setCancelled(true);
		if (matchesItemStack(exit, clickedItem))
		{
			plugin.getMenu().showTo(player);
			return;
		}
		if (event.getClick().equals(ClickType.LEFT))
		{
			if (clickedItem != null && points == 0)
			{
				player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
				return;
			}
			if (matchesItemStack(tnt, clickedItem))
			{
				ld.put(def.get(0), tntAmount + 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points - 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.9f);
			}
			else if (matchesItemStack(hook, clickedItem))
			{
				ld.put(def.get(1), hookAmount + 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points - 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.9f);
			}
			else if (matchesItemStack(web, clickedItem))
			{
				ld.put(def.get(2), webAmount + 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points - 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.9f);
			}
			else if (matchesItemStack(invis, clickedItem))
			{
				ld.put(def.get(3), invisAmount + 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points - 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.9f);
			}
			else if (matchesItemStack(boost, clickedItem))
			{
				ld.put(def.get(4), boostAmount + 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points - 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.9f);
			}
		}
		else if (event.getClick().equals(ClickType.RIGHT))
		{
			if (clickedItem.getAmount() <= 0)
			{
				player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
				return;
			}
			if (matchesItemStack(tnt, clickedItem))
			{
				ld.put(def.get(0), tntAmount - 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points + 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.3f);
			}
			else if (matchesItemStack(hook, clickedItem))
			{
				ld.put(def.get(1), hookAmount - 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points + 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.3f);
			}
			else if (matchesItemStack(web, clickedItem))
			{
				ld.put(def.get(2), webAmount - 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points + 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.3f);
			}
			else if (matchesItemStack(invis, clickedItem))
			{
				ld.put(def.get(3), invisAmount - 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points + 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.3f);
			}
			else if (matchesItemStack(boost, clickedItem))
			{
				ld.put(def.get(4), boostAmount - 1);
				arena.getLoadouts().put(player, ld);
				arena.getLoadoutPoints().put(player, points + 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.3f);
			}
		}
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
