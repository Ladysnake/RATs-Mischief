package ladysnake.ratsmischief.client;

import ladysnake.ratsmischief.client.render.entity.RatEntityRenderer;
import ladysnake.ratsmischief.client.render.item.RatMasterMaskItemRenderer;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.common.init.ModParticles;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.resource.loader.api.ResourceLoader;

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

		Identifier itemId = Registries.ITEM.getId(ModItems.RAT_MASTER_MASK);
		RatMasterMaskItemRenderer inventoryItemRenderer = new RatMasterMaskItemRenderer(itemId);
		ResourceLoader.get(ResourceType.CLIENT_RESOURCES).registerReloader(inventoryItemRenderer);
		BuiltinItemRendererRegistry.INSTANCE.register(ModItems.RAT_MASTER_MASK, inventoryItemRenderer);
		ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.addModels(new ModelIdentifier(itemId, "inventory"));
            pluginContext.addModels(new ModelIdentifier(new Identifier(itemId + "_worn"), "inventory"));
        });

		// model predicates
		ModelPredicateProviderRegistry.register(new Identifier(RatsMischief.MOD_ID + ":filled"), (itemStack, world, livingEntity, seed) -> itemStack.getOrCreateSubNbt(RatsMischief.MOD_ID).getFloat("filled"));

//		// block render layer map
//		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlock.MOD_BLOCK);

//		// entity renderer registration
//		EntityRendererRegistry.register(ModEntities.MOD_ENTITY, ModEntityRenderer::new);
	}
}
