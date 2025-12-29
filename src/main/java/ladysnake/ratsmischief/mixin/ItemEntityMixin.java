package ladysnake.ratsmischief.mixin;

import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.common.item.RatItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
	public ItemEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow
	public abstract ItemStack getStack();

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void tick(CallbackInfo ci) {
		ItemStack stack = this.getStack();

		if (stack.isOf(ModItems.RAT)) {
			if (!this.getWorld().isClient) {
				NbtCompound ratTag = RatItem.getRatTag(stack, this.getWorld());
				RatEntity rat = ModEntities.RAT.create(this.getWorld());
				if (rat != null) {
					rat.readNbt(ratTag);
					rat.updatePosition(this.getX(), this.getY(), this.getZ());
					rat.setPos(this.getX(), this.getY(), this.getZ());
					this.getWorld().spawnEntity(rat);
					rat.setSitting(false);
					stack.decrement(1);
					ci.cancel();
				}
			}
		}
	}

	@Inject(method = "cannotPickup", at = @At("RETURN"), cancellable = true)
	public void cannotPickup(CallbackInfoReturnable<Boolean> cir) {
		if (this.getStack().isOf(ModItems.RAT)) {
			cir.setReturnValue(true);
		}
	}
}
