package ladysnake.ratsmischief.mialeemisc;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.mialeemisc.items.IClickConsumingItem;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class MialeeMisc {

	public static void onInitialize() {
		PayloadTypeRegistry.playC2S().register(ClickConsumePayload.ID, ClickConsumePayload.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ClickConsumePayload.ID, (payload, context) -> {
			if (context.player().getMainHandStack().getItem() instanceof IClickConsumingItem item) {
				item.mialeeMisc$doAttack(context.player());
			}
		});
	}
}
