package com.parzivail.filteredreceptacles.util;

import net.minecraft.item.ItemStack;

import java.util.Objects;

public class FilterUtil
{
	public enum FilterLevel
	{
		Item, FuzzyItem, ItemAndData
	}

	public static boolean AreEqual(ItemStack a, ItemStack b, FilterLevel level)
	{
		if (a.isEmpty() || b.isEmpty())
			return false;

		switch (level)
		{
			case Item:
				return a.getItem() == b.getItem();
			case FuzzyItem:
				return false;
			case ItemAndData:
				return a.getItem() == b.getItem() && Objects.equals(a.getTag(), b.getTag());
			default:
				throw new IndexOutOfBoundsException("level");
		}
	}
}
