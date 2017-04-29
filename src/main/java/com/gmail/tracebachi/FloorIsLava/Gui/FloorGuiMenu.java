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
    public static final ItemStack JOIN_ITEM = new ItemStack(Material.LEATHER_CHESTPLATE);
    public static final ItemStack LEAVE_ITEM = new ItemStack(Material.LEATHER_LEGGINGS);
    public static final ItemStack SCORE_ITEM = new ItemStack(Material.NETHER_STAR);
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

    private Arena arena;
    private Inventory inventory;

    public FloorGuiMenu(Arena arena)
    {
        this.arena = arena;
        this.inventory = Bukkit.createInventory(null, 27, "Floor Is Lava Menu");
    }

    public void showTo(Player player)
    {
        String name = player.getName();
        Loadout loadout = arena.getLoadoutMap().getOrDefault(name, new Loadout());

        ItemStack scoreClone = SCORE_ITEM.clone();
        ItemMeta meta = scoreClone.getItemMeta();
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Floor Is Lava wins: "
            + arena.getFloorLeaderboard().getScore(name)));
        scoreClone.setItemMeta(meta);

        inventory.setItem(2, JOIN_ITEM);
        inventory.setItem(3, LEAVE_ITEM);
        inventory.setItem(4, scoreClone);
        inventory.setItem(5, WATCH_ITEM);
        inventory.setItem(6, HELP_ITEM);

        int maxPoints = arena.getBooster().isActive() ? 10 : 5;

        inventory.setItem(13, cloneWithAmount(POINTS_ITEM, maxPoints - loadout.countSum()));
        inventory.setItem(19, cloneWithAmount(TNT_ITEM, loadout.tnt));
        inventory.setItem(20, cloneWithAmount(HOOK_ITEM, loadout.hook));
        inventory.setItem(21, cloneWithAmount(WEB_ITEM, loadout.web));
        inventory.setItem(22, cloneWithAmount(INVIS_ITEM, loadout.invis));
        inventory.setItem(23, cloneWithAmount(BOOST_ITEM, loadout.boost));
        inventory.setItem(24, cloneWithAmount(CHIKUN_ITEM, loadout.chikun));
        inventory.setItem(25, cloneWithAmount(STEAL_ITEM, loadout.steal));

        player.openInventory(inventory);
    }

    private ItemStack cloneWithAmount(ItemStack itemStack, int amount)
    {
        ItemStack itemStackClone = itemStack.clone();
        itemStackClone.setAmount(amount);

        return itemStackClone;
    }

    static
    {
        ItemMeta meta = JOIN_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Join");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to join Floor Is Lava"));
        JOIN_ITEM.setItemMeta(meta);

        meta = LEAVE_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Leave");
        meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to leave Floor Is Lava"));
        LEAVE_ITEM.setItemMeta(meta);

        meta = SCORE_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Wins");
        SCORE_ITEM.setItemMeta(meta);

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
