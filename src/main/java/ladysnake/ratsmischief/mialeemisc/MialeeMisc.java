package ladysnake.ratsmischief.mialeemisc;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.mialeemisc.items.IClickConsumingItem;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class MialeeMisc {
	public static final Identifier clickConsumePacket = RatsMischief.id("click_consume");

	public static void onInitialize() {
		ServerPlayNetworking.registerGlobalReceiver(clickConsumePacket, (minecraftServer, serverPlayer, serverPlayNetworkHandler, packetByteBuf, packetSender) -> minecraftServer.execute(() -> {
			if (serverPlayer.getMainHandStack().getItem() instanceof IClickConsumingItem item) {
				item.mialeeMisc$doAttack(serverPlayer);
			}
		}));
	}
}
