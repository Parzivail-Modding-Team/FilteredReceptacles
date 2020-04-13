package com.parzivail.filteredreceptacles.gui.container;

import com.parzivail.filteredreceptacles.block.entity.BottomlessReceptacleEntity;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class BottomlessReceptacleContainer extends Container
{
	private final BottomlessReceptacleEntity inventory;
	private static final int INVENTORY_SIZE = 2;
	private final Slot inputSlot;
	private final Slot outputSlot;

	public BottomlessReceptacleContainer(int syncId, PlayerInventory playerInventory, BottomlessReceptacleEntity inventory)
	{
		super(null, syncId);
		this.inventory = inventory;
		checkContainerSize(inventory, INVENTORY_SIZE);
		inventory.onInvOpen(playerInventory.player);

		this.addSlot(inputSlot = new Slot(inventory, 0, 44, 22)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				return BottomlessReceptacleContainer.this.inventory.canInsertInvStack(0, stack, null);
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
		return this.inventory.canPlayerUseInv(player);
	}

	public ItemStack transferSlot(PlayerEntity player, int invSlot)
	{
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slotList.get(invSlot);
		if (slot != null && slot.hasStack())
		{
			ItemStack itemStack2 = slot.getStack();
			itemStack = itemStack2.copy();
			if (invSlot < this.inventory.getInvSize())
			{
				if (!this.insertItem(itemStack2, this.inventory.getInvSize(), this.slotList.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!this.insertItem(itemStack2, 0, this.inventory.getInvSize(), false))
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
