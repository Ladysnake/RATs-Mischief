package ladysnake.ratsmischief.client;

import ladysnake.ratsmischief.common.item.RatMasterArmorItem;
import ladysnake.ratsmischief.common.util.PlayerRatOwner;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.function.Consumer;

public class RatsMischiefClientHelper {
	public static void addSetBonus(Consumer<Text> tooltip) {
		if (RatMasterArmorItem.getEquippedPieces(MinecraftClient.getInstance().player) >= 4) {
			tooltip.accept(Text.translatable("item.ratsmischief.rat_master_set_bonus").formatted(Formatting.GRAY));
			tooltip.accept(Text.translatable("item.ratsmischief.rat_master_friendly_fire").formatted(Formatting.GRAY));
		}
	}

	public static boolean shouldRatsBringItems() {
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player instanceof PlayerRatOwner playerRatOwner) {
			return playerRatOwner.mischief$shouldBringItems();
		}
		return false;
	}
}
