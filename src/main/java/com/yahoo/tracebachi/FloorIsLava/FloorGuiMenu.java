package com.yahoo.tracebachi.FloorIsLava;

import java.util.Arrays;
import java.util.Collections;
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

public class FloorGuiMenu implements Listener {
	private FloorArena arena;

	private ItemStack leaveItem;
	private ItemStack watchItem;
	private ItemStack helpItem;
	private ItemStack joinItem;
	
	private ItemStack points;
	private ItemStack tnt;
	private ItemStack hook;
	private ItemStack web;
	private ItemStack invis;
	private ItemStack boost;

	public FloorGuiMenu(FloorArena arena)
	{
		this.arena = arena;
		createMenuItems();
	}

	public void showTo(Player player)
	{
		if (!arena.getLoadouts().containsKey(player.getName()))
		{
			HashMap<ItemStack, Integer> temp = new HashMap<>();
			temp.put(arena.getDefaultLoadout().get(0), 1);
			temp.put(arena.getDefaultLoadout().get(1), 1);
			temp.put(arena.getDefaultLoadout().get(2), 1);
			temp.put(arena.getDefaultLoadout().get(3), 1);
			temp.put(arena.getDefaultLoadout().get(4), 1);
			arena.getLoadouts().put(player.getName(), temp);
			arena.getLoadoutPoints().put(player.getName(), 0);
		}
		
		HashMap<ItemStack, Integer> ld = arena.getLoadouts().get(player.getName());
		List<ItemStack> def = arena.getDefaultLoadout();
		
		int tntAmount = ld.get(def.get(0));
		int hookAmount = ld.get(def.get(1));
		int webAmount = ld.get(def.get(2));
		int invisAmount = ld.get(def.get(3));
		int boostAmount = ld.get(def.get(4));
		int pointsAmount = arena.getLoadoutPoints().get(player.getName());
		
		ItemStack points = new ItemStack(this.points);
		ItemStack tnt = new ItemStack(this.tnt);
		ItemStack hook = new ItemStack(this.hook);
		ItemStack web = new ItemStack(this.web);
		ItemStack invis = new ItemStack(this.invis);
		ItemStack boost = new ItemStack(this.boost);

		Inventory inventory = Bukkit.createInventory(null, 27, "Floor Is Lava Menu");
		
		ItemMeta meta = joinItem.getItemMeta();
		String hasStarted = arena.hasStarted() ? ChatColor.RED + "Started" : ChatColor.GREEN + "Waiting";

		meta.setLore(Arrays.asList(ChatColor.YELLOW + "Status: " + hasStarted, ChatColor.YELLOW + "Wager: " + ChatColor.GREEN + Integer.toString(arena.getWager())));
		joinItem.setItemMeta(meta);

		tnt.setAmount(tntAmount);
		hook.setAmount(hookAmount);
		web.setAmount(webAmount);
		invis.setAmount(invisAmount);
		boost.setAmount(boostAmount);
		points.setAmount(pointsAmount);

		inventory.setItem(2, joinItem);
		inventory.setItem(3, leaveItem);
		inventory.setItem(5, watchItem);
		inventory.setItem(6, helpItem);
		
		
		inventory.setItem(13, points);
		inventory.setItem(20, tnt);
		inventory.setItem(21, hook);
		inventory.setItem(22, web);
		inventory.setItem(23, invis);
		inventory.setItem(24, boost);

		player.openInventory(inventory);
	}

