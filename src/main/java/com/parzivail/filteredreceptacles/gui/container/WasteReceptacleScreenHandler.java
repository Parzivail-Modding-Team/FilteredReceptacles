package com.parzivail.filteredreceptacles.gui.container;

import com.parzivail.filteredreceptacles.FilteredReceptacles;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.slot.Slot;

public class WasteReceptacleScreenHandler extends InventoryScreenHandler
{
	private static final int INVENTORY_SIZE = 1;

	public WasteReceptacleScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(INVENTORY_SIZE));
	}

	public WasteReceptacleScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory)
	{
		super(FilteredReceptacles.SH_RECEPTACLE_WASTE, syncId, inventory);
		checkSize(inventory, INVENTORY_SIZE);
		inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, 0, 80, 22));

		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 9; j++)
			{
				this.addSlot(new Slot(playerInventory, i * 9 + j + 9, 8 + j * 18, 58 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++)
		{
			this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 116));
		}
	}
}
