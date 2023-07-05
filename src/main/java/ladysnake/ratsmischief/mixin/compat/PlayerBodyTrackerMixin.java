package ladysnake.ratsmischief.mixin.compat;

import ladysnake.ratsmischief.common.requiem.RatsMischiefRequiemPlugin;
import ladysnake.requiem.api.v1.record.GlobalRecord;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.common.remnant.PlayerBodyTracker;
import ladysnake.requiem.common.remnant.RemnantTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerBodyTracker.class)
public class PlayerBodyTrackerMixin {
	@Shadow
	@Final
	private PlayerEntity player;

	@Inject(method = "updateBodyHealth", at = @At(value = "INVOKE", target = "Lladysnake/requiem/common/network/RequiemNetworking;sendAnchorDamageMessage(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", shift = At.Shift.AFTER))
	private void getBackToBody(GlobalRecord anchor, CallbackInfo ci) {
		if (RemnantComponent.get(this.player).getRemnantType() == RatsMischiefRequiemPlugin.SPYING_RAT_REMNANT_TYPE) {
			RatsMischiefRequiemPlugin.goBackToBody((ServerPlayerEntity) this.player);
		}
	}

	@Inject(method = "onBodyDisappeared", at = @At(value = "FIELD", target = "Lladysnake/requiem/common/remnant/PlayerBodyTracker;previousAnchorHealth:F", shift = At.Shift.AFTER), remap = false)
	private void reset(CallbackInfo ci) {
		if (RemnantComponent.get(this.player).getRemnantType() == RatsMischiefRequiemPlugin.SPYING_RAT_REMNANT_TYPE) {
			RemnantComponent.get(this.player).become(RemnantTypes.MORTAL);
			this.player.kill();
		}
	}
}
