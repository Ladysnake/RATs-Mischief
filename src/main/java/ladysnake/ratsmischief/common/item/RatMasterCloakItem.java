package ladysnake.ratsmischief.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RatMasterCloakItem extends RatMasterArmorItem {
	public RatMasterCloakItem(ArmorMaterial material, ArmorSlot slot, Settings settings) {
		super(material, slot, settings);
	}

	public static boolean isStripped(ItemStack stack) {
		return stack.getOrCreateNbt().getBoolean("stripped");
	}

	@Override
	public void incrementType(ItemStack stack, boolean sneaking) {
		if (sneaking) {
			NbtCompound compound = stack.getOrCreateNbt();
			compound.putBoolean("stripped", !compound.getBoolean("stripped"));
			return;
		}
		super.incrementType(stack, false);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.ratsmischief.rat_master_cloak.tooltip." + stack.getOrCreateNbt().getBoolean("stripped")).formatted(Formatting.GRAY));
		super.appendTooltip(stack, world, tooltip, context);
	}
}
