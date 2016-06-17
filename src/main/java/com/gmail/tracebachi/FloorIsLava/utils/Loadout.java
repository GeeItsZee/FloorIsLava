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
package com.gmail.tracebachi.FloorIsLava.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 2/17/16.
 */
public class Loadout
{
    public static final ItemStack TNT_ITEM = new ItemStack(Material.TNT);
    public static final ItemStack HOOK_ITEM = new ItemStack(Material.TRIPWIRE_HOOK);
    public static final ItemStack WEB_ITEM = new ItemStack(Material.WEB);
    public static final ItemStack INVIS_ITEM = new ItemStack(Material.BLAZE_ROD);
    public static final ItemStack BOOST_ITEM = new ItemStack(Material.FEATHER);
    public static final ItemStack CHIKUN_ITEM = new ItemStack(Material.EGG);
    public static final ItemStack STEAL_ITEM = new ItemStack(Material.FLINT_AND_STEEL);

    static
    {
        ItemMeta tntMeta = TNT_ITEM.getItemMeta();
        tntMeta.setDisplayName(ChatColor.DARK_RED + "\u2622"
                    + ChatColor.GOLD + " Throwing TNT "
                    + ChatColor.DARK_RED + "\u2622");
        TNT_ITEM.setItemMeta(tntMeta);

        ItemMeta hookMeta = HOOK_ITEM.getItemMeta();
        hookMeta.setDisplayName(ChatColor.AQUA + "Player Launcher");
        HOOK_ITEM.setItemMeta(hookMeta);

        ItemMeta dewebMeta = WEB_ITEM.getItemMeta();
        dewebMeta.setDisplayName(ChatColor.GREEN + "Webber");
        WEB_ITEM.setItemMeta(dewebMeta);

        ItemMeta invisMeta = INVIS_ITEM.getItemMeta();
        invisMeta.setDisplayName(ChatColor.GRAY + "Rod of Invisibility");
        INVIS_ITEM.setItemMeta(invisMeta);

        ItemMeta boostMeta = BOOST_ITEM.getItemMeta();
        boostMeta.setDisplayName(ChatColor.YELLOW + "Boost");
        BOOST_ITEM.setItemMeta(boostMeta);

        ItemMeta chikunMeta = CHIKUN_ITEM.getItemMeta();
        chikunMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Chikun Bomb");
        CHIKUN_ITEM.setItemMeta(chikunMeta);

        ItemMeta stealMeta = STEAL_ITEM.getItemMeta();
        stealMeta.setDisplayName(ChatColor.BLUE + "Steal");
        STEAL_ITEM.setItemMeta(stealMeta);
    }

    public int tntCount;
    public int hookCount;
    public int webCount;
    public int invisCount;
    public int boostCount;
    public int chikunCount;
    public int stealCount;

    public int countSum()
    {
        return tntCount + hookCount + webCount + invisCount + boostCount + chikunCount + stealCount;
    }
}
