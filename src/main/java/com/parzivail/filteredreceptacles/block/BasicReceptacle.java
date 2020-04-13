package com.parzivail.filteredreceptacles.block;

import com.parzivail.filteredreceptacles.block.entity.BasicReceptacleEntity;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BasicReceptacle extends BlockWithEntity implements InventoryProvider
{
	protected final FilterUtil.FilterLevel filterLevel;

	public BasicReceptacle(FilterUtil.FilterLevel filterLevel)
	{
		super(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES, 1).strength(1, 2).build());
		this.filterLevel = filterLevel;
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view)
	{
		return new BasicReceptacleEntity(this);
	}

	public BlockEntityType<?> getEntityType()
	{
		return Registry.BLOCK_ENTITY.get(Registry.BLOCK.getId(this));
	}

	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	public FilterUtil.FilterLevel getFilterLevel()
	{
		return filterLevel;
	}

	@Override
	public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof BasicReceptacleEntity)
		{
			return (BasicReceptacleEntity)blockEntity;
		}
		else
		{
			if (blockEntity != null)
			{
				blockEntity.markInvalid();
			}
			return null;
		}
	}

	@Override
	public boolean hasComparatorOutput(BlockState blockState_1)
	{
		return true;
	}

	@Override
	public boolean hasBlockEntity()
	{
		return true;
	}

	public int getComparatorOutput(BlockState state, World world, BlockPos pos)
	{
		return Container.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	public void onBlockRemoved(BlockState blockState_1, World world_1, BlockPos blockPos_1, BlockState blockState_2, boolean boolean_1)
	{
		if (blockState_1.getBlock() != blockState_2.getBlock())
		{
			BlockEntity blockEntity_1 = world_1.getBlockEntity(blockPos_1);
			if (blockEntity_1 instanceof Inventory)
			{
				ItemScatterer.spawn(world_1, blockPos_1, (Inventory)blockEntity_1);
				world_1.updateHorizontalAdjacent(blockPos_1, this);
			}

			super.onBlockRemoved(blockState_1, world_1, blockPos_1, blockState_2, boolean_1);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!world.isClient)
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BasicReceptacleEntity)
			{
				ContainerProviderRegistry.INSTANCE.openContainer(Registry.BLOCK.getId(this), player, buf -> buf.writeBlockPos(pos));
			}
		}
		return ActionResult.SUCCESS;
	}
}
