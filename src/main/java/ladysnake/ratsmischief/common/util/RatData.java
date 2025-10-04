package ladysnake.ratsmischief.common.util;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.item.RatItem;
import ladysnake.ratsmischief.mialeemisc.util.MialeeText;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static net.minecraft.text.Style.EMPTY;

public record RatData(NbtCompound ratTag) implements TooltipAppender {
	public static final Codec<RatData> CODEC = NbtCompound.CODEC.xmap(RatData::new, RatData::ratTag);
	public static final PacketCodec<ByteBuf, RatData> PACKET_CODEC = PacketCodecs.NBT_COMPOUND.xmap(RatData::new, RatData::ratTag);

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components){
		var ratType = Text.translatable("type.ratsmischief." + RatItem.getRatType(ratTag).name().toLowerCase());

		var style = EMPTY.withColor(Formatting.DARK_GRAY);
		if (ratTag.getString("RatType").equals(RatEntity.Type.GOLD.name())) {
			style = EMPTY.withColor(Formatting.GOLD);
		}

		textConsumer.accept(ratType.setStyle(style));

		// spy
		if (ratTag.getBoolean("Spy", false)) {
			textConsumer.accept(Text.translatable("item.ratsmischief.rat.tooltip.spy").setStyle(EMPTY.withColor(Formatting.DARK_GREEN)));
		}

		// potion genes
		var potionId = Identifier.tryParse(ratTag.getString("PotionGene", ""));
		var statusEffect = Registries.STATUS_EFFECT.get(potionId);
		if (statusEffect != null) {
			textConsumer.accept(Text.translatable("item.ratsmischief.rat.tooltip.potion").setStyle(EMPTY.withColor(Formatting.GRAY)).append(MialeeText.withColor(Text.translatable(statusEffect.getTranslationKey()).setStyle(EMPTY), statusEffect.getColor())));
		}

		// set to return
		if (ratTag.getBoolean("ShouldReturnToOwnerInventory", false)) {
			textConsumer.accept(Text.translatable("item.ratsmischief.rat.tooltip.return").setStyle(EMPTY.withItalic(true).withColor(Formatting.GRAY)));
		}

	}
}
