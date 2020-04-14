package com.parzivail.filteredreceptacles.block.entity;

import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.block.BottomlessReceptacle;
import com.parzivail.filteredreceptacles.gui.container.BottomlessReceptacleContainer;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BottomlessReceptacleEntity extends LootableContainerBlockEntity implements SidedInventory, Tickable
{
	public static final int INPUT = 0;
	public static final int OUTPUT = 1;
	public static final int REFERENCE = 2;

	private final String i18nName;
	protected DefaultedList<ItemStack> inv;
	protected long numItemsStored = 0;

	public BottomlessReceptacleEntity(BottomlessReceptacle block)
	{
		super(block.getEntityType());
		this.i18nName = block.getTranslationKey();

		this.inv = DefaultedList.ofSize(3, ItemStack.EMPTY);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag)
	{
		tag = super.toTag(tag);
		Inventories.toTag(tag, inv, false);
		tag.putLong("numItemsStored", numItemsStored);
		return tag;
	}

	public CompoundTag serializeInventory(CompoundTag tag)
	{
		return tag;
	}

	public long getNumItemsStored()
	{
		return numItemsStored;
	}

	@Override
	protected Text getContainerName()
	{
		return new TranslatableText(i18nName);
	}

	@Override
	protected Container createContainer(int syncId, PlayerInventory playerInventory)
	{
		return new BottomlessReceptacleContainer(syncId, playerInventory, this);
	}

	public CompoundTag toInitialChunkDataTag()
	{
		return this.toTag(new CompoundTag());
	}

	@Override
	public void fromTag(CompoundTag tag)
	{
		super.fromTag(tag);
		Inventories.fromTag(tag, inv);
		numItemsStored = tag.getLong("numItemsStored");
		markDirty();
	}

	public void fromItemTag(CompoundTag tag)
	{
		Inventories.fromTag(tag, inv);
		numItemsStored = tag.getLong("numItemsStored");
		markDirty();
	}

	@Override
	public int[] getInvAvailableSlots(Direction side)
	{
		return IntStream.range(0, inv.size()).toArray();
	}

	@Override
	public boolean canInsertInvStack(int slot, ItemStack stack, Direction dir)
	{
		ItemStack ref = getInvStack(OUTPUT);
		return slot == 0 && (FilterUtil.IsEmpty(ref) || FilterUtil.AreEqual(ref, stack, FilterUtil.FilterLevel.ItemAndData));
	}

	@Override
	public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir)
	{
		return slot == 1;
	}

	@Override
	public int getInvSize()
	{
		return inv.size();
	}

	@Override
	public boolean isInvEmpty()
	{
		return inv.isEmpty();
	}

	@Override
	public ItemStack getInvStack(int slot)
	{
		if (slot < 0 || slot >= inv.size())
		{
			return ItemStack.EMPTY;
		}
		return inv.get(slot);
	}

	@Override
	public ItemStack takeInvStack(int slot, int amount)
	{
		markDirty();
		return Inventories.splitStack(this.inv, slot, amount);
	}

	@Override
	public ItemStack removeInvStack(int slot)
	{
		markDirty();
		return Inventories.removeStack(this.inv, slot);
	}

	@Override
	public void setInvStack(int slot, ItemStack stack)
	{
		if (slot < 0 || slot >= inv.size())
		{
			return;
		}

		this.inv.set(slot, stack);
		if (stack.getCount() > this.getInvMaxStackAmount())
		{
			stack.setCount(this.getInvMaxStackAmount());
		}
		markDirty();
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player)
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

	@Override
	public void tick()
	{
		if (this.world == null)
			return;

		long prevStored = numItemsStored;

		ItemStack inputStack = getInvStack(INPUT);
		if (!inputStack.isEmpty())
		{
			if (FilterUtil.IsEmpty(getInvStack(REFERENCE)))
				setInvStack(REFERENCE, inputStack.copy());

			if (!world.isClient)
				numItemsStored += inputStack.getCount();

			setInvStack(INPUT, ItemStack.EMPTY);
		}

		if (getInvStack(OUTPUT).getCount() < getInvStack(REFERENCE).getMaxCount() && numItemsStored > 0)
		{
			ItemStack outputStack;
			int outputSize;

			if (FilterUtil.IsEmpty(getInvStack(OUTPUT)))
			{
				outputStack = getInvStack(REFERENCE).copy();
				outputSize = 0;
			}
			else
			{
				outputStack = getInvStack(OUTPUT).copy();
				outputSize = outputStack.getCount();
			}

			int diff = (int)Math.min(numItemsStored, getInvStack(REFERENCE).getMaxCount() - outputSize);

			if (!world.isClient)
				numItemsStored -= diff;

			outputStack.setCount(outputSize + diff);

			setInvStack(OUTPUT, outputStack);
		}

		if (!FilterUtil.IsEmpty(getInvStack(REFERENCE)) && numItemsStored == 0 && getInvStack(INPUT).isEmpty() && getInvStack(OUTPUT).isEmpty())
			setInvStack(REFERENCE, ItemStack.EMPTY);

		if (prevStored != numItemsStored && !world.isClient)
		{
			Stream<PlayerEntity> watchingPlayers = PlayerStream.watching(world, pos);
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeBlockPos(this.pos);
			passedData.writeLong(numItemsStored);
			watchingPlayers.forEach(player -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, FilteredReceptacles.PACKET_BOTTOMLESS_UPDATE, passedData));
		}
	}
}
