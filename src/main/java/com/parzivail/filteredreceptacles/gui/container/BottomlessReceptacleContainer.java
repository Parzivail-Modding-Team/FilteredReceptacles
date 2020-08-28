package com.parzivail.filteredreceptacles.gui.container;

import com.parzivail.filteredreceptacles.block.entity.BottomlessReceptacleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BottomlessReceptacleContainer extends ScreenHandler
{
	private final BottomlessReceptacleEntity inventory;
	private static final int INVENTORY_SIZE = 2;
	private final Slot inputSlot;
	private final Slot outputSlot;

	public BottomlessReceptacleContainer(int syncId, PlayerInventory playerInventory, BottomlessReceptacleEntity inventory)
	{
		super(null, syncId);
		this.inventory = inventory;
		checkSize(inventory, INVENTORY_SIZE);
		inventory.onOpen(playerInventory.player);

		this.addSlot(inputSlot = new Slot(inventory, 0, 44, 22)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				return BottomlessReceptacleContainer.this.inventory.canInsert(0, stack, null);
			}
		});
		this.addSlot(outputSlot = new Slot(inventory, 1, 116, 22)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				return false;
			}
		});

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

	public BottomlessReceptacleEntity getReceptacle()
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
