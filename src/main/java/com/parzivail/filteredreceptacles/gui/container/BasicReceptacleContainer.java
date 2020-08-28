package com.parzivail.filteredreceptacles.gui.container;

import com.parzivail.filteredreceptacles.block.entity.BasicReceptacleEntity;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BasicReceptacleContainer extends ScreenHandler
{
	private final BasicReceptacleEntity inventory;
	private static final int INVENTORY_SIZE = 5;
	private final Slot filterSlot;

	public BasicReceptacleContainer(int syncId, PlayerInventory playerInventory, BasicReceptacleEntity inventory)
	{
		super(null, syncId);
		this.inventory = inventory;
		checkSize(inventory, INVENTORY_SIZE);
		inventory.onOpen(playerInventory.player);

		this.addSlot(filterSlot = new Slot(inventory, 0, 15, 22)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				return true;
			}

			@Override
			public int getMaxItemCount()
			{
				return 1;
			}
		});

		for (int i = 0; i < 5; i++)
		{
			this.addSlot(new Slot(inventory, 1 + i, 44 + i * 18, 22)
			{
				@Override
				public boolean canInsert(ItemStack stack)
				{
					ItemStack filterStack = filterSlot.getStack();
					return filterStack.isEmpty() || FilterUtil.AreEqual(filterStack, stack, BasicReceptacleContainer.this.inventory.getFilterLevel());
				}
			});
		}

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

	public BasicReceptacleEntity getReceptacle()
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
			else if (!this.insertItem(itemStack2, 1, this.inventory.size(), false))
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