	public void createMenuItems()
	{
		this.joinItem = new ItemStack(Material.LEATHER_CHESTPLATE);
		this.leaveItem = new ItemStack(Material.LEATHER_LEGGINGS);
		this.watchItem = new ItemStack(Material.EYE_OF_ENDER);
		this.helpItem = new ItemStack(Material.MAP);
		
		this.tnt = new ItemStack(Material.TNT);
		this.hook = new ItemStack(Material.TRIPWIRE_HOOK);
		this.web = new ItemStack(Material.WEB);
		this.invis = new ItemStack(Material.BLAZE_ROD);
		this.boost = new ItemStack(Material.FEATHER);
		this.points = new ItemStack(Material.EMERALD);
		
		ItemMeta meta = joinItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Join");
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to join Floor Is Lava"));
		joinItem.setItemMeta(meta);

		meta = leaveItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Leave");
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to leave Floor Is Lava"));
		leaveItem.setItemMeta(meta);

		
		meta = watchItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Watch");
		meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to watch other players"));
		watchItem.setItemMeta(meta);

		
		meta = helpItem.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Menu");
		meta.setLore(Arrays.asList(new String[] { ChatColor.WHITE + "  /floor",
						ChatColor.YELLOW + "Wagering Money",
						ChatColor.WHITE + "  /floor wager [amount]",
						ChatColor.YELLOW + "Arena Status",
						ChatColor.WHITE + "  /floor count",}));
		helpItem.setItemMeta(meta);

		meta = tnt.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Throwing TNT ");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Throw ignited tnt at players by right clicking it!",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		tnt.setItemMeta(meta);

		meta = hook.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Player Launcher");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Launcher players in the air by right clicking them!", 
					ChatColor.GRAY + "Maximum distance from player: 6 blocks", 
					ChatColor.GRAY + "Minimum distance from player: 2 blocks",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		hook.setItemMeta(meta);

		meta = web.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Webber");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Create a box of webs around a player by right clicking them!", 
					ChatColor.GRAY + "Maximum distance from player: 15 blocks", 
					ChatColor.GRAY + "Minimum distance from player: 2 blocks",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		web.setItemMeta(meta);

		meta = hook.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Rod of Invisibility");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Become invisible and sneak up on your opponents!",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		invis.setItemMeta(meta);

		meta = boost.getItemMeta();
		meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Boost");
		meta.setLore(Arrays.asList(ChatColor.WHITE + "Launch yourself in the air to get away from danger!",
					ChatColor.YELLOW + "Left Click: Add",
					ChatColor.YELLOW + "Right Click: Remove"));
		boost.setItemMeta(meta);

		meta = points.getItemMeta();
		meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Loadout Points");
		points.setItemMeta(meta);
		
	}

	@EventHandler
	public void onPlayerInteract(InventoryClickEvent event)
	{
		Inventory inventory = event.getInventory();
		if (!inventory.getName().equals("Floor Is Lava Menu"))
			return;
		Player player = (Player) event.getWhoClicked();
		String playerName = player.getName();
		
		ItemStack clickedItem = event.getCurrentItem();

		HashMap<ItemStack, Integer> ld = arena.getLoadouts().get(playerName);
		List<ItemStack> def = arena.getDefaultLoadout();
		
		int tntAmount = ld.get(def.get(0));
		int hookAmount = ld.get(def.get(1));
		int webAmount = ld.get(def.get(2));
		int invisAmount = ld.get(def.get(3));
		int boostAmount = ld.get(def.get(4));
		int points = arena.getLoadoutPoints().get(playerName);

		event.setCancelled(true);
		
		/* Menu Items */
		if (matchesItemStack(this.joinItem, clickedItem))
		{
			player.closeInventory();
			player.sendMessage(arena.add(player));
			return;
		}
		else if (matchesItemStack(this.leaveItem, clickedItem))
		{
			player.closeInventory();
			player.sendMessage(this.arena.remove(player));
			return;
		}
		else if (matchesItemStack(this.watchItem, clickedItem))
		{
			player.closeInventory();
			player.sendMessage(FloorArena.GOOD + "Teleporting to watch location ... ");
			player.teleport(this.arena.getWatchLocation());
			return;
		}
		else if (matchesItemStack(this.helpItem, clickedItem))
		{
			player.closeInventory();
			return;
		}
		
		
		/* Loadout Items */
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
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points - 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.9f);
			}
			else if (matchesItemStack(hook, clickedItem))
			{
				ld.put(def.get(1), hookAmount + 1);
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points - 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.9f);
			}
			else if (matchesItemStack(web, clickedItem))
			{
				ld.put(def.get(2), webAmount + 1);
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points - 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.9f);
			}
			else if (matchesItemStack(invis, clickedItem))
			{
				ld.put(def.get(3), invisAmount + 1);
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points - 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.9f);
			}
			else if (matchesItemStack(boost, clickedItem))
			{
				ld.put(def.get(4), boostAmount + 1);
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points - 1);
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
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points + 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.3f);
			}
			else if (matchesItemStack(hook, clickedItem))
			{
				ld.put(def.get(1), hookAmount - 1);
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points + 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.3f);
			}
			else if (matchesItemStack(web, clickedItem))
			{
				ld.put(def.get(2), webAmount - 1);
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points + 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.3f);
			}
			else if (matchesItemStack(invis, clickedItem))
			{
				ld.put(def.get(3), invisAmount - 1);
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points + 1);
				showTo(player);
				player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.7f, 1.3f);
			}
			else if (matchesItemStack(boost, clickedItem))
			{
				ld.put(def.get(4), boostAmount - 1);
				arena.getLoadouts().put(playerName, ld);
				arena.getLoadoutPoints().put(playerName, points + 1);
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
