package doctor4t.ratsmischief.mixin;

import doctor4t.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin extends TameableEntity {
	protected CatEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "initGoals", at = @At(value = "TAIL"))
	private void ratsmischief$fleeFromRats(CallbackInfo ci) {
		this.goalSelector.add(3, new FleeEntityGoal<>(this, RatEntity.class, 10, 2, 2.2));
	}
}
