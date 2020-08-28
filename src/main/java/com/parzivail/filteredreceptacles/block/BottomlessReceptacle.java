package com.parzivail.filteredreceptacles.block;

import com.parzivail.filteredreceptacles.block.entity.BottomlessReceptacleEntity;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.tools.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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
		super(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES, 3).strength(25, 2).build());
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view)
	{
		return new BottomlessReceptacleEntity(this);
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
			if (itemStack.hasTag())
				tile.fromItemTag(itemStack.getTag());
		}
	}

	@Environment(EnvType.CLIENT)
	public void buildTooltip(ItemStack stack, @Nullable BlockView view, List<Text> tooltip, TooltipContext options)
	{
		CompoundTag compoundTag = stack.getTag();
		if (compoundTag != null)
		{
			if (compoundTag.contains("Items", 9))
			{
				DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(3, ItemStack.EMPTY);
				Inventories.fromTag(compoundTag, defaultedList);

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
				blockEntity.markInvalid();
			}
			return null;
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!world.isClient)
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof BottomlessReceptacleEntity)
			{
				ContainerProviderRegistry.INSTANCE.openContainer(Registry.BLOCK.getId(this), player, buf -> buf.writeBlockPos(pos));
			}
		}
		return ActionResult.SUCCESS;
	}
}
