package ladysnake.ratsmischief.common.item;

import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.mialeemisc.util.MialeeMath;
import ladysnake.ratsmischief.mialeemisc.util.MialeeText;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RatMasterMaskItem extends TrinketItem {
	public RatMasterMaskItem(Settings settings) {
		super(settings);
	}

	public static boolean isWearingMask(LivingEntity livingEntity) {
		return getWornMask(livingEntity) != ItemStack.EMPTY;
	}

	public static ItemStack getWornMask(LivingEntity livingEntity) {
		return TrinketsApi.getTrinketComponent(livingEntity).flatMap(h -> {
			var results = h.getEquipped(ModItems.RAT_MASTER_MASK);
			if (results.isEmpty()) {
				return Optional.empty();
			} else {
				return Optional.of(results.get(0).getRight());
			}
		}).orElse(ItemStack.EMPTY);
	}

	public static int getOffset(ItemStack stack) {
		if (stack.getNbt() == null) {
			return 0;
		}
		return stack.getNbt().getInt("offset");
	}

	public static void incrementOffset(ItemStack stack) {
		NbtCompound compound = stack.getOrCreateNbt();
		compound.putInt("offset", MialeeMath.clampLoop(compound.getInt("offset") + 1, -2, 3));
	}

	@Override
	public Text getName(ItemStack stack) {
		return MialeeText.withColor(super.getName(stack), 0x87FFBF);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.ratsmischief.rat_master_mask.desc1").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("item.ratsmischief.rat_master_friendly_fire").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("item.ratsmischief.rat_master_mask.desc.offset1").formatted(Formatting.GRAY));
		if (stack.getNbt() != null) {
			tooltip.add(Text.translatable("item.ratsmischief.rat_master_mask.desc.offset2", stack.getNbt().getInt("offset")).formatted(Formatting.GRAY));
		}
		super.appendTooltip(stack, world, tooltip, context);
	}
}
