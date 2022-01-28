package com.parzivail.filteredreceptacles.block.entity;

import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.gui.container.WasteReceptacleScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.stream.IntStream;

public class WasteReceptacleEntity extends LootableContainerBlockEntity implements SidedInventory
{
	protected static final int INPUT = 0;

	private final String i18nName;
	protected DefaultedList<ItemStack> inv;
	protected long numItemsStored = 0;

	public WasteReceptacleEntity(BlockPos blockPos, BlockState blockState)
	{
		super(FilteredReceptacles.BLOCK_ENTITY_TYPE_RECEPTACLE_WASTE, blockPos, blockState);
		this.i18nName = blockState.getBlock().getTranslationKey();

		this.inv = DefaultedList.ofSize(1, ItemStack.EMPTY);
	}

	@Override
	protected Text getContainerName()
	{
		return new TranslatableText(i18nName);
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		return new WasteReceptacleScreenHandler(syncId, playerInventory, this);
	}

	@Override
	public int[] getAvailableSlots(Direction side)
	{
		return IntStream.range(0, inv.size()).toArray();
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir)
	{
		return true;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir)
	{
		return false;
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

	public void setNumItemsStored(long numItemsStored)
	{
		this.numItemsStored = numItemsStored;
	}

	public static void tick(World world, BlockPos pos, BlockState state, WasteReceptacleEntity blockEntity)
	{
		if (world == null)
			return;

		if (!blockEntity.getStack(WasteReceptacleEntity.INPUT).isEmpty())
			blockEntity.setStack(WasteReceptacleEntity.INPUT, ItemStack.EMPTY);
	}
}
