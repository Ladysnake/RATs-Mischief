package ladysnake.ratsmischief.mixin;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.block.entity.SculkSensorBlockEntity.VibrationCallback")
public class SculkSensorBlockEntityMixin {
	@Inject(method = "accepts", at = @At(value = "HEAD"), cancellable = true)
	private void mischief$noRats(ServerWorld world, BlockPos pos, GameEvent event, GameEvent.Context eventContext, CallbackInfoReturnable<Boolean> cir) {
		if (eventContext.sourceEntity() instanceof RatEntity) {
			cir.setReturnValue(false);
		}
	}
}
