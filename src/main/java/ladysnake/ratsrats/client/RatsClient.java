package ladysnake.ratsrats.client;

import ladysnake.ratsrats.client.network.EntityDispatcher;
import ladysnake.ratsrats.client.renderer.entity.RatEntityRenderer;
import ladysnake.ratsrats.common.Rats;
import ladysnake.ratsrats.common.network.Packets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;

public class RatsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(Packets.SPAWN, EntityDispatcher::spawnFrom);

        EntityRendererRegistry.INSTANCE.register(Rats.RAT,
                (entityRenderDispatcher, context) -> new RatEntityRenderer(entityRenderDispatcher));


        // model predicates
        FabricModelPredicateProviderRegistry.register(new Identifier(Rats.MODID + ":filled"), (itemStack, world, livingEntity) -> itemStack.getOrCreateSubTag(Rats.MODID).getFloat("filled"));

    }
}
