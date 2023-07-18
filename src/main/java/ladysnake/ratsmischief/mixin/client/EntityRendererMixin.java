package ladysnake.ratsmischief.mixin.client;

import ladysnake.ratsmischief.common.item.RatMasterMaskItem;
import ladysnake.ratsmischief.common.util.EntityRendererWrapper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> implements EntityRendererWrapper {
	@Unique
	private EntityRendererFactory.Context context;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void mischief$init(EntityRendererFactory.Context ctx, CallbackInfo ci) {
		this.context = ctx;
	}

	@Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
	protected void mischief$hideNames(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (entity instanceof LivingEntity living && RatMasterMaskItem.isWearingMask(living)) {
			ci.cancel();
		}
	}

	@Override
	public EntityRendererFactory.Context getContext() {
		return this.context;
	}
}
