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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.yahoo.tracebachi.FloorIsLava.UtilClasses.PlayerState;
import com.yahoo.tracebachi.FloorIsLava.UtilClasses.Point;

import net.milkbowl.vault.economy.EconomyResponse;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class FloorArena implements Listener {
	public static final String GOOD = ChatColor.translateAlternateColorCodes('&', "&8[&aFIL&8]&a ");
	public static final String BAD = ChatColor.translateAlternateColorCodes('&', "&8[&cFIL&8]&c ");

	private FloorIsLavaPlugin plugin;
	private ItemStack winPrize;

	private ItemStack tnt;
	private ItemStack hook;
	private ItemStack web;
	private ItemStack invis;
	private ItemStack boost;
	private List<ItemStack> defaultLoadout = new ArrayList<ItemStack>();

	private boolean started = false;
	private boolean enabled = true;
	private BukkitTask arenaTask;
	private BukkitTask countdownTask;
	private BukkitTask kitEnableTask;
	private BukkitTask tntUseTask;
	private BukkitTask hookUseTask;
	private BukkitTask webUseTask;
	private BukkitTask invisUseTask;
	private BukkitTask boostUseTask;
	private FloorArenaBlocks arenaBlocks;
	private Location watchLocation;

	private HashMap<String, PlayerState> playing = new HashMap<>();
	private HashMap<String, HashMap<ItemStack, Integer>> loadouts = new HashMap<>();
	private HashMap<String, Integer> loadoutPoints = new HashMap<>();
	private HashMap<String, Boolean> canUseTnt = new HashMap<>();
	private HashMap<String, Boolean> canUseHook = new HashMap<>();
	private HashMap<String, Boolean> canUseWebber = new HashMap<>();
	private HashMap<String, Boolean> canUseInvis = new HashMap<>();
	private HashMap<String, Boolean> canUseBoost = new HashMap<>();
	private HashMap<String, Long> msSinceLastTNTThrow = new HashMap<>();
	private HashMap<String, Long> msSinceLastHookUse = new HashMap<>();
	private HashMap<String, Long> msSinceLastWebbing = new HashMap<>();
	private HashMap<String, Long> msSinceLastInvisUse = new HashMap<>();
	private HashMap<String, Long> msSinceLastBoostUse = new HashMap<>();
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
	private int kitEnableDelay;
	private int blocksToCancelTnt;
	private int tntUseDelay;
	private int hookUseDelay;
	private int deWebUseDelay;
	private int invisUseDelay;
	private int boostUseDelay;

	private int wager = 0;
	private int countdown = 0;
	private int elapsedTicks = 0;
	private int degradeLevel = 0;

	public FloorArena(FloorIsLavaPlugin plugin)
	{
		this.plugin = plugin;
		this.winPrize = new ItemStack(Material.POTATO_ITEM);
		this.tnt = new ItemStack(Material.TNT);
		this.hook = new ItemStack(Material.TRIPWIRE_HOOK);
		this.web = new ItemStack(Material.WEB);
		this.invis = new ItemStack(Material.BLAZE_ROD);
		this.boost = new ItemStack(Material.FEATHER);
		ItemStack[] stack = { tnt, hook, web, invis, boost };
		this.defaultLoadout.addAll(Arrays.asList(stack));

		ItemMeta winTatoMeta = winPrize.getItemMeta();
		winTatoMeta.setDisplayName(ChatColor.GOLD + "WinTato");
		List<String> lore = new ArrayList<>();
		lore.add("You won a round of FloorIsLava!");
		lore.add("--");
		lore.add("May the WinTato be with you - Zee");
		winTatoMeta.setLore(lore);
		winPrize.setItemMeta(winTatoMeta);

		ItemMeta tntMeta = tnt.getItemMeta();
		tntMeta.setDisplayName(ChatColor.DARK_RED + "\u2622" + ChatColor.GOLD + " Throwing TNT " + ChatColor.DARK_RED + "\u2622");
		tnt.setItemMeta(tntMeta);

		ItemMeta hookMeta = hook.getItemMeta();
		hookMeta.setDisplayName(ChatColor.AQUA + "Player Launcher");
		hook.setItemMeta(hookMeta);

		ItemMeta dewebMeta = web.getItemMeta();
		dewebMeta.setDisplayName(ChatColor.GREEN + "Webber");
		web.setItemMeta(dewebMeta);

		ItemMeta invisMeta = hook.getItemMeta();
		invisMeta.setDisplayName(ChatColor.GRAY + "Rod of Invisibility");
		invis.setItemMeta(invisMeta);

		ItemMeta boostMeta = boost.getItemMeta();
		boostMeta.setDisplayName(ChatColor.YELLOW + "Boost");
		boost.setItemMeta(boostMeta);
	}

	public String addWager(int amount, String name)
	{
		EconomyResponse response = plugin.getEconomy().bankWithdraw(name, amount);

		if (response.transactionSuccess())
		{
			wager += amount;
			broadcast(GOOD + "+$" + amount + " by " + name + " ( = $" + wager + " )", null);

			return GOOD + "You added $" + amount + " to FloorIsLava ( = $" + wager + " )";
		}
		else
		{
			return BAD + "You do not have enough funds to wager that amount.";
		}
	}

	public String add(Player player)
	{
		if (!enabled)
		{
			return BAD + "Unable to join. FloorIsLava is currently disabled.";
		}

		if (started)
		{
			return BAD + "Unable to join. FloorIsLava has already begun.";
		}

		String playerName = player.getName();
		if (playing.containsKey(playerName))
		{
			return BAD + "You are already waiting to play FloorIsLava.";
		}

		playing.put(playerName, null);
		watching.add(playerName);

		broadcast(GOOD + playerName + " has joined.", new String[] { playerName });
		resetCoundown();
		return GOOD + "You have joined FloorIsLava.";
	}

	public String remove(Player player)
	{
		String name = player.getName();
		if (!playing.containsKey(name) && !watching.contains(name))
			return BAD + "You are not part of FloorIsLava.";

		PlayerState state = playing.remove(name);
		watching.remove(name);

		if (state != null)
		{
			state.restoreInventory(player);
			state.restoreLocation(player);
			state.restoreGameMode(player);
			broadcast(BAD + name + " has left.", null);

			if (watching.size() < minimumPlayers)
			{
				resetCoundown();
			}

			return GOOD + "You have left FloorIsLava.";
		}
		else
		{
			if (started)
			{
				return BAD + "You are not part of FloorIsLava.";
			}
			else
			{
				if (watching.size() < minimumPlayers)
				{
					broadcast(BAD + name + " has left.", null);
					resetCoundown();
				}
				return GOOD + "You have left FloorIsLava.";
			}
		}
	}

	public void start()
	{
		if (started)
		{
			throw new IllegalStateException("start() was called while arena has already been started!");
		}

		Iterator<Map.Entry<String, PlayerState>> iter = playing.entrySet().iterator();
		World world = Bukkit.getWorld(worldName);

		arenaBlocks.save(world);

		while (iter.hasNext())
		{
			Map.Entry<String, PlayerState> entry = iter.next();
			Player player = Bukkit.getPlayer(entry.getKey());

			if (player != null && player.isOnline())
			{
				PlayerState playerState = new PlayerState();
				playerState.save(player);
				playing.put(entry.getKey(), playerState);

				for (PotionEffect e : player.getActivePotionEffects())
				{
					player.removePotionEffect(e.getType());
				}

				for (String command : prestartCommands)
				{
					Bukkit.getServer().dispatchCommand(player, command);
				}
				ItemStack[] contents;
				if (loadouts.get(player.getName()) == null)
				{
					ItemStack[] temp = { tnt, hook, web, invis, boost };
					contents = temp;
				}
				else
				{
					contents = getContents(loadouts.get(player.getName()));
				}

				player.getInventory().setHelmet(null);
				player.getInventory().setChestplate(null);
				player.getInventory().setLeggings(null);
				player.getInventory().setBoots(null);

				player.getInventory().setContents(contents);
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

		kitEnableTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			public void run()
			{
				for (String name : playing.keySet())
				{
					canUseTnt.put(name, true);
					canUseHook.put(name, true);
					canUseWebber.put(name, true);
					canUseInvis.put(name, true);
					canUseBoost.put(name, true);
					Bukkit.getPlayerExact(name).sendMessage(GOOD + "Kit items are now enabled!");
				}
			}
		}, kitEnableDelay * ticksPerCheck);

		started = true;
	}

	public void tick()
	{
		Iterator<Map.Entry<String, PlayerState>> iter = playing.entrySet().iterator();
		World world = Bukkit.getWorld(worldName);

		while (iter.hasNext())
		{
			Map.Entry<String, PlayerState> entry = iter.next();
			Player player = Bukkit.getPlayer(entry.getKey());
			PlayerState state = entry.getValue();
			Location location = player.getLocation();

			if (!arenaBlocks.isInside(location))
			{
				iter.remove();
				state.restoreInventory(player);
				state.restoreLocation(player);
				state.restoreGameMode(player);

				plugin.getEconomy().bankDeposit(entry.getKey(), baseReward);
				player.sendMessage(GOOD + "Thanks for playing! Here's $" + baseReward);

				broadcast(BAD + entry.getKey() + " fell! " + playing.size() + '/' + watching.size() + " left!", null);
			}
		}

		if (playing.size() > 1)
		{
			if (elapsedTicks >= startDegradeOn && (elapsedTicks % degradeOn) == 0)
			{
				arenaBlocks.degradeBlocks(world, degradeLevel);
				degradeLevel++;
			}

			elapsedTicks++;
		}
		else
		{
			for (Map.Entry<String, PlayerState> entry : playing.entrySet())
			{
				Player player = Bukkit.getPlayer(entry.getKey());
				PlayerState state = entry.getValue();

				state.restoreInventory(player);
				state.restoreLocation(player);
				player.getInventory().addItem(winPrize);
				state.restoreGameMode(player);

				plugin.getEconomy().bankDeposit(entry.getKey(), (winnerReward + wager));

				player.sendMessage(GOOD + "You won! Here's a WinTato and $" + (winnerReward + wager));

				plugin.getLogger().info(entry.getKey() + " won a round. Amount = " + (winnerReward + wager));
				wager = 0;

				broadcast(GOOD + entry.getKey() + " won that round!", null);

				Firework f = (Firework) player.getWorld().spawn(player.getLocation().add(0, 1, 0), Firework.class);

				FireworkMeta fd = f.getFireworkMeta();
				fd.addEffects(FireworkEffect.builder().flicker(false).trail(true).with(Type.BALL_LARGE).withColor(Color.BLUE).withFade(Color.WHITE).build());
				f.setFireworkMeta(fd);
			}

			postStopCleanup();
		}
	}

	public void countdownTick()
	{
		if (countdown <= 0)
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

	/*************************************************************************/
	/* Getter Methods */
	/*************************************************************************/
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

	public Location getWatchLocation()
	{
		return watchLocation;
	}

	public HashMap<String, HashMap<ItemStack, Integer>> getLoadouts()
	{
		return loadouts;
	}

	public HashMap<String, Integer> getLoadoutPoints()
	{
		return loadoutPoints;
	}

	public List<ItemStack> getDefaultLoadout()
	{
		return defaultLoadout;
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
		kitEnableDelay = config.getInt("KitEnableDelay");
		tntUseDelay = config.getInt("ThrowingTNTUseDelay");
		blocksToCancelTnt = config.getInt("ThrowingTNTCancelDistance");
		hookUseDelay = config.getInt("PlayerLauncherUseDelay");
		deWebUseDelay = config.getInt("WebberUseDelay");
		invisUseDelay = config.getInt("RodOfInvisibilityUseDelay");
		boostUseDelay = config.getInt("BoostUseDelay");

		arenaBlocks = new FloorArenaBlocks(config.getConfigurationSection("PointOne"), config.getConfigurationSection("PointTwo"));

		Point watchPoint = new Point(config.getConfigurationSection("WatchPoint"));
		watchLocation = new Location(Bukkit.getWorld(worldName), watchPoint.x(), watchPoint.y(), watchPoint.z(), (float) watchPoint.yaw(), (float) watchPoint.pitch());
	}

	public String forceStart()
	{
		if (started)
		{
			return BAD + "The arena has already started!";
		}
		else if (!enabled)
		{
			return BAD + "The arena is currently disabled!";
		}
		else
		{
			if (countdownTask != null)
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
		if (started)
		{
			if (arenaTask != null)
			{
				arenaTask.cancel();
				arenaTask = null;
			}

			for (Map.Entry<String, PlayerState> entry : playing.entrySet())
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

		if (playing.containsKey(playerName) && started)
		{
			event.setCancelled(true);

			if (event.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				Location clicked = event.getClickedBlock().getLocation();
				if (arenaBlocks.isInside(clicked))
				{
					event.getClickedBlock().setType(Material.AIR);
				}
			}
			else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
			{
				Location clicked = event.getClickedBlock().getLocation();
				ItemStack heldItem = event.getItem();
				if (heldItem != null && heldItem.getType().equals(Material.TNT))
				{
					if (canUseTnt.get(playerName) == null)
						player.sendMessage(BAD + "You can not place tnt yet!");
					else if (arenaBlocks.isInside(clicked))
					{
						if (canUseTnt.get(playerName))
						{
							if (heldItem.getAmount() == 1)
							{
								player.getInventory().remove(heldItem);
							}
							else if (heldItem.getAmount() > 1)
							{
								heldItem.setAmount(heldItem.getAmount() - 1);
							}
							Bukkit.getWorld(worldName).spawn(clicked.add(0, 1, 0), TNTPrimed.class);
							canUseTnt.put(playerName, false);
							msSinceLastTNTThrow.put(playerName, System.currentTimeMillis());
							tntUseTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

								@Override
								public void run()
								{
									canUseTnt.put(playerName, true);
								}
							}, tntUseDelay * ticksPerCheck);
						}
						else if (!canUseTnt.get(playerName))
						{
							player.sendMessage(BAD + "You can not place tnt for another " + (((long) tntUseDelay / (20 / ticksPerCheck)) - ((System.currentTimeMillis() - msSinceLastTNTThrow.get(playerName)) / 1000)) + " seconds!");
						}
					}
				}
				else if (heldItem != null && heldItem.getType().equals(Material.BLAZE_ROD))
				{
					if (canUseInvis.get(playerName) == null)
						player.sendMessage(BAD + "You can not go invisible yet!");
					else if (canUseInvis.get(playerName))
					{
						if (heldItem.getAmount() == 1)
						{
							player.getInventory().remove(heldItem);
						}
						else if (heldItem.getAmount() > 1)
						{
							heldItem.setAmount(heldItem.getAmount() - 1);
						}
						for (Player p : Bukkit.getOnlinePlayers())
						{
							p.hidePlayer(player);
						}
						player.sendMessage(GOOD + "You are now invisible!");
						canUseInvis.put(playerName, false);
						msSinceLastInvisUse.put(playerName, System.currentTimeMillis());
						invisUseTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

							@Override
							public void run()
							{
								canUseInvis.put(playerName, true);
								for (Player p : Bukkit.getOnlinePlayers())
								{
									p.showPlayer(player);
								}
								player.sendMessage(GOOD + "You have become visible!");
							}
						}, invisUseDelay * ticksPerCheck);
					}
					else if (!canUseInvis.get(playerName))
					{
						player.sendMessage(BAD + "You can not go invisible for another " + (((long) invisUseDelay / (20 / ticksPerCheck)) - ((System.currentTimeMillis() - msSinceLastInvisUse.get(playerName)) / 1000)) + " seconds!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerAirClick(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		String playerName = player.getName();
		if (playing.containsKey(playerName) && started)
		{
			event.setCancelled(true);
			if (event.getAction() == Action.RIGHT_CLICK_AIR)
			{
				ItemStack heldItem = event.getItem();

				if (heldItem != null && heldItem.getType().equals(Material.TNT))
				{
					if (canUseTnt.get(playerName) == null)
						player.sendMessage(BAD + "You can not throw tnt yet!");

					else if (canUseTnt.get(playerName))
					{
						if (heldItem.getAmount() == 1)
						{
							player.getInventory().remove(heldItem);
						}
						else if (heldItem.getAmount() > 1)
						{
							heldItem.setAmount(heldItem.getAmount() - 1);
						}
						TNTPrimed tnt = (TNTPrimed) Bukkit.getWorld(worldName).spawn(player.getLocation().add(0, 1, 0), TNTPrimed.class);
						Vector vector = player.getLocation().getDirection();
						vector.add(new Vector(0.0, 0.15, 0.0));
						tnt.setVelocity(vector);
						canUseTnt.put(playerName, false);
						msSinceLastTNTThrow.put(playerName, System.currentTimeMillis());
						tntUseTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

							@Override
							public void run()
							{
								canUseTnt.put(playerName, true);
							}
						}, tntUseDelay * ticksPerCheck);
					}
					else if (!canUseTnt.get(playerName))
					{
						player.sendMessage(BAD + "You can not throw tnt for another " + (((long) tntUseDelay / (20 / ticksPerCheck)) - ((System.currentTimeMillis() - msSinceLastTNTThrow.get(playerName)) / 1000)) + " seconds!");
					}
				}
				else if (heldItem != null && heldItem.getType().equals(Material.FEATHER))
				{
					if (canUseBoost.get(playerName) == null)
						player.sendMessage(BAD + "You can't use a boost yet!");
					else if (canUseBoost.get(playerName))
					{
						if (getWebsAroundPlayer(player, 1) > 0)
						{
							player.sendMessage(BAD + "You can not use a boost while near webs!");
							player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
							return;
						}
						if (heldItem.getAmount() == 1)
						{
							player.getInventory().remove(heldItem);
						}
						else if (heldItem.getAmount() > 1)
						{
							heldItem.setAmount(heldItem.getAmount() - 1);
						}
						Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
						loc.setPitch(-30f);
						Vector vector = loc.getDirection();
						vector.add(new Vector(0.0, 0.15, 0.0));
						vector.multiply(2);
						player.setVelocity(vector);
						player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1f, 1f);
						canUseBoost.put(playerName, false);
						msSinceLastBoostUse.put(playerName, System.currentTimeMillis());
						boostUseTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

							@Override
							public void run()
							{
								canUseBoost.put(playerName, true);
							}
						}, boostUseDelay * ticksPerCheck);
					}
					else if (!canUseBoost.get(playerName))
					{
						player.sendMessage(BAD + "You can not use a boost for another " + (((long) boostUseDelay / (20 / ticksPerCheck)) - ((System.currentTimeMillis() - msSinceLastBoostUse.get(playerName)) / 1000)) + " seconds!");
					}
				}
				else if (heldItem != null && heldItem.getType().equals(Material.TRIPWIRE_HOOK))
				{
					if (canUseHook.get(playerName) == null)
						player.sendMessage(BAD + "You can't launch players yet!");
					else if (canUseHook.get(playerName))
					{
						Player looking = getTarget(player, Bukkit.getOnlinePlayers());
						if (looking != null && playing.containsKey(looking.getName()))
						{
							if (player.getLocation().distance(looking.getLocation()) <= 6)
							{
								if (getWebsAroundPlayer(looking, 1) > 0)
								{
									player.sendMessage(BAD + "You can not launch players that are in a web!");
									player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
									return;
								}
								if (heldItem.getAmount() == 1)
								{
									player.getInventory().remove(heldItem);
								}
								else if (heldItem.getAmount() > 1)
								{
									heldItem.setAmount(heldItem.getAmount() - 1);
								}
								Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
								loc.setYaw(loc.getYaw() + 180);
								loc.setPitch(-30f);
								Vector vector = loc.getDirection();
								vector.multiply(2);
								looking.setVelocity(vector);
								player.playSound(player.getLocation(), Sound.HURT_FLESH, 1f, 1f);
								canUseHook.put(playerName, false);
								msSinceLastHookUse.put(playerName, System.currentTimeMillis());
								hookUseTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

									@Override
									public void run()
									{
										canUseHook.put(playerName, true);
									}
								}, hookUseDelay * ticksPerCheck);
							}
						}
					}
					else if (!canUseHook.get(playerName))
					{
						player.sendMessage(BAD + "You can not launch players for another " + (((long) hookUseDelay / (20 / ticksPerCheck)) - ((System.currentTimeMillis() - msSinceLastHookUse.get(playerName)) / 1000)) + " seconds!");
					}

				}
				else if (heldItem != null && heldItem.getType().equals(Material.BLAZE_ROD))
				{
					if (canUseInvis.get(playerName) == null)
						player.sendMessage(BAD + "You can not go invisible yet!");
					else if (canUseInvis.get(playerName))
					{
						if (heldItem.getAmount() == 1)
						{
							player.getInventory().remove(heldItem);
						}
						else if (heldItem.getAmount() > 1)
						{
							heldItem.setAmount(heldItem.getAmount() - 1);
						}
						for (Player p : Bukkit.getOnlinePlayers())
						{
							p.hidePlayer(player);
						}
						player.sendMessage(GOOD + "You are now invisible!");
						canUseInvis.put(playerName, false);
						msSinceLastInvisUse.put(playerName, System.currentTimeMillis());
						invisUseTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

							@Override
							public void run()
							{
								canUseInvis.put(playerName, true);
								for (Player p : Bukkit.getOnlinePlayers())
								{
									p.showPlayer(player);
								}
								player.sendMessage(GOOD + "You have become visible!");
							}
						}, invisUseDelay * ticksPerCheck);
					}
					else if (!canUseInvis.get(playerName))
					{
						player.sendMessage(BAD + "You can not go invisible for another " + (((long) invisUseDelay / (20 / ticksPerCheck)) - ((System.currentTimeMillis() - msSinceLastInvisUse.get(playerName)) / 1000)) + " seconds!");
					}
				}
				else if (heldItem != null && heldItem.getType().equals(Material.WEB))
				{
					if (canUseWebber.get(playerName) == null)
						player.sendMessage(BAD + "You can not use the webber yet!");
					else if (canUseWebber.get(playerName))
					{
						Player looking = getTarget(player, Bukkit.getOnlinePlayers());
						if (looking != null && playing.containsKey(looking.getName()))
						{
							if (player.getLocation().distance(looking.getLocation()) <= 15)
							{
								if (heldItem.getAmount() == 1)
								{
									player.getInventory().remove(heldItem);
								}
								else if (heldItem.getAmount() > 1)
								{
									heldItem.setAmount(heldItem.getAmount() - 1);
								}
								webPlayer(looking, true, 2);
								canUseWebber.put(playerName, false);
								msSinceLastWebbing.put(playerName, System.currentTimeMillis());
								webUseTask = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

									@Override
									public void run()
									{
										canUseWebber.put(playerName, true);
									}
								}, deWebUseDelay * ticksPerCheck);
							}
						}
					}
					else if (!canUseWebber.get(playerName))
					{
						player.sendMessage(BAD + "You can not use the webber for another " + (((long) deWebUseDelay / (20 / ticksPerCheck)) - ((System.currentTimeMillis() - msSinceLastWebbing.get(playerName)) / 1000)) + " seconds!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		if (started && playing.containsKey(event.getPlayer().getName()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if (started && playing.containsKey(event.getPlayer().getName()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryDragItem(InventoryDragEvent event)
	{
		if (started && event.getWhoClicked() instanceof Player && playing.containsKey(event.getWhoClicked().getName()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryClickArenaStarted(InventoryClickEvent event)
	{
		if (started && event.getWhoClicked() instanceof Player && playing.containsKey(event.getWhoClicked().getName()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		Location explosion = event.getLocation();
		if (event.getEntityType().equals(EntityType.PRIMED_TNT) && (arenaBlocks.isInside(explosion) || arenaBlocks.isYBlocksBelow(explosion, blocksToCancelTnt)))
		{
			event.setCancelled(true);
			List<Block> blocksToBeDestroyed = event.blockList();
			for (Block block : blocksToBeDestroyed)
			{
				if (started && arenaBlocks.isInside(block.getLocation()))
				{
					block.setType(Material.AIR);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event)
	{
		if (event.getEntityType().equals(EntityType.PLAYER) && playing.containsKey(((Player) event.getEntity()).getName()))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		String playerName = event.getPlayer().getName();
		Location locTo = event.getTo();

		if (arenaBlocks.isInside(locTo) && !playing.containsKey(playerName))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		String playerName = player.getName();

		if (playing.containsKey(playerName))
		{
			String word = event.getMessage().split("\\s+")[0];

			if (player.hasPermission("FloorIsLava.Staff"))
			{
				return;
			}
			if (whitelistCommands.contains(word))
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

	/*************************************************************************/
	/* Private Methods */
	/*************************************************************************/
	private void resetCoundown()
	{
		if (countdownTask != null)
		{
			countdownTask.cancel();
			countdownTask = null;
		}

		if (playing.size() >= minimumPlayers)
		{
			countdown = maxCountdown;
			countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
				@Override
				public void run()
				{
					countdownTick();
				}
			}, 100, 10);
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

		playing.clear();
		watching.clear();

		for (String player : canUseInvis.keySet())
		{
			for (Player p : Bukkit.getOnlinePlayers())
			{
				p.showPlayer(Bukkit.getPlayerExact(player));
			}
		}

		canUseTnt.clear();
		canUseBoost.clear();
		canUseHook.clear();
		canUseWebber.clear();
		canUseInvis.clear();

		arenaBlocks.restore();

		if (arenaTask != null)
		{
			arenaTask.cancel();
			arenaTask = null;
		}
		if (countdownTask != null)
		{
			countdownTask.cancel();
			countdownTask = null;
		}
		if (kitEnableTask != null)
		{
			kitEnableTask.cancel();
			kitEnableTask = null;
		}
		if (tntUseTask != null)
		{
			tntUseTask.cancel();
			tntUseTask = null;
		}
		if (boostUseTask != null)
		{
			boostUseTask.cancel();
			boostUseTask = null;
		}
		if (hookUseTask != null)
		{
			hookUseTask.cancel();
			hookUseTask = null;
		}
		if (invisUseTask != null)
		{
			invisUseTask.cancel();
			invisUseTask = null;
		}
		if (webUseTask != null)
		{
			webUseTask.cancel();
			webUseTask = null;
		}

		started = false;
	}

	private void broadcast(String message, String[] exclude)
	{
		for (String name : watching)
		{
			Player target = Bukkit.getPlayer(name);
			if (target != null && target.isOnline())
			{
				if (exclude == null || !Arrays.asList(exclude).contains(name))
					target.sendMessage(message);
			}
		}
	}

	private <T extends Entity> T getTarget(final Entity entity, final Iterable<T> entities)
	{
		if (entity == null)
			return null;
		T target = null;
		final double threshold = 1;
		for (final T other : entities)
		{
			final Vector n = other.getLocation().toVector().subtract(entity.getLocation().toVector());
			if (entity.getLocation().getDirection().normalize().crossProduct(n).lengthSquared() < threshold && n.normalize().dot(entity.getLocation().getDirection().normalize()) >= 0)
			{
				if (target == null || target.getLocation().distanceSquared(entity.getLocation()) > other.getLocation().distanceSquared(entity.getLocation()))
					target = other;
			}
		}
		return target;
	}

	private void webPlayer(Player player, boolean shouldWeb, int radius)
	{
		int r = radius;
		int px = player.getLocation().getBlockX();
		int py = player.getLocation().getBlockY();
		int pz = player.getLocation().getBlockZ();

		for (int x = -r; x <= r; x++)
			for (int y = -r; y <= r; y++)
				for (int z = -r; z <= r; z++)
				{
					if (x * x + y * y + z * z > r * r)
					{
						continue;
					}
					int xpos = px + x;
					int ypos = py + y;
					int zpos = pz + z;
					if (ypos > 127 || ypos < 0)
					{
						continue;
					}
					if (player.getWorld().getBlockAt(xpos, ypos, zpos).getType().equals(Material.AIR) && arenaBlocks.isInside(player.getWorld().getBlockAt(xpos, ypos, zpos).getLocation()))
					{
						if (shouldWeb)
						{
							player.getWorld().getBlockAt(xpos, ypos, zpos).setType(Material.WEB);
						}
					}
				}
	}

	private int getWebsAroundPlayer(Player player, int radius)
	{
		int r = radius;
		int count = 0;
		int px = player.getLocation().getBlockX();
		int py = player.getLocation().getBlockY();
		int pz = player.getLocation().getBlockZ();

		for (int x = -r; x <= r; x++)
			for (int y = -r; y <= r; y++)
				for (int z = -r; z <= r; z++)
				{
					if (x * x + y * y + z * z > r * r)
					{
						continue;
					}
					int xpos = px + x;
					int ypos = py + y;
					int zpos = pz + z;
					if (ypos > 127 || ypos < 0)
					{
						continue;
					}
					if (player.getWorld().getBlockAt(xpos, ypos, zpos).getType().equals(Material.WEB) && arenaBlocks.isInside(player.getWorld().getBlockAt(xpos, ypos, zpos).getLocation()))
					{
						count++;
					}
				}
		return count;
	}

	private ItemStack[] getContents(HashMap<ItemStack, Integer> hash)
	{
		ItemStack[] newStack = new ItemStack[hash.keySet().size()];

		int count = 0;
		for (ItemStack stack : hash.keySet())
		{
			newStack[count] = new ItemStack(stack);
			newStack[count].setAmount(hash.get(stack));
			if (newStack[count].getAmount() == 0)
				newStack[count] = null;
			count++;
		}

		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for (ItemStack item : newStack)
		{
			if (item != null)
				stacks.add(item);
		}

		newStack = stacks.toArray(new ItemStack[stacks.size()]);
		return newStack;
	}
}
