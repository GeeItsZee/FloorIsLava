package com.yahoo.tracebachi.FloorIsLava.UtilClasses;

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

    static
    {
        ItemMeta tntMeta = TNT_ITEM.getItemMeta();
        tntMeta.setDisplayName(ChatColor.DARK_RED + "\u2622" + ChatColor.GOLD +
            " Throwing TNT " + ChatColor.DARK_RED + "\u2622");
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
    }

    public int tntCount;
    public int hookCount;
    public int webCount;
    public int invisCount;
    public int boostCount;

    public int countSum()
    {
        return tntCount + hookCount + webCount + invisCount + boostCount;
    }
}
