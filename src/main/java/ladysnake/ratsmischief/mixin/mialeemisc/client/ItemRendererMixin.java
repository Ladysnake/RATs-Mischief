package ladysnake.ratsmischief.mixin.mialeemisc.client;

import ladysnake.ratsmischief.common.init.ModItems;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

	@Shadow @Final private ItemModels models;

	@Inject(method = "getHeldItemModel", at = @At("HEAD"), cancellable = true)
	private void mialeeMisc$heldItemModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
		if (ModItems.RAT_MASTER_MASK.equals(stack.getItem())) {
			var bakedModel = this.models.getModelManager().getModel(new ModelIdentifier(new Identifier("minecraft:trident_in_hand"), "inventory"));
			var clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
			var bakedModel2 = bakedModel.getOverrides().apply(bakedModel, stack, clientWorld, entity, seed);
			cir.setReturnValue(bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2);
		}
	}
}
