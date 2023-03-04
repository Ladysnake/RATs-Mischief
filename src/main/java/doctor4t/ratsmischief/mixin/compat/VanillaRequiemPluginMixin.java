package doctor4t.ratsmischief.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import doctor4t.ratsmischief.common.requiem.RatsMischiefRequiemPlugin;
import ladysnake.requiem.api.v1.remnant.RemnantComponent;
import ladysnake.requiem.common.VanillaRequiemPlugin;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VanillaRequiemPlugin.class)
public class VanillaRequiemPluginMixin {
	@ModifyExpressionValue(method = "refreshInventoryLocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityType;isIn(Lnet/minecraft/tag/TagKey;)Z"))
	private boolean allowMainInventory(boolean allow, PlayerEntity player) {
		if (RemnantComponent.get(player).getRemnantType() == RatsMischiefRequiemPlugin.RATIFIED_REMNANT_TYPE) {
			return true;
		}

		return allow;
	}
}
