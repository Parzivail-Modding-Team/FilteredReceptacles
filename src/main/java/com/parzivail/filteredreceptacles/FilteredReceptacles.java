package com.parzivail.filteredreceptacles;

import com.parzivail.filteredreceptacles.block.BasicReceptacle;
import com.parzivail.filteredreceptacles.block.BottomlessReceptacle;
import com.parzivail.filteredreceptacles.block.WasteReceptacle;
import com.parzivail.filteredreceptacles.block.entity.BasicReceptacleEntity;
import com.parzivail.filteredreceptacles.block.entity.BottomlessReceptacleEntity;
import com.parzivail.filteredreceptacles.block.entity.WasteReceptacleEntity;
import com.parzivail.filteredreceptacles.gui.BasicReceptacleScreen;
import com.parzivail.filteredreceptacles.gui.BottomlessReceptacleScreen;
import com.parzivail.filteredreceptacles.gui.WasteReceptacleScreen;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class FilteredReceptacles implements ModInitializer, ClientModInitializer
{
	public static final String MODID = "filteredreceptacles";

	public static final Identifier ID_RECEPTACLE_BASIC = new Identifier(MODID, "receptacle_basic");
	public static final BasicReceptacle BLOCK_RECEPTACLE_BASIC = new BasicReceptacle(FilterUtil.FilterLevel.Item);

	public static final Identifier ID_RECEPTACLE_STRICT = new Identifier(MODID, "receptacle_strict");
	public static final BasicReceptacle BLOCK_RECEPTACLE_STRICT = new BasicReceptacle(FilterUtil.FilterLevel.ItemAndData);

	public static final Identifier ID_RECEPTACLE_BOTTOMLESS = new Identifier(MODID, "receptacle_bottomless");
	public static final BottomlessReceptacle BLOCK_RECEPTACLE_BOTTOMLESS = new BottomlessReceptacle();
	public static final Identifier PACKET_BOTTOMLESS_UPDATE = new Identifier(MODID, "receptacle_bottomless_update");

	public static final Identifier ID_RECEPTACLE_WASTE = new Identifier(MODID, "receptacle_waste");
	public static final WasteReceptacle BLOCK_RECEPTACLE_WASTE = new WasteReceptacle();

	@Override
	public void onInitialize()
	{
		registerReceptacle(ID_RECEPTACLE_BASIC, BLOCK_RECEPTACLE_BASIC);
		registerReceptacle(ID_RECEPTACLE_STRICT, BLOCK_RECEPTACLE_STRICT);

		registerItem(ID_RECEPTACLE_BOTTOMLESS, BLOCK_RECEPTACLE_BOTTOMLESS, ItemGroup.DECORATIONS);
		Registry.register(Registry.BLOCK_ENTITY, ID_RECEPTACLE_BOTTOMLESS, BlockEntityType.Builder.create(() -> new BottomlessReceptacleEntity(BLOCK_RECEPTACLE_BOTTOMLESS), BLOCK_RECEPTACLE_BOTTOMLESS).build(null));
		registerContainerFactory(ID_RECEPTACLE_BOTTOMLESS);

		registerItem(ID_RECEPTACLE_WASTE, BLOCK_RECEPTACLE_WASTE, ItemGroup.DECORATIONS);
		Registry.register(Registry.BLOCK_ENTITY, ID_RECEPTACLE_WASTE, BlockEntityType.Builder.create(() -> new WasteReceptacleEntity(BLOCK_RECEPTACLE_WASTE), BLOCK_RECEPTACLE_WASTE).build(null));
		registerContainerFactory(ID_RECEPTACLE_WASTE);
	}

	private static void registerReceptacle(Identifier id, BasicReceptacle block)
	{
		registerItem(id, block, ItemGroup.DECORATIONS);
		Registry.register(Registry.BLOCK_ENTITY, id, BlockEntityType.Builder.create(() -> new BasicReceptacleEntity(block), block).build(null));
		registerContainerFactory(id);
	}

	private static void registerItem(Identifier id, Block block, ItemGroup group)
	{
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(group)));
	}

	private static void registerContainerFactory(Identifier id)
	{
		ContainerProviderRegistry.INSTANCE.registerFactory(id, (syncId, identifier, player, buf) -> {
			final World world = player.world;
			final BlockPos pos = buf.readBlockPos();
			return world.getBlockState(pos).createContainerProvider(world, pos).createMenu(syncId, player.inventory, player);
		});
	}

	@Override
	public void onInitializeClient()
	{
		ScreenProviderRegistry.INSTANCE.registerFactory(ID_RECEPTACLE_BASIC, BasicReceptacleScreen::new);
		ScreenProviderRegistry.INSTANCE.registerFactory(ID_RECEPTACLE_STRICT, BasicReceptacleScreen::new);
		ScreenProviderRegistry.INSTANCE.registerFactory(ID_RECEPTACLE_BOTTOMLESS, BottomlessReceptacleScreen::new);
		ScreenProviderRegistry.INSTANCE.registerFactory(ID_RECEPTACLE_WASTE, WasteReceptacleScreen::new);

		ClientSidePacketRegistry.INSTANCE.register(PACKET_BOTTOMLESS_UPDATE, (packetContext, attachedData) -> {
			BlockPos pos = attachedData.readBlockPos();
			long numItems = attachedData.readLong();
			packetContext.getTaskQueue().execute(() -> {
				assert MinecraftClient.getInstance().world != null;
				BlockEntity e = MinecraftClient.getInstance().world.getBlockEntity(pos);
				if (e instanceof BottomlessReceptacleEntity)
					((BottomlessReceptacleEntity)e).setNumItemsStored(numItems);
			});
		});
	}
}
