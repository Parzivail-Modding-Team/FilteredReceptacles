package com.parzivail.filteredreceptacles.gui.container;

import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.block.entity.BasicReceptacleEntity;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class BasicReceptacleScreenHandler extends InventoryScreenHandler
{
	private static final int INVENTORY_SIZE = 6;

	private final Slot filterSlot;
	private final PropertyDelegate propertyDelegate;

	public BasicReceptacleScreenHandler(int syncId, PlayerInventory inventory)
	{
		this(syncId, inventory, new SimpleInventory(INVENTORY_SIZE), new ArrayPropertyDelegate(1));
	}

	public BasicReceptacleScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate)
	{
		super(FilteredReceptacles.SH_RECEPTACLE_BASIC, syncId, inventory);
		this.propertyDelegate = propertyDelegate;
		checkSize(inventory, INVENTORY_SIZE);
		inventory.onOpen(playerInventory.player);

		this.addProperties(propertyDelegate);

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
					return filterStack.isEmpty() || FilterUtil.AreEqual(filterStack, stack, BasicReceptacleScreenHandler.this.getFilterLevel());
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

	FilterUtil.FilterLevel getFilterLevel()
	{
		return FilterUtil.FilterLevel.values()[propertyDelegate.get(BasicReceptacleEntity.PROP_FILTER_LEVEL)];
	}
}
