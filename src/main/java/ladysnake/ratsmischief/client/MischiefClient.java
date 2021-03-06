package ladysnake.ratsmischief.client;

import ladysnake.ratsmischief.client.network.EntityDispatcher;
import ladysnake.ratsmischief.client.render.entity.RatEntityRenderer;
import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.network.Packets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class MischiefClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(Packets.SPAWN, EntityDispatcher::spawnFrom);

        EntityRendererRegistry.INSTANCE.register(Mischief.RAT,
                (entityRenderDispatcher, context) -> new RatEntityRenderer(entityRenderDispatcher));


        // model predicates
        FabricModelPredicateProviderRegistry.register(new Identifier(Mischief.MODID + ":filled"), (itemStack, world, livingEntity) -> itemStack.getOrCreateSubTag(Mischief.MODID).getFloat("filled"));

        // rat mask
        ArmorRenderingRegistry.registerTexture((livingEntity, itemStack, equipmentSlot, b, s, identifier) -> new Identifier(Mischief.MODID, "textures/models/armor/rat_mask_layer_1.png"), Mischief.RAT_MASK);
    }
}
