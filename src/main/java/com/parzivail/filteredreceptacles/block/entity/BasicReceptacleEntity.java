package com.parzivail.filteredreceptacles.block.entity;

import com.parzivail.filteredreceptacles.block.BasicReceptacle;
import com.parzivail.filteredreceptacles.gui.container.BasicReceptacleContainer;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

import java.util.stream.IntStream;

public class BasicReceptacleEntity extends LootableContainerBlockEntity implements SidedInventory
{
	protected final FilterUtil.FilterLevel filterLevel;
	protected final String i18nName;
	protected DefaultedList<ItemStack> inv;

	public BasicReceptacleEntity(BasicReceptacle block)
	{
		super(block.getEntityType());
		this.filterLevel = block.getFilterLevel();
		this.i18nName = block.getTranslationKey();

		this.inv = DefaultedList.ofSize(6, ItemStack.EMPTY);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		tag = super.toTag(tag);
		Inventories.toTag(tag, inv);
		return tag;
	}

	@Override
	protected Text getContainerName()
	{
		return new TranslatableText(i18nName);
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		return new BasicReceptacleContainer(syncId, playerInventory, this);
	}

	@Override
	public void fromTag(BlockState state, CompoundTag tag)
	{
		super.fromTag(state, tag);
		Inventories.fromTag(tag, inv);
		markDirty();
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
