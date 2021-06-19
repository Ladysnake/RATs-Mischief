package ladysnake.ratsmischief.client;

import ladysnake.ratsmischief.client.render.entity.RatEntityRenderer;
import ladysnake.ratsmischief.common.Mischief;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class MischiefClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(Mischief.RAT, RatEntityRenderer::new);

        // model predicates
        FabricModelPredicateProviderRegistry.register(new Identifier(Mischief.MODID + ":filled"), (itemStack, world, livingEntity, seed) -> itemStack.getOrCreateSubTag(Mischief.MODID).getFloat("filled"));

        // rat mask, no need to register texture anymore
//        ArmorRenderingRegistry.registerTexture((livingEntity, itemStack, equipmentSlot, b, s, identifier) -> new Identifier(Mischief.MODID, "textures/models/armor/rat_mask_layer_1.png"), Mischief.RAT_MASK);
    }
}
