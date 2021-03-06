package ladysnake.ratsmischief.mixin;

import ladysnake.ratsmischief.common.Mischief;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onKilledBy", at = @At(value = "RETURN"))
    public void onKilledBy(LivingEntity adversary, CallbackInfo ci) {
        if (adversary instanceof PlayerEntity && this.getUuid().equals("1b44461a-f605-4b29-a7a9-04e649d1981c")) {
            this.dropStack(new ItemStack(Mischief.RAT_MASK));
        }
    }
}
