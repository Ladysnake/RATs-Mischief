package doctor4t.ratsmischief.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import doctor4t.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.event.listener.SculkSensorListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SculkSensorListener.Callback.class)
public class SculkSensorListenerMixin {
	@WrapOperation(method = "isValid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isSpectator()Z"))
	private boolean mischief$noRats(Entity entity, Operation<Boolean> operation) {
		if (entity instanceof RatEntity) {
			return false;
		}
		return operation.call(entity);
	}
}
