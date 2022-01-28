package com.parzivail.filteredreceptacles.gui.container;

import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.block.entity.BottomlessReceptacleEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;

public class BottomlessReceptacleScreenHandler extends InventoryScreenHandler
{
	private static final int INVENTORY_SIZE = 2;

	private final Slot inputSlot;
	private final Slot outputSlot;
	private final PropertyDelegate propertyDelegate;

	public BottomlessReceptacleScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		this(syncId, playerInventory, new SimpleInventory(INVENTORY_SIZE), new ArrayPropertyDelegate(2));
	}

	public BottomlessReceptacleScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate)
	{
		super(FilteredReceptacles.SH_RECEPTACLE_BOTTOMLESS, syncId, inventory);
		this.propertyDelegate = propertyDelegate;
		checkSize(inventory, INVENTORY_SIZE);
		inventory.onOpen(playerInventory.player);

		this.addProperties(propertyDelegate);

		this.addSlot(inputSlot = new Slot(inventory, 0, 44, 22)
		{
			@Override
			public boolean canInsert(ItemStack stack)
			{
				return BottomlessReceptacleScreenHandler.this.inventory.isValid(0, stack);
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

	public long getNumStoredItems()
	{
		return ((long)propertyDelegate.get(BottomlessReceptacleEntity.PROP_NUM_ITEMS_HI32) << 32) | propertyDelegate.get(BottomlessReceptacleEntity.PROP_NUM_ITEMS_LO32);
	}
}
