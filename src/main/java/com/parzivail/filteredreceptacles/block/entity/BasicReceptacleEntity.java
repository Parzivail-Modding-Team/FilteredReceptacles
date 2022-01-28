package com.parzivail.filteredreceptacles.block.entity;

import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.block.BasicReceptacle;
import com.parzivail.filteredreceptacles.gui.container.BasicReceptacleScreenHandler;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.stream.IntStream;

public class BasicReceptacleEntity extends LootableContainerBlockEntity implements SidedInventory
{
	public static final int PROP_FILTER_LEVEL = 0;
	protected final FilterUtil.FilterLevel filterLevel;
	protected final String i18nName;
	protected DefaultedList<ItemStack> inv;

	private final PropertyDelegate propertyDelegate = new PropertyDelegate()
	{
		@Override
		public int get(int index)
		{
			if (index == PROP_FILTER_LEVEL)
				return filterLevel.ordinal();
			return 0;
		}

		@Override
		public void set(int index, int value)
		{
		}

		@Override
		public int size()
		{
			return 1;
		}
	};

	public BasicReceptacleEntity(BlockPos blockPos, BlockState blockState)
	{
		super(FilteredReceptacles.BLOCK_ENTITY_TYPE_RECEPTACLE_BASIC, blockPos, blockState);
		var block = ((BasicReceptacle)blockState.getBlock());
		this.filterLevel = block.getFilterLevel();
		this.i18nName = block.getTranslationKey();

		this.inv = DefaultedList.ofSize(6, ItemStack.EMPTY);
	}

	@Override
	public void writeNbt(NbtCompound tag)
	{
		super.writeNbt(tag);
		Inventories.writeNbt(tag, inv);
	}

	@Override
	public void readNbt(NbtCompound tag)
	{
		super.readNbt(tag);
		Inventories.readNbt(tag, inv);
		markDirty();
	}

	@Override
	protected Text getContainerName()
	{
		return new TranslatableText(i18nName);
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		return new BasicReceptacleScreenHandler(syncId, playerInventory, this, propertyDelegate);
	}

	@Override
	public int[] getAvailableSlots(Direction side)
	{
		return IntStream.range(1, inv.size()).toArray();
	}

	public FilterUtil.FilterLevel getFilterLevel()
	{
		return filterLevel;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir)
	{
		return slot > 0 && FilterUtil.AreEqual(getStack(0), stack, filterLevel);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir)
	{
		return slot > 0;
	}

	@Override
	public int size()
	{
		return inv.size();
	}

	@Override
	public boolean isEmpty()
	{
		return inv.isEmpty();
	}

	@Override
	public ItemStack getStack(int slot)
	{
		if (slot < 0 || slot >= inv.size())
		{
			return ItemStack.EMPTY;
		}
		return inv.get(slot);
	}

	@Override
	public ItemStack removeStack(int slot, int amount)
	{
		markDirty();
		return Inventories.splitStack(this.inv, slot, amount);
	}

	@Override
	public ItemStack removeStack(int slot)
	{
		markDirty();
		return Inventories.removeStack(this.inv, slot);
	}

	@Override
	public void setStack(int slot, ItemStack stack)
	{
		if (slot < 0 || slot >= inv.size())
		{
			return;
		}

		this.inv.set(slot, stack);
		if (stack.getCount() > this.getMaxCountPerStack())
		{
			stack.setCount(this.getMaxCountPerStack());
		}
		markDirty();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player)
	{
		if (this.world == null)
			return false;

		if (this.world.getBlockEntity(this.pos) != this)
		{
			return false;
		}
		else
		{
			return player.squaredDistanceTo((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
		}
	}

	@Override
	public void clear()
	{
		inv.clear();
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList()
	{
		return inv;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> list)
	{
		inv = list;
	}
}
