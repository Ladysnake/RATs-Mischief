package doctor4t.ratsmischief.client;

import doctor4t.ratsmischief.common.item.RatMasterArmorItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
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
}
