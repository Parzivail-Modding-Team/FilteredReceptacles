package com.parzivail.filteredreceptacles.gui.container;

import com.parzivail.filteredreceptacles.block.entity.WasteReceptacleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class WasteReceptacleContainer extends ScreenHandler
{
	private final WasteReceptacleEntity inventory;
	private static final int INVENTORY_SIZE = 1;

	public WasteReceptacleContainer(int syncId, PlayerInventory playerInventory, WasteReceptacleEntity inventory)
	{
		super(null, syncId);
		this.inventory = inventory;
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

	public WasteReceptacleEntity getReceptacle()
	{
		return inventory;
	}

	@Override
	public boolean canUse(PlayerEntity player)
	{
		return this.inventory.canPlayerUse(player);
	}

	public ItemStack transferSlot(PlayerEntity player, int invSlot)
	{
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(invSlot);
		if (slot != null && slot.hasStack())
		{
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot < this.inventory.size())
			{
				if (!this.insertItem(itemStack2, this.inventory.size(), this.slots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.insertItem(itemStack2, 0, this.inventory.size(), false))
			{
				return ItemStack.EMPTY;
			}

			if (itemStack2.isEmpty())
			{
				slot.setStack(ItemStack.EMPTY);
			}
			else
			{
				slot.markDirty();
			}
		}

		return itemStack;
	}
}
