package ladysnake.ratsmischief.common.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import ladysnake.ratsmischief.client.RatsMischiefClientHelper;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.init.ModDataComponents;
import ladysnake.ratsmischief.common.init.ModTags;
import ladysnake.ratsmischief.mialeemisc.util.MialeeMath;
import ladysnake.ratsmischief.mialeemisc.util.MialeeText;
import ladysnake.ratsmischief.mixin.ArmorMaterialsMixin;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.Set;
import java.util.function.Consumer;

public class RatMasterArmorItem extends Item {
	public static final RegistryKey<EquipmentAsset> ASSET_KEY = RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, RatsMischief.id("rat_master"));

	public static final ArmorMaterial MATERIAL = new ArmorMaterial(38,
		ArmorMaterialsMixin.createDefenseMap(3, 6, 8, 3, 11),
		16,
		SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
		2f,
		0f,
		ModTags.REPAIRS_RAT_MASTER_ARMOR,
		ASSET_KEY);
	public static final Set<EquipmentSlot> SLOTS = Set.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

	public RatMasterArmorItem(Settings settings) {
		super(settings);
	}

	public static MasterArmorBoost getType(ItemStack stack) {
		return stack.get(ModDataComponents.MASTER_ARMOR_BOOST);
	}

	public static float getEquippedPieces(LivingEntity owner) {
		if (owner != null) {
			int equipped = 0;
			for (EquipmentSlot slot : RatMasterArmorItem.SLOTS) {
				if (owner.getEquippedStack(slot).getItem() instanceof RatMasterArmorItem) {
					equipped++;
				}
			}
			if (RatMasterMaskItem.isWearingMask(owner)) {
				equipped += 4;
			}
			return equipped;
		}
		return 0;
	}

	public static float getResistanceMultiplier(LivingEntity owner) {
		if (owner != null) {
			float bonus = 0.0f;
			for (EquipmentSlot slot : RatMasterArmorItem.SLOTS) {
				if (RatMasterArmorItem.getType(owner.getEquippedStack(slot)) == MasterArmorBoost.RESISTANCE) {
					bonus += 0.15f;
				}
			}
			return 1.0f - bonus;
		}
		return 1f;
	}

	public static float getDamageMultiplier(LivingEntity owner) {
		if (owner != null) {
			float bonus = 0.0f;
			for (EquipmentSlot slot : RatMasterArmorItem.SLOTS) {
				if (RatMasterArmorItem.getType(owner.getEquippedStack(slot)) == MasterArmorBoost.DAMAGE) {
					bonus += 0.25f;
				}
			}
			return 1.0f + bonus;
		}
		return 1f;
	}

	public static float getMiningSpeedMultiplier(LivingEntity owner) {
		if (owner != null) {
			float bonus = 0.0f;
			for (EquipmentSlot slot : RatMasterArmorItem.SLOTS) {
				if (RatMasterArmorItem.getType(owner.getEquippedStack(slot)) == MasterArmorBoost.MINING_SPEED) {
					bonus += 0.25f;
				}
			}
			return 1.0f + bonus;
		}
		return 1f;
	}

	public void incrementType(ItemStack stack, boolean sneaking) {
		MasterArmorBoost masterArmorBoost = getType(stack);
		int i = MialeeMath.clampLoop(masterArmorBoost.ordinal() + 1, 0, MasterArmorBoost.values().length);
		stack.set(ModDataComponents.MASTER_ARMOR_BOOST, MasterArmorBoost.values()[i]);
	}

	public enum MasterArmorBoost implements TooltipAppender {
		NONE,
		RESISTANCE,
		DAMAGE,
		MINING_SPEED;
		public static final Codec<MasterArmorBoost> CODEC = Codec.intRange(0, 3).xmap(integer -> MasterArmorBoost.values()[integer], Enum::ordinal);
		public static final PacketCodec<ByteBuf, MasterArmorBoost> PACKET_CODEC =PacketCodecs.INTEGER.xmap(integer -> MasterArmorBoost.values()[integer], Enum::ordinal);

		@Override
		public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
			switch (this) {
				case RESISTANCE ->
					textConsumer.accept(MialeeText.withColor(Text.translatable("item.ratsmischief.rat_master_armor.tooltip.resistance"), 10044730));
				case DAMAGE ->
					textConsumer.accept(MialeeText.withColor(Text.translatable("item.ratsmischief.rat_master_armor.tooltip.damage"), 9643043));
				case MINING_SPEED ->
					textConsumer.accept(MialeeText.withColor(Text.translatable("item.ratsmischief.rat_master_armor.tooltip.mining_speed"), 14270531));
			}
			RatsMischiefClientHelper.addSetBonus(textConsumer);
		}
	}


}
