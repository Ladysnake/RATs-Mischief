package doctor4t.ratsmischief.client;

import doctor4t.ratsmischief.common.item.RatMasterArmorItem;
import doctor4t.ratsmischief.common.util.PlayerRatOwner;
import doctor4t.ratsmischief.mixin.PlayerEntityMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RatsMischiefClientHelper {
	public static void addSetBonus(List<Text> tooltip) {
		if (RatMasterArmorItem.getEquippedPieces(MinecraftClient.getInstance().player) >= 4) {
			tooltip.add(Text.translatable("item.ratsmischief.rat_master_set_bonus").formatted(Formatting.GRAY));
			tooltip.add(Text.translatable("item.ratsmischief.rat_master_friendly_fire").formatted(Formatting.GRAY));
		}
	}

	public static boolean shouldRatsBringItems() {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player instanceof PlayerRatOwner playerRatOwner) {
			return playerRatOwner.shouldBringItems();
		}
		return true;
	}
}
