package ladysnake.ratsmischief.common.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.init.ModDataComponents;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.mialeemisc.util.MialeeText;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static net.minecraft.text.Style.EMPTY;

public class RatPouchItem extends Item {
	private static final Predicate<RatEntity> CLOSEST_RAT_PREDICATE = TameableEntity::isTamed;

	public RatPouchItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World w, PlayerEntity user, Hand hand) {
		if (w instanceof ServerWorld world && user.isSneaking()) {
			ItemStack stack = user.getStackInHand(hand);
			if (Boolean.TRUE.equals(stack.get(ModDataComponents.RAT_POUCH_FILLED))) {
				for (NbtCompound ratTag : stack.get(ModDataComponents.STORED_RATS).rats()) {
					RatEntity rat = ModEntities.RAT.create(world, SpawnReason.SPAWN_ITEM_USE);
					rat.readData(NbtReadView.create(ErrorReporter.EMPTY, world.getRegistryManager(), ratTag));
					rat.updatePosition(user.getX(), user.getY(), user.getZ());
					rat.setPos(user.getX(), user.getY(), user.getZ());
					world.spawnEntity(rat);
				}

				stack.set(ModDataComponents.STORED_RATS, new StoredRats(List.of()));
				stack.set(ModDataComponents.RAT_POUCH_FILLED, false);

				return ActionResult.SUCCESS;
			} else {
				List<NbtCompound> list = new ArrayList<>(stack.get(ModDataComponents.STORED_RATS).rats());

				List<RatEntity> closestTamedRats = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16.0D), CLOSEST_RAT_PREDICATE);
				List<RatEntity> closestOwnedRats = closestTamedRats.stream().filter(ratEntity -> ratEntity.getOwnerReference() != null && ratEntity.getOwnerReference().uuidEquals(user)).toList();

				if (!closestOwnedRats.isEmpty()) {
					for (int i = 0; i < stack.get(ModDataComponents.RAT_POUCH_CAPACITY); i++) {
						if (i < closestOwnedRats.size()) {
							NbtWriteView writeView = NbtWriteView.create(ErrorReporter.EMPTY, user.getWorld().getRegistryManager());
							closestOwnedRats.get(i).writeData(writeView);
							NbtCompound nbt = writeView.getNbt();
							nbt.remove("UUID");
							list.add(nbt);
							closestOwnedRats.get(i).playSpawnEffects();
							closestOwnedRats.get(i).remove(Entity.RemovalReason.DISCARDED);
						} else {
							break;
						}
					}

					stack.set(ModDataComponents.STORED_RATS, new StoredRats(list));
					stack.set(ModDataComponents.RAT_POUCH_FILLED, true);
					return ActionResult.SUCCESS;
				} else {
					return ActionResult.FAIL;
				}
			}
		}

		return super.use(w, user, hand);
	}
	public int getSize(ItemStack stack){
		return stack.get(ModDataComponents.RAT_POUCH_CAPACITY);
	}
	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		List<NbtCompound> list = new ArrayList<>(stack.get(ModDataComponents.STORED_RATS).rats());
		int size = getSize(stack);
		if (list.size() < size && entity instanceof RatEntity rat && rat.getOwnerReference() != null && rat.getOwnerReference().uuidEquals(user)) {
			NbtWriteView writeView = NbtWriteView.create(ErrorReporter.EMPTY, user.getWorld().getRegistryManager());
			rat.writeData(writeView);
			list.add(writeView.getNbt());
			stack.set(ModDataComponents.STORED_RATS, new StoredRats(list));
			rat.playSpawnEffects();
			entity.remove(Entity.RemovalReason.DISCARDED);
			stack.set(ModDataComponents.RAT_POUCH_FILLED, true);
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}
	public record StoredRats(List<NbtCompound> rats) implements TooltipAppender {
		public static final Codec<StoredRats> CODEC = NbtCompound.CODEC.listOf().xmap(StoredRats::new, StoredRats::rats);
		public static final PacketCodec<ByteBuf, StoredRats> PACKET_CODEC = PacketCodecs.NBT_COMPOUND.collect(PacketCodecs.toList()).xmap(StoredRats::new, StoredRats::rats);

		@Override
		public void appendTooltip(TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
			textConsumer.accept(Text.translatable("item.ratsmischief.rat_pouch.tooltip.capacity", rats.size(), components.get(ModDataComponents.RAT_POUCH_CAPACITY)).setStyle(EMPTY.withColor(Formatting.GRAY)));

			for (NbtCompound ratTag : rats) {
				var style = EMPTY.withColor(Formatting.DARK_GRAY);
				if (ratTag.getString("RatType").equals(RatEntity.Type.GOLD.name())) {
					style = EMPTY.withColor(Formatting.GOLD);
				}
				var ratType = Text.translatable("type.ratsmischief." + ratTag.getString("RatType", "").toLowerCase()).setStyle(style);

				// name
				var text = ratType;
				if (ratTag.contains("CustomName")) {
					var matcher = Pattern.compile("\\{\"text\":\"(.+)\"\\}").matcher(ratTag.getString("CustomName", ""));
					if (matcher.find()) {
						var name = matcher.group(1);
						text = Text.literal(name).append(" (").append(ratType).append(")");
					}
				}

				// spy
				if (ratTag.getBoolean("Spy", false)) {
					text = text.append(" (").append(Text.translatable("item.ratsmischief.rat.tooltip.spy").setStyle(EMPTY.withColor(Formatting.DARK_GREEN))).append(")");
				}

				// potion genes
				var potionId = Identifier.tryParse(ratTag.getString("PotionGene", ""));
				var statusEffect = Registries.STATUS_EFFECT.get(potionId);
				if (statusEffect != null) {
					text = text.append(" (").append(Text.translatable("item.ratsmischief.rat.tooltip.potion").setStyle(EMPTY.withColor(Formatting.GRAY)).append(MialeeText.withColor(Text.translatable(statusEffect.getTranslationKey()).setStyle(EMPTY), statusEffect.getColor()))).append(")");
				}

				textConsumer.accept(text);
			}
		}
	}
}
