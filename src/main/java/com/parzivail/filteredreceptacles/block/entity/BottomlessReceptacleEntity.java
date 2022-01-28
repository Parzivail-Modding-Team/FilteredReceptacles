package com.parzivail.filteredreceptacles.block.entity;

import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.gui.container.BottomlessReceptacleScreenHandler;
import com.parzivail.filteredreceptacles.util.BlockEntityClientSerializable;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.stream.IntStream;

public class BottomlessReceptacleEntity extends LootableContainerBlockEntity implements SidedInventory, BlockEntityClientSerializable
{
	public static final int INPUT = 0;
	public static final int OUTPUT = 1;
	public static final int REFERENCE = 2;

	public static final int PROP_NUM_ITEMS_LO32 = 0;
	public static final int PROP_NUM_ITEMS_HI32 = 1;

	protected final String i18nName;
	protected DefaultedList<ItemStack> inv;
	protected long numItemsStored = 0;

	private final PropertyDelegate propertyDelegate = new PropertyDelegate()
	{
		@Override
		public int get(int index)
		{
			if (index == PROP_NUM_ITEMS_LO32)
				return (int)numItemsStored;
			else if (index == PROP_NUM_ITEMS_HI32)
				return (int)(numItemsStored >>> 32);
			return 0;
		}

		@Override
		public void set(int index, int value)
		{
		}

		@Override
		public int size()
		{
			return 2;
		}
	};

	public BottomlessReceptacleEntity(BlockPos blockPos, BlockState blockState)
	{
		super(FilteredReceptacles.BLOCK_ENTITY_TYPE_RECEPTACLE_BOTTOMLESS, blockPos, blockState);
		this.i18nName = blockState.getBlock().getTranslationKey();

		this.inv = DefaultedList.ofSize(3, ItemStack.EMPTY);
	}

	@Override
	public void writeNbt(NbtCompound tag)
	{
		super.writeNbt(tag);
		Inventories.writeNbt(tag, inv);
		tag.putLong("numItemsStored", numItemsStored);
	}

	@Override
	public void readNbt(NbtCompound tag)
	{
		super.readNbt(tag);
		Inventories.readNbt(tag, inv);
		numItemsStored = tag.getLong("numItemsStored");
		markDirty();
	}

	@Override
	public void fromClientTag(NbtCompound tag)
	{
		numItemsStored = tag.getLong("numItemsStored");
	}

	@Override
	public NbtCompound toClientTag(NbtCompound tag)
	{
		tag.putLong("numItemsStored", numItemsStored);
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
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory)
	{
		return new BottomlessReceptacleScreenHandler(syncId, playerInventory, this, propertyDelegate);
	}

	@Override
	public int[] getAvailableSlots(Direction side)
	{
		return IntStream.range(0, inv.size()).toArray();
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, Direction dir)
	{
		ItemStack ref = getStack(OUTPUT);
		return slot == 0 && (FilterUtil.IsEmpty(ref) || FilterUtil.AreEqual(ref, stack, FilterUtil.FilterLevel.ItemAndData));
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir)
	{
		return slot == 1;
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

	public static void tick(World world, BlockPos pos, BlockState state, BottomlessReceptacleEntity blockEntity)
	{
		if (world == null)
			return;

		long prevStored = blockEntity.numItemsStored;

		ItemStack inputStack = blockEntity.getStack(INPUT);
		if (!inputStack.isEmpty())
		{
			if (FilterUtil.IsEmpty(blockEntity.getStack(REFERENCE)))
				blockEntity.setStack(REFERENCE, inputStack.copy());

			if (!world.isClient)
				blockEntity.numItemsStored += inputStack.getCount();

			blockEntity.setStack(INPUT, ItemStack.EMPTY);
		}

		if (blockEntity.getStack(OUTPUT).getCount() < blockEntity.getStack(REFERENCE).getMaxCount() && blockEntity.numItemsStored > 0)
		{
			ItemStack outputStack;
			int outputSize;

			if (FilterUtil.IsEmpty(blockEntity.getStack(OUTPUT)))
			{
				outputStack = blockEntity.getStack(REFERENCE).copy();
				outputSize = 0;
			}
			else
			{
				outputStack = blockEntity.getStack(OUTPUT).copy();
				outputSize = outputStack.getCount();
			}

			int diff = (int)Math.min(blockEntity.numItemsStored, blockEntity.getStack(REFERENCE).getMaxCount() - outputSize);

			if (!world.isClient)
				blockEntity.numItemsStored -= diff;

			outputStack.setCount(outputSize + diff);

			blockEntity.setStack(OUTPUT, outputStack);
		}

		if (!FilterUtil.IsEmpty(blockEntity.getStack(REFERENCE)) && blockEntity.numItemsStored == 0 && blockEntity.getStack(INPUT).isEmpty() && blockEntity.getStack(OUTPUT).isEmpty())
			blockEntity.setStack(REFERENCE, ItemStack.EMPTY);

		if (prevStored != blockEntity.numItemsStored && !world.isClient)
		{
			Collection<ServerPlayerEntity> watchingPlayers = PlayerLookup.tracking(blockEntity);
			PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
			passedData.writeBlockPos(pos);
			passedData.writeLong(blockEntity.numItemsStored);
			watchingPlayers.forEach(player -> ServerPlayNetworking.send(player, FilteredReceptacles.PACKET_BOTTOMLESS_UPDATE, passedData));
		}
	}
}
