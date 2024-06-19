package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.mialeemisc.util.MialeeText;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.minecraft.text.Style.EMPTY;

public class RatPouchItem extends Item {
	private static final Predicate<RatEntity> CLOSEST_RAT_PREDICATE = (ratEntity) -> ratEntity.isTamed();
	private final int size;

	public RatPouchItem(Settings settings, int size) {
		super(settings);
		this.size = size;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient() && user.isSneaking()) {
			if (user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).getFloat("filled") == 1f) {
				for (NbtElement ratTag : user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).getList("rats", NbtType.COMPOUND)) {
					RatEntity rat = ModEntities.RAT.create(world);
					rat.readNbt((NbtCompound) ratTag);
					rat.updatePosition(user.getX(), user.getY(), user.getZ());
					rat.setPos(user.getX(), user.getY(), user.getZ());
					world.spawnEntity(rat);
				}

				user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).put("rats", new NbtList());
				user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).putFloat("filled", 0F);

				return TypedActionResult.success(user.getStackInHand(hand));
			} else {
				NbtList NbtList = user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).getList("rats", NbtType.COMPOUND);

				List<RatEntity> closestTamedRats = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16.0D), CLOSEST_RAT_PREDICATE);
				List<RatEntity> closestOwnedRats = closestTamedRats.stream().filter(ratEntity -> ratEntity.getOwnerUuid() != null && ratEntity.getOwnerUuid().equals(user.getUuid())).collect(Collectors.toList());

				if (closestOwnedRats.size() > 0) {
					for (int i = 0; i < this.size; i++) {
						if (i < closestOwnedRats.size()) {
							NbtCompound nbt = new NbtCompound();
							closestOwnedRats.get(i).saveNbt(nbt);
//							nbt.remove("UUID");
							NbtList.add(nbt);
							closestOwnedRats.get(i).playSpawnEffects();
							closestOwnedRats.get(i).remove(Entity.RemovalReason.DISCARDED);
						} else {
							break;
						}
					}

					user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).put("rats", NbtList);
					user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).putFloat("filled", 1F);
					return TypedActionResult.success(user.getStackInHand(hand));
				} else {
					return TypedActionResult.fail(user.getStackInHand(hand));
				}
			}
		}

		return super.use(world, user, hand);
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		NbtList NbtList = user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).getList("rats", NbtElement.COMPOUND_TYPE);

		if (NbtList.size() < this.size && entity instanceof RatEntity rat && rat.getOwnerUuid() != null && rat.getOwnerUuid().equals(user.getUuid())) {
			NbtCompound NbtCompound = new NbtCompound();
			entity.saveNbt(NbtCompound);
			NbtList.add(NbtCompound);
			user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).put("rats", NbtList);
			rat.playSpawnEffects();
			entity.remove(Entity.RemovalReason.DISCARDED);
			user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).putFloat("filled", 1F);

			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		var NbtList = stack.getOrCreateSubNbt(RatsMischief.MOD_ID).getList("rats", NbtElement.COMPOUND_TYPE);

		tooltip.add(Text.translatable("item.ratsmischief.rat_pouch.tooltip.capacity", NbtList.size(), this.size).setStyle(EMPTY.withColor(Formatting.GRAY)));

		for (var ratTag : NbtList) {
			var style = EMPTY.withColor(Formatting.DARK_GRAY);
			if (((NbtCompound) ratTag).getString("RatType").equals(RatEntity.Type.GOLD.name())) {
				style = EMPTY.withColor(Formatting.GOLD);
			}
			var ratType = Text.translatable("type.ratsmischief." + ((NbtCompound) ratTag).getString("RatType").toLowerCase()).setStyle(style);

			// name
			var text = ratType;
			if (((NbtCompound) ratTag).contains("CustomName")) {
				var matcher = Pattern.compile("\\{\"text\":\"(.+)\"\\}").matcher(((NbtCompound) ratTag).getString("CustomName"));
				if (matcher.find()) {
					var name = matcher.group(1);
					text = Text.literal(name).append(" (").append(ratType).append(")");
				}
			}

			// spy
			if (((NbtCompound) ratTag).getBoolean("Spy")) {
				text = text.append(" (").append(Text.translatable("item.ratsmischief.rat.tooltip.spy").setStyle(EMPTY.withColor(Formatting.DARK_GREEN))).append(")");
			}

			// potion genes
			var potionId = Identifier.tryParse(((NbtCompound) ratTag).getString("PotionGene"));
			var statusEffect = Registries.STATUS_EFFECT.get(potionId);
			if (statusEffect != null) {
				text = text.append(" (").append(Text.translatable("item.ratsmischief.rat.tooltip.potion").setStyle(EMPTY.withColor(Formatting.GRAY)).append(MialeeText.withColor(Text.translatable(statusEffect.getTranslationKey()).setStyle(EMPTY), statusEffect.getColor()))).append(")");
			}

			tooltip.add(text);
		}

		super.appendTooltip(stack, world, tooltip, context);
	}
}
