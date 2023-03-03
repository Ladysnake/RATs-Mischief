package doctor4t.ratsmischief.client;

import doctor4t.ratsmischief.client.render.entity.RatEntityRenderer;
import doctor4t.ratsmischief.client.render.item.RatItemRenderer;
import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.init.ModEntities;
import doctor4t.ratsmischief.common.init.ModItems;
import doctor4t.ratsmischief.common.init.ModParticles;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RatsMischiefClient implements ClientModInitializer {
	public static final EntityModelLayer MASTER_RAT_ARMOR_INNER_LAYER = new EntityModelLayer(RatsMischief.id("master_rat_inner_layer"), "main");
	public static final EntityModelLayer MASTER_RAT_ARMOR_OUTER_LAYER = new EntityModelLayer(RatsMischief.id("master_rat_outer_layer"), "main");
	public static final EntityModelLayer MASTER_RAT_ARMOR_OUTER_LAYER_SLIM = new EntityModelLayer(RatsMischief.id("master_rat_outer_layer_slim"), "main");

	@Override
	public void onInitializeClient(ModContainer mod) {
		EntityModelLayerRegistry.registerModelLayer(MASTER_RAT_ARMOR_INNER_LAYER, () -> TexturedModelData.of(PlayerEntityModel.getTexturedModelData(new Dilation(0.28f), false), 64, 64));
		EntityModelLayerRegistry.registerModelLayer(MASTER_RAT_ARMOR_OUTER_LAYER, () -> TexturedModelData.of(PlayerEntityModel.getTexturedModelData(new Dilation(0.29f), false), 64, 64));
		EntityModelLayerRegistry.registerModelLayer(MASTER_RAT_ARMOR_OUTER_LAYER_SLIM, () -> TexturedModelData.of(PlayerEntityModel.getTexturedModelData(new Dilation(0.29f), true), 64, 64));

		ModParticles.registerFactories();
		ModParticles.init();

		EntityRendererRegistry.register(ModEntities.RAT, RatEntityRenderer::new);
		GeoItemRenderer.registerItemRenderer(ModItems.RAT, new RatItemRenderer());

		// model predicates
		FabricModelPredicateProviderRegistry.register(new Identifier(RatsMischief.MOD_ID + ":filled"), (itemStack, world, livingEntity, seed) -> itemStack.getOrCreateSubNbt(RatsMischief.MOD_ID).getFloat("filled"));


//		// block render layer map
//		BlockRenderLayerMap.put(RenderLayer.getCutout(), ModBlock.MOD_BLOCK);

//		// entity renderer registration
//		EntityRendererRegistry.register(ModEntities.MOD_ENTITY, ModEntityRenderer::new);
	}

	static {
		ModelPredicateProviderRegistry.register(ModItems.RAT_STAFF, new Identifier("action"), (stack, world, entity, seed) -> stack.getOrCreateNbt().getInt("action") / 4f);
	}
}
