package ladysnake.ratsmischief.mixin;

import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.common.init.ModSoundEvents;
import ladysnake.ratsmischief.common.item.RatItem;
import ladysnake.ratsmischief.common.item.RatMasterArmorItem;
import ladysnake.ratsmischief.common.item.RatMasterMaskItem;
import ladysnake.ratsmischief.common.item.RatMasterOcarinaItem;
import ladysnake.ratsmischief.common.util.PlayerRatOwner;
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
	@Shadow
	@Final
	public DefaultedList<Slot> slots;

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
				} else if (stack.isOf(ModItems.RAT_MASTER_MASK)) {
					RatMasterMaskItem.incrementOffset(stack);
					player.playSound(ModSoundEvents.ITEM_RAT_TOGGLE, SoundCategory.PLAYERS, 0.9f, 1.5f);
					ci.cancel();
				} else if (stack.getItem() instanceof RatMasterOcarinaItem) {
					if (player instanceof PlayerRatOwner playerRatOwner) {
						playerRatOwner.mischief$setBringingItems(!playerRatOwner.mischief$shouldBringItems());
						player.playSound(ModSoundEvents.ITEM_RAT_TOGGLE, SoundCategory.PLAYERS, 0.9f, 1.5f);
						ci.cancel();
					}
				}
			}
		}
	}
}
