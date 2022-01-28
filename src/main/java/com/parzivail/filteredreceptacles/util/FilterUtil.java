package com.parzivail.filteredreceptacles.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Objects;

public class FilterUtil
{
	public static boolean IsEmpty(ItemStack referenceStack)
	{
		return referenceStack == ItemStack.EMPTY || (referenceStack.getItem() == Items.AIR && referenceStack.getCount() == 0);
	}

	public enum FilterLevel
	{
		Item, FuzzyItem, ItemAndData
	}

	public static boolean AreEqual(ItemStack a, ItemStack b, FilterLevel level)
	{
		if (a.isEmpty() || b.isEmpty())
			return false;

		// TODO: Tags
		return switch (level)
				{
					case Item -> a.getItem() == b.getItem();
					case FuzzyItem -> false;
					case ItemAndData -> a.getItem() == b.getItem() && Objects.equals(a.getNbt(), b.getNbt());
				};
	}
}
