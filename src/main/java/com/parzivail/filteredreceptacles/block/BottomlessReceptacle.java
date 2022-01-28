package com.parzivail.filteredreceptacles.block;

import com.parzivail.filteredreceptacles.FilteredReceptacles;
import com.parzivail.filteredreceptacles.block.entity.BottomlessReceptacleEntity;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import javax.annotation.Nullable;
import java.util.List;

public class BottomlessReceptacle extends BlockWithEntity implements InventoryProvider
{
	public BottomlessReceptacle()
	{
		super(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES, 3).strength(25, 2));
	}

	@org.jetbrains.annotations.Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new BottomlessReceptacleEntity(pos, state);
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player)
	{
		super.onBreak(world, pos, state, player);
	}

	public BlockEntityType<?> getEntityType()
	{
		return Registry.BLOCK_ENTITY_TYPE.get(Registry.BLOCK.getId(this));
	}

	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	public PistonBehavior getPistonBehavior(BlockState state)
	{
		return PistonBehavior.BLOCK;
	}

	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
	{
		super.onPlaced(world, pos, state, placer, itemStack);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BottomlessReceptacleEntity)
		{
			BottomlessReceptacleEntity tile = (BottomlessReceptacleEntity)blockEntity;
			if (itemStack.hasNbt())
				tile.readNbt(itemStack.getNbt());
		}
	}

	@Override
	@org.jetbrains.annotations.Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
		return world.isClient ? null : checkType(type, FilteredReceptacles.BLOCK_ENTITY_TYPE_RECEPTACLE_BOTTOMLESS, BottomlessReceptacleEntity::tick);
	}

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable BlockView view, List<Text> tooltip, TooltipContext options)
	{
		NbtCompound compoundTag = stack.getOrCreateNbt();
		if (compoundTag != null)
		{
			if (compoundTag.contains("Items", 9))
			{
				DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(3, ItemStack.EMPTY);
				Inventories.readNbt(compoundTag, defaultedList);

				ItemStack output = defaultedList.get(BottomlessReceptacleEntity.OUTPUT);
				ItemStack reference = defaultedList.get(BottomlessReceptacleEntity.REFERENCE);

				if (FilterUtil.IsEmpty(reference) || FilterUtil.IsEmpty(output))
					return;

				long numItems = compoundTag.getLong("numItemsStored");

				numItems += output.getCount();

				tooltip.add((new TranslatableText("container.filteredreceptacles.receptacle_bottomless.more", numItems, reference.getItem().getName())).formatted(Formatting.ITALIC));
			}
		}
	}

	@Override
	public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BottomlessReceptacleEntity)
		{
			return (BottomlessReceptacleEntity)blockEntity;
		}
		else
		{
			if (blockEntity != null)
			{
				blockEntity.markRemoved();
			}
			return null;
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!world.isClient)
		{
			NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
			if (screenHandlerFactory != null)
				player.openHandledScreen(screenHandlerFactory);
		}
		return ActionResult.SUCCESS;
	}
}
