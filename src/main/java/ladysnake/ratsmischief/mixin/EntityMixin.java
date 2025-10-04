package ladysnake.ratsmischief.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
	@WrapWithCondition(method = "tickBlockCollisions", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onSteppedOn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/Entity;)V"))
	private boolean mischief$notSteppedOn(Block instance, World world, BlockPos pos, BlockState state, Entity entity) {
		return !(entity instanceof RatEntity);
	}
}
