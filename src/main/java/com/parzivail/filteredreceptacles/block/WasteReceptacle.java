package com.parzivail.filteredreceptacles.block;

import com.parzivail.filteredreceptacles.block.entity.WasteReceptacleEntity;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class WasteReceptacle extends BlockWithEntity implements InventoryProvider
{
	public WasteReceptacle()
	{
		super(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES, 3).strength(12, 2).build());
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view)
	{
		return new WasteReceptacleEntity(this);
	}

	public BlockEntityType<?> getEntityType()
	{
		return Registry.BLOCK_ENTITY_TYPE.get(Registry.BLOCK.getId(this));
	}

	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof WasteReceptacleEntity)
		{
			return (WasteReceptacleEntity)blockEntity;
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

	public int getComparatorOutput(BlockState state, World world, BlockPos pos)
	{
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
	{
		if (!state.isOf(newState.getBlock()))
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory)
			{
				ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
				world.updateComparators(pos, this);
			}

			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!world.isClient)
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof WasteReceptacleEntity)
			{
				ContainerProviderRegistry.INSTANCE.openContainer(Registry.BLOCK.getId(this), player, buf -> buf.writeBlockPos(pos));
			}
		}
		return ActionResult.SUCCESS;
	}
}
