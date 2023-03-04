package doctor4t.ratsmischief.mixin;

import doctor4t.ratsmischief.common.util.PlayerRatOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerRatOwner {
	@Unique private boolean shouldRatsBringItems = true;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "damageArmor", at = @At("HEAD"), cancellable = true)
    public void ratsmischef$removeRatArmorDamage(DamageSource source, float amount, CallbackInfo ci) {
        if (Objects.equals(source.getName(), "rat")) {
            ci.cancel();
        }
    }

	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void ratsmischef$writeCustomDataToNbt(net.minecraft.nbt.NbtCompound nbt, CallbackInfo ci) {
		nbt.putBoolean("shouldRatsBringItems", this.shouldRatsBringItems);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void ratsmischef$readCustomDataFromNbt(net.minecraft.nbt.NbtCompound nbt, CallbackInfo ci) {
		this.shouldRatsBringItems = nbt.getBoolean("shouldRatsBringItems");
	}

	@Override
	public boolean shouldBringItems() {
		return this.shouldRatsBringItems;
	}

	@Override
	public void setBringingItems(boolean bringingItems) {
		this.shouldRatsBringItems = bringingItems;
	}
}
