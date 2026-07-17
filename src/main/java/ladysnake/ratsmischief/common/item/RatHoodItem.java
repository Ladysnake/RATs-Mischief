package ladysnake.ratsmischief.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RatHoodItem extends RatArmorItem {
	public RatHoodItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
		super(material, slot, settings);
	}

	public static boolean isDown(ItemStack stack) {
		return stack.getOrCreateNbt().getBoolean("hidden");
	}

	@Override
	public void incrementType(ItemStack stack, boolean sneaking) {
		if (sneaking) {
			NbtCompound compound = stack.getOrCreateNbt();
			compound.putBoolean("hidden", !compound.getBoolean("hidden"));
			return;
		}
		super.incrementType(stack, false);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.ratsmischief.rat_master_hood.tooltip." + stack.getOrCreateNbt().getBoolean("hidden")).formatted(Formatting.GRAY));
		super.appendTooltip(stack, world, tooltip, context);
	}
}
