package ladysnake.ratsmischief.mixin.mialeemisc.client;

/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import ladysnake.ratsmischief.client.RatsMischiefClient;
import ladysnake.ratsmischief.common.init.ModItems;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;I)V", at = @At("HEAD"))
	private void mialeeMisc$nonheldItemModel(CallbackInfo ci, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) ItemDisplayContext renderMode, @Local(argsOnly = true) LocalRef<BakedModel> modelRef) {
		if (stack.isOf(ModItems.RAT_MASTER_MASK) && (renderMode == ItemDisplayContext.GUI || renderMode == ItemDisplayContext.GROUND || renderMode == ItemDisplayContext.FIXED)) {
			modelRef.set(this.models.getModelManager().getModel(RatsMischiefClient.RAT_MASTER_MASK));
		}
	}

	@WrapOperation(method = "getModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemModels;getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;"))
	private BakedModel mialeeMisc$heldItemModel(ItemModels instance, ItemStack stack, Operation<BakedModel> original) {
		if (stack.isOf(ModItems.RAT_MASTER_MASK)) {
			return this.models.getModelManager().getModel(RatsMischiefClient.RAT_MASTER_MASK_WORN);
		} else {
			return original.call(instance, stack);
		}
	}
}
*/
//TODO Fix this
