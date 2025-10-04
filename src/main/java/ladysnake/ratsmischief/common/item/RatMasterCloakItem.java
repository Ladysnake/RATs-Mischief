package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.common.init.ModDataComponents;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class RatMasterCloakItem extends RatMasterArmorItem {


	public RatMasterCloakItem(Settings settings) {
		super(settings);
	}

	public static boolean isStripped(ItemStack stack) {
		return Boolean.TRUE.equals(stack.get(ModDataComponents.USE_ALTERNATE_ARMOR));
	}

	@Override
	public void incrementType(ItemStack stack, boolean sneaking) {
		if (sneaking) {
			stack.set(ModDataComponents.USE_ALTERNATE_ARMOR, !stack.get(ModDataComponents.USE_ALTERNATE_ARMOR));
			return;
		}
		super.incrementType(stack, false);
	}
	//I know this is deprecated, but it feels better than having two separate boolean wrappers just for armor
	//Fabric should let you register tooltip appenders per component, not only as an interface
	@Override
	public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		textConsumer.accept(Text.translatable("item.ratsmischief.rat_master_cloak.tooltip." + isStripped(stack)).formatted(Formatting.GRAY));
	}

}

