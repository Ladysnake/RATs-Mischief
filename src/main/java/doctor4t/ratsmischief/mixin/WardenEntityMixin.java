package doctor4t.ratsmischief.mixin;

import doctor4t.ratsmischief.common.entity.RatEntity;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.mob.warden.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WardenEntity.class)
public class WardenEntityMixin {
	@Inject(method = "accepts", at = @At(value = "HEAD"), cancellable = true)
	private void mischief$noRats(ServerWorld world, GameEventListener listener, BlockPos pos, GameEvent event, GameEvent.Context context, CallbackInfoReturnable<Boolean> cir) {
		if (context.sourceEntity() instanceof RatEntity) {
			cir.setReturnValue(false);
		}
	}
}
