package ladysnake.ratsmischief.common.item;

import com.mojang.serialization.Codec;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import io.netty.buffer.ByteBuf;
import ladysnake.ratsmischief.common.init.ModDataComponents;
import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.mialeemisc.util.MialeeMath;
import ladysnake.ratsmischief.mialeemisc.util.MialeeText;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.function.Consumer;

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
		return stack.get(ModDataComponents.RAT_MASTER_MASK_OFFSET).offset();
	}

	public static void incrementOffset(ItemStack stack) {
		stack.set(ModDataComponents.RAT_MASTER_MASK_OFFSET, new Offset(MialeeMath.clampLoop(stack.get(ModDataComponents.RAT_MASTER_MASK_OFFSET).offset() + 1, -2, 3)));
	}

	@Override
	public Text getName(ItemStack stack) {
		return MialeeText.withColor(super.getName(stack), 0x87FFBF);
	}

	public record Offset(int offset) implements TooltipAppender {
		public static final Codec<Offset> CODEC = Codec.INT.xmap(Offset::new, Offset::offset);
		public static final PacketCodec<ByteBuf, Offset> PACKET_CODEC = PacketCodecs.INTEGER.xmap(Offset::new, Offset::offset);

		@Override
		public void appendTooltip(TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
			textConsumer.accept(Text.translatable("item.ratsmischief.rat_master_mask.desc1").formatted(Formatting.GRAY));
			textConsumer.accept(Text.translatable("item.ratsmischief.rat_master_friendly_fire").formatted(Formatting.GRAY));
			textConsumer.accept(Text.translatable("item.ratsmischief.rat_master_mask.desc.offset1").formatted(Formatting.GRAY));
			textConsumer.accept(Text.translatable("item.ratsmischief.rat_master_mask.desc.offset2", offset).formatted(Formatting.GRAY));

		}
	}
}
