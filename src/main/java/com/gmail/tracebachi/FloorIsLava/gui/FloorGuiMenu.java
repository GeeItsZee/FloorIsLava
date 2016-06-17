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
package com.gmail.tracebachi.FloorIsLava.gui;

import com.gmail.tracebachi.FloorIsLava.arena.Arena;
import com.gmail.tracebachi.FloorIsLava.utils.Loadout;
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
    public static final ItemStack CHIKUN_ITEM = new ItemStack(Material.EGG);
    public static final ItemStack STEAL_ITEM = new ItemStack(Material.FLINT_AND_STEEL);

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
        int chikunAmount = 0;
        int stealAmount = 0;
        int pointsAmount = arena.getBooster().isActive() ? 9 : 4;

        if(loadout != null)
        {
            tntAmount = loadout.tntCount;
            hookAmount = loadout.hookCount;
            webAmount = loadout.webCount;
            invisAmount = loadout.invisCount;
            boostAmount = loadout.boostCount;
            chikunAmount = loadout.chikunCount;
            stealAmount = loadout.stealCount;
            pointsAmount = (arena.getBooster().isActive() ? 10 : 5) - loadout.countSum();
        }

        POINTS_ITEM.setAmount(pointsAmount);
        TNT_ITEM.setAmount(tntAmount);
        HOOK_ITEM.setAmount(hookAmount);
        WEB_ITEM.setAmount(webAmount);
        INVIS_ITEM.setAmount(invisAmount);
        BOOST_ITEM.setAmount(boostAmount);
        CHIKUN_ITEM.setAmount(chikunAmount);
        STEAL_ITEM.setAmount(stealAmount);

        inventory.setItem(2, JOIN_ITEM);
        inventory.setItem(3, LEAVE_ITEM);
        inventory.setItem(5, WATCH_ITEM);
        inventory.setItem(6, HELP_ITEM);

        inventory.setItem(13, POINTS_ITEM);
        inventory.setItem(19, TNT_ITEM);
        inventory.setItem(20, HOOK_ITEM);
        inventory.setItem(21, WEB_ITEM);
        inventory.setItem(22, INVIS_ITEM);
        inventory.setItem(23, BOOST_ITEM);
        inventory.setItem(24, CHIKUN_ITEM);
        inventory.setItem(25, STEAL_ITEM);
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
        meta.setLore(Arrays.asList(
            ChatColor.WHITE + "Throw ignited tnt at",
            ChatColor.WHITE + "players by right clicking it!",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        TNT_ITEM.setItemMeta(meta);

        meta = HOOK_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Player Launcher");
        meta.setLore(Arrays.asList(
            ChatColor.WHITE + "Launches players in the",
            ChatColor.WHITE + "air by right clicking them!",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        HOOK_ITEM.setItemMeta(meta);

        meta = WEB_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Webber");
        meta.setLore(Arrays.asList(
            ChatColor.WHITE + "Create a box of webs around a",
            ChatColor.WHITE + "player by right clicking them!",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        WEB_ITEM.setItemMeta(meta);

        meta = HOOK_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Rod of Invisibility");
        meta.setLore(Arrays.asList(
            ChatColor.WHITE + "Become invisible and",
            ChatColor.WHITE + "sneak up on your opponents!",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        INVIS_ITEM.setItemMeta(meta);

        meta = BOOST_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Boost");
        meta.setLore(Arrays.asList(
            ChatColor.WHITE + "Launch yourself in the",
            ChatColor.WHITE + "air to get away from danger!",
            ChatColor.YELLOW + "Left Click: Add",
            ChatColor.YELLOW + "Right Click: Remove"));
        BOOST_ITEM.setItemMeta(meta);

        meta = CHIKUN_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Chikun Bomb");
        meta.setLore(Arrays.asList(
                    ChatColor.WHITE + "Instantly spawn in",
                    ChatColor.WHITE + "chikun distractions!",
                    ChatColor.YELLOW + "Left Click: Add",
                    ChatColor.YELLOW + "Right Click: Remove"));
        CHIKUN_ITEM.setItemMeta(meta);

        meta = STEAL_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Steal");
        meta.setLore(Arrays.asList(
                    ChatColor.WHITE + "You have a chance to steal an",
                    ChatColor.WHITE + "ability from an opponent or",
                    ChatColor.WHITE + "backfire on you!",
                    ChatColor.YELLOW + "Left Click: Add",
                    ChatColor.YELLOW + "Right Click: Remove"));
        STEAL_ITEM.setItemMeta(meta);
    }
}