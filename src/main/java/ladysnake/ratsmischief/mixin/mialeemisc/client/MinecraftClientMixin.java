package ladysnake.ratsmischief.mixin.mialeemisc.client;

import ladysnake.ratsmischief.mialeemisc.MialeeMisc;
import ladysnake.ratsmischief.mialeemisc.items.IClickConsumingItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.qsl.networking.api.PacketByteBufs;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow @Nullable public ClientPlayerEntity player;

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void mialeeMisc$cancelAttack(CallbackInfoReturnable<Boolean> cir) {
		if (this.player != null) {
			if (this.player.getMainHandStack().getItem() instanceof IClickConsumingItem) {
				ClientPlayNetworking.send(MialeeMisc.clickConsumePacket, PacketByteBufs.empty());
				cir.setReturnValue(false);
			}
		}
	}

}
