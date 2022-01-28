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
import com.parzivail.filteredreceptacles.gui.container.BasicReceptacleScreenHandler;
import com.parzivail.filteredreceptacles.gui.container.BottomlessReceptacleScreenHandler;
import com.parzivail.filteredreceptacles.gui.container.WasteReceptacleScreenHandler;
import com.parzivail.filteredreceptacles.util.BlockEntityClientSerializable;
import com.parzivail.filteredreceptacles.util.FilterUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class FilteredReceptacles implements ModInitializer, ClientModInitializer
{
	public static final String MODID = "filteredreceptacles";

	public static final Identifier ID_RECEPTACLE_BASIC = new Identifier(MODID, "receptacle_basic");
	public static final BasicReceptacle BLOCK_RECEPTACLE_BASIC = new BasicReceptacle(FilterUtil.FilterLevel.Item);
	public static final ScreenHandlerType<BasicReceptacleScreenHandler> SH_RECEPTACLE_BASIC = ScreenHandlerRegistry.registerSimple(ID_RECEPTACLE_BASIC, BasicReceptacleScreenHandler::new);

	public static final Identifier ID_RECEPTACLE_STRICT = new Identifier(MODID, "receptacle_strict");
	public static final BasicReceptacle BLOCK_RECEPTACLE_STRICT = new BasicReceptacle(FilterUtil.FilterLevel.ItemAndData);
	public static final BlockEntityType<BasicReceptacleEntity> BLOCK_ENTITY_TYPE_RECEPTACLE_BASIC = FabricBlockEntityTypeBuilder.create(BasicReceptacleEntity::new, BLOCK_RECEPTACLE_BASIC, BLOCK_RECEPTACLE_STRICT).build();

	public static final Identifier ID_RECEPTACLE_BOTTOMLESS = new Identifier(MODID, "receptacle_bottomless");
	public static final BottomlessReceptacle BLOCK_RECEPTACLE_BOTTOMLESS = new BottomlessReceptacle();
	public static final BlockEntityType<BottomlessReceptacleEntity> BLOCK_ENTITY_TYPE_RECEPTACLE_BOTTOMLESS = FabricBlockEntityTypeBuilder.create(BottomlessReceptacleEntity::new, BLOCK_RECEPTACLE_BOTTOMLESS).build();
	public static final Identifier PACKET_BOTTOMLESS_UPDATE = new Identifier(MODID, "receptacle_bottomless_update");
	public static final ScreenHandlerType<BottomlessReceptacleScreenHandler> SH_RECEPTACLE_BOTTOMLESS = ScreenHandlerRegistry.registerSimple(ID_RECEPTACLE_BOTTOMLESS, BottomlessReceptacleScreenHandler::new);

	public static final Identifier ID_RECEPTACLE_WASTE = new Identifier(MODID, "receptacle_waste");
	public static final WasteReceptacle BLOCK_RECEPTACLE_WASTE = new WasteReceptacle();
	public static final BlockEntityType<WasteReceptacleEntity> BLOCK_ENTITY_TYPE_RECEPTACLE_WASTE = FabricBlockEntityTypeBuilder.create(WasteReceptacleEntity::new, BLOCK_RECEPTACLE_WASTE).build();
	public static final ScreenHandlerType<WasteReceptacleScreenHandler> SH_RECEPTACLE_WASTE = ScreenHandlerRegistry.registerSimple(ID_RECEPTACLE_WASTE, WasteReceptacleScreenHandler::new);

	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MODID, "general"), () -> new ItemStack(BLOCK_RECEPTACLE_BASIC));

	public static final Identifier ID_MATTER_TRANSDUCER = new Identifier(MODID, "matter_transducer");
	public static final Item MATTER_TRANSDUCER = new Item(new Item.Settings().group(ITEM_GROUP));

	public static final Identifier PACKET_CLIENT_SYNC = new Identifier(MODID, "becs_sync");

	@Override
	public void onInitialize()
	{
		registerItem(ID_RECEPTACLE_BASIC, BLOCK_RECEPTACLE_BASIC);
		registerItem(ID_RECEPTACLE_STRICT, BLOCK_RECEPTACLE_STRICT);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, ID_RECEPTACLE_BASIC, BLOCK_ENTITY_TYPE_RECEPTACLE_BASIC);

		registerItem(ID_RECEPTACLE_BOTTOMLESS, BLOCK_RECEPTACLE_BOTTOMLESS);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, ID_RECEPTACLE_BOTTOMLESS, BLOCK_ENTITY_TYPE_RECEPTACLE_BOTTOMLESS);

		registerItem(ID_RECEPTACLE_WASTE, BLOCK_RECEPTACLE_WASTE);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, ID_RECEPTACLE_WASTE, BLOCK_ENTITY_TYPE_RECEPTACLE_WASTE);

		Registry.register(Registry.ITEM, ID_MATTER_TRANSDUCER, MATTER_TRANSDUCER);
	}

	private static void registerItem(Identifier id, Block block)
	{
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ITEM_GROUP)));
	}

	@Override
	public void onInitializeClient()
	{
		ScreenRegistry.register(SH_RECEPTACLE_BASIC, BasicReceptacleScreen::new);
		ScreenRegistry.register(SH_RECEPTACLE_BOTTOMLESS, BottomlessReceptacleScreen::new);
		ScreenRegistry.register(SH_RECEPTACLE_WASTE, WasteReceptacleScreen::new);

		ClientPlayNetworking.registerGlobalReceiver(PACKET_BOTTOMLESS_UPDATE, (client, networkHandler, buf, sender) -> {
			BlockPos pos = buf.readBlockPos();
			long numItems = buf.readLong();
			client.execute(() -> {
				assert MinecraftClient.getInstance().world != null;
				BlockEntity e = MinecraftClient.getInstance().world.getBlockEntity(pos);
				if (e instanceof BottomlessReceptacleEntity)
					((BottomlessReceptacleEntity)e).setNumItemsStored(numItems);
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(PACKET_CLIENT_SYNC, BlockEntityClientSerializable::handle);
	}
}
