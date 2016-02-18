package com.yahoo.tracebachi.FloorIsLava;

import com.yahoo.tracebachi.FloorIsLava.UtilClasses.Loadout;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class FloorGuiMenu implements Listener
{
    private Arena arena;
    private Inventory inventory;

    public static final ItemStack JOIN_ITEM = new ItemStack(Material.LEATHER_CHESTPLATE);
    public static final ItemStack LEAVE_ITEM = new ItemStack(Material.LEATHER_LEGGINGS);
    public static final ItemStack WATCH_ITEM = new ItemStack(Material.EYE_OF_ENDER);
    public static final ItemStack HELP_ITEM = new ItemStack(Material.MAP);
    public static final ItemStack POINTS_ITEM = new ItemStack(Material.EMERALD);

    public static final ItemStack TNT_ITEM = new ItemStack(Material.TNT);
    public static final ItemStack HOOK_ITEM = new ItemStack(Material.TRIPWIRE_HOOK);
    public static final ItemStack WEB_ITEM = new ItemStack(Material.WEB);
    public static final ItemStack INVIS_ITEM = new ItemStack(Material.BLAZE_ROD);
    public static final ItemStack BOOST_ITEM = new ItemStack(Material.FEATHER);

    static
    {
        setupMenuItemMetas();
    }

    public FloorGuiMenu(Arena arena)
    {
        this.arena = arena;
        this.inventory = Bukkit.createInventory(null, 27, "Floor Is Lava Menu");

        setupMenuItemMetas();
    }

    public void showTo(Player player)
    {
        setupLoadoutCounts(player);
        player.openInventory(inventory);
    }

    private void setupLoadoutCounts(Player player)
    {
        String name = player.getName();
        Loadout loadout = arena.getLoadoutMap().get(name);

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

        POINTS_ITEM.setAmount(pointsAmount);
        TNT_ITEM.setAmount(tntAmount);
        HOOK_ITEM.setAmount(hookAmount);
        WEB_ITEM.setAmount(webAmount);
        INVIS_ITEM.setAmount(invisAmount);
        BOOST_ITEM.setAmount(boostAmount);

        inventory.setItem(2, JOIN_ITEM);
        inventory.setItem(3, LEAVE_ITEM);
        inventory.setItem(5, WATCH_ITEM);
        inventory.setItem(6, HELP_ITEM);

        inventory.setItem(13, POINTS_ITEM);
        inventory.setItem(20, TNT_ITEM);
        inventory.setItem(21, HOOK_ITEM);
        inventory.setItem(22, WEB_ITEM);
        inventory.setItem(23, INVIS_ITEM);
        inventory.setItem(24, BOOST_ITEM);
    }

    private static void setupMenuItemMetas()
    {
        ItemMeta meta = JOIN_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Join");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to join Floor Is Lava"));
        JOIN_ITEM.setItemMeta(meta);

        meta = LEAVE_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Leave");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to leave Floor Is Lava"));
        LEAVE_ITEM.setItemMeta(meta);

        meta = WATCH_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Watch");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to watch other players"));
        WATCH_ITEM.setItemMeta(meta);

        meta = HELP_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Menu");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "  /floor",
            ChatColor.YELLOW + "Wagering Money",
            ChatColor.WHITE + "  /floor wager [amount]",
            ChatColor.YELLOW + "Arena Status",
            ChatColor.WHITE + "  /floor count"));
        HELP_ITEM.setItemMeta(meta);

        meta = POINTS_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Loadout Points");
        POINTS_ITEM.setItemMeta(meta);

        meta = TNT_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Throwing TNT ");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "Throw ignited tnt at players by right clicking it!",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        TNT_ITEM.setItemMeta(meta);

        meta = HOOK_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Player Launcher");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "Launcher players in the air by right clicking them!",
            ChatColor.GRAY + "Maximum distance from player: 6 blocks",
            ChatColor.GRAY + "Minimum distance from player: 2 blocks",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        HOOK_ITEM.setItemMeta(meta);

        meta = WEB_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Webber");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "Create a box of webs around a player by right clicking them!",
            ChatColor.GRAY + "Maximum distance from player: 15 blocks",
            ChatColor.GRAY + "Minimum distance from player: 2 blocks",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        WEB_ITEM.setItemMeta(meta);

        meta = HOOK_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Rod of Invisibility");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "Become invisible and sneak up on your opponents!",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        INVIS_ITEM.setItemMeta(meta);

        meta = BOOST_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Boost");
        meta.setLore(Arrays.asList(ChatColor.WHITE + "Launch yourself in the air to get away from danger!",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        BOOST_ITEM.setItemMeta(meta);
    }
}
