package doctor4t.ratsmischief.mixin;

import doctor4t.ratsmischief.common.init.ModSoundEvents;
import doctor4t.ratsmischief.common.item.MasterRatArmorItem;
import doctor4t.ratsmischief.common.item.RatItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
	@Shadow
	@Final
	public DefaultedList<Slot> slots;

	@Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
	private void ratsmischief$toggleMode(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if (slotIndex >= 0 && slotIndex < this.slots.size()) {
			if (button == 1) {
				Slot slot = this.slots.get(slotIndex);
				ItemStack stack = slot.getStack();
				if (stack.getItem() instanceof MasterRatArmorItem armorItem) {
					boolean quickMove = actionType == SlotActionType.QUICK_MOVE;
					armorItem.incrementType(stack, quickMove);
					player.playSound(ModSoundEvents.RAT_ITEM_TOGGLE, SoundCategory.PLAYERS, 0.9f, 1.5f);
					ci.cancel();
				} else if (stack.getItem() instanceof RatItem) {
					RatItem.cycleRatReturn(stack);
					player.playSound(ModSoundEvents.RAT_ITEM_TOGGLE, SoundCategory.PLAYERS, 0.9f, 1.5f);
					ci.cancel();
				}
			}
		}
	}
}
