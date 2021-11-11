package ladysnake.ratsmischief.mixin;

import ladysnake.ratsmischief.common.entity.LivingEntityInterface;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "damageArmor", at = @At("HEAD"), cancellable = true)
    public void capRatArmorDamage(DamageSource source, float amount, CallbackInfo ci) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof RatEntity) {
            ci.cancel();
        }
    }

}
