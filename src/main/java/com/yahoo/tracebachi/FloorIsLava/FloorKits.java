package com.yahoo.tracebachi.FloorIsLava;

import org.bukkit.inventory.ItemStack;

public class FloorKits
{
	private String name;
	private ItemStack[] items;

	public FloorKits(String name, ItemStack[] items)
	{
		this.name = name;
		this.items = items;
	}

	public ItemStack[] getKit()
	{
		return items;
	}

	public String getName()
	{
		return name;
	}
}
