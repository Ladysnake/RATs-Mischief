package ladysnake.ratsmischief.client;

import ladysnake.ratsmischief.client.render.entity.RatEntityRenderer;
import ladysnake.ratsmischief.client.render.item.RatItemRenderer;
import ladysnake.ratsmischief.client.render.item.RatMasterMaskItemRenderer;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.common.init.ModParticles;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import xyz.amymialee.mialeemisc.MialeeMiscClient;

public class RatsMischiefClient implements ClientModInitializer {
	public static final EntityModelLayer RAT_MASTER_ARMOR_INNER_LAYER = new EntityModelLayer(RatsMischief.id("rat_master_inner_layer"), "main");
	public static final EntityModelLayer RAT_MASTER_ARMOR_OUTER_LAYER = new EntityModelLayer(RatsMischief.id("rat_master_outer_layer"), "main");
	public static final EntityModelLayer RAT_MASTER_ARMOR_OUTER_LAYER_SLIM = new EntityModelLayer(RatsMischief.id("rat_master_outer_layer_slim"), "main");

	static {
		ModelPredicateProviderRegistry.register(ModItems.RAT_MASTER_OCARINA, new Identifier("action"), (stack, world, entity, seed) -> stack.getOrCreateNbt().getInt("action") / 4f);
	}

	@Override
	public void onInitializeClient(ModContainer mod) {
		EntityModelLayerRegistry.registerModelLayer(RAT_MASTER_ARMOR_INNER_LAYER, () -> TexturedModelData.of(PlayerEntityModel.getTexturedModelData(new Dilation(0.28f), false), 64, 64));
		EntityModelLayerRegistry.registerModelLayer(RAT_MASTER_ARMOR_OUTER_LAYER, () -> TexturedModelData.of(PlayerEntityModel.getTexturedModelData(new Dilation(0.29f), false), 64, 64));
		EntityModelLayerRegistry.registerModelLayer(RAT_MASTER_ARMOR_OUTER_LAYER_SLIM, () -> TexturedModelData.of(PlayerEntityModel.getTexturedModelData(new Dilation(0.29f), true), 64, 64));

		ModParticles.registerFactories();
		ModParticles.init();

		EntityRendererRegistry.register(ModEntities.RAT, RatEntityRenderer::new);
		GeoItemRenderer.registerItemRenderer(ModItems.RAT, new RatItemRenderer());

		Identifier itemId = Registry.ITEM.getId(ModItems.RAT_MASTER_MASK);
		RatMasterMaskItemRenderer inventoryItemRenderer = new RatMasterMaskItemRenderer(itemId);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(inventoryItemRenderer);
		BuiltinItemRendererRegistry.INSTANCE.register(ModItems.RAT_MASTER_MASK, inventoryItemRenderer);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(new ModelIdentifier(itemId, "inventory"));
			out.accept(new ModelIdentifier(itemId + "_worn", "inventory"));
		});
		MialeeMiscClient.INVENTORY_ITEMS.add(ModItems.RAT_MASTER_MASK);

		// model predicates
		FabricModelPredicateProviderRegistry.register(new Identifier(RatsMischief.MOD_ID + ":filled"), (itemStack, world, livingEntity, seed) -> itemStack.getOrCreateSubNbt(RatsMischief.MOD_ID).getFloat("filled"));


//		// block render layer map
//		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlock.MOD_BLOCK);

//		// entity renderer registration
//		EntityRendererRegistry.register(ModEntities.MOD_ENTITY, ModEntityRenderer::new);
	}
}
