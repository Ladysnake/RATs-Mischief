package doctor4t.ratsmischief.mixin;

import doctor4t.ratsmischief.common.init.ModSoundEvents;
import doctor4t.ratsmischief.common.item.RatItem;
import doctor4t.ratsmischief.common.item.RatMasterArmorItem;
import doctor4t.ratsmischief.common.item.RatMasterMaskItem;
import doctor4t.ratsmischief.common.item.RatMasterOcarinaItem;
import doctor4t.ratsmischief.common.util.PlayerRatOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
	@Shadow @Final public DefaultedList<Slot> slots;

	@Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
	private void ratsmischief$toggleMode(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		if (slotIndex >= 0 && slotIndex < this.slots.size()) {
			if (button == 1) {
				Slot slot = this.slots.get(slotIndex);
				ItemStack stack = slot.getStack();
				if (stack.getItem() instanceof RatMasterArmorItem armorItem) {
					boolean quickMove = actionType == SlotActionType.QUICK_MOVE;
					armorItem.incrementType(stack, quickMove);
					player.playSound(ModSoundEvents.ITEM_RAT_TOGGLE, SoundCategory.PLAYERS, 0.9f, 1.5f);
					ci.cancel();
				} else if (stack.getItem() instanceof RatItem) {
					NbtCompound ratTag = RatItem.getRatTag(stack);
					if (ratTag == null || !ratTag.getBoolean("Spy")) {
						RatItem.cycleRatReturn(stack);
						player.playSound(ModSoundEvents.ITEM_RAT_TOGGLE, SoundCategory.PLAYERS, 0.9f, 1.5f);
						ci.cancel();
					}
				} else if (stack.getItem() instanceof RatMasterMaskItem) {
					RatMasterMaskItem.incrementOffset(stack);
					player.playSound(ModSoundEvents.ITEM_RAT_TOGGLE, SoundCategory.PLAYERS, 0.9f, 1.5f);
					ci.cancel();
				} else if (stack.getItem() instanceof RatMasterOcarinaItem) {
					if (player instanceof PlayerRatOwner playerRatOwner) {
						playerRatOwner.setBringingItems(!playerRatOwner.shouldBringItems());
						player.playSound(ModSoundEvents.ITEM_RAT_TOGGLE, SoundCategory.PLAYERS, 0.9f, 1.5f);
						ci.cancel();
					}
				}
			}
		}
	}
}
