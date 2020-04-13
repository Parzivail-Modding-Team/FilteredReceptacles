package com.parzivail.filteredreceptacles.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;

public class PlayerUtil
{
	public static PlayerInventory getPlayerInventory()
	{
		MinecraftClient c = MinecraftClient.getInstance();
		if (c == null || c.player == null)
			return null;

		return c.player.inventory;
	}
}
