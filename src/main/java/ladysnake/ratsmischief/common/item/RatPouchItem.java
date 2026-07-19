package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.mixin.PlayerInventoryAccessor;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

import static net.minecraft.text.Style.EMPTY;

public class RatPouchItem extends Item {
	private final int size;

	public RatPouchItem(Settings settings, int size) {
		super(settings);
		this.size = size;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (world instanceof ServerWorld serverWorld && user.isSneaking()) {
			ItemStack stack = user.getStackInHand(hand);
			TypedActionResult<ItemStack> itemStackTypedActionResult = usePouch(world, user, stack, PouchActionOverride.NONE);
			if (itemStackTypedActionResult.getResult() == ActionResult.SUCCESS) {
				float filled = stack.getOrCreateSubNbt(RatsMischief.MOD_ID).getFloat("filled");
				serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), filled == 0f ? SoundEvents.ITEM_BUNDLE_DROP_CONTENTS : SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.NEUTRAL, 1f, 1f);
				return itemStackTypedActionResult;
			}
		}

		return super.use(world, user, hand);
	}

	public enum PouchActionOverride {
		NONE, GATHER, RELEASE
	}

	@NotNull
	public static TypedActionResult<ItemStack> usePouch(World world, PlayerEntity user, ItemStack stack, PouchActionOverride actionOverride) {
		NbtCompound nbt = stack.getOrCreateSubNbt(RatsMischief.MOD_ID);
		boolean release = nbt.getFloat("filled") == 1f && actionOverride != PouchActionOverride.GATHER || actionOverride == PouchActionOverride.RELEASE;
		if (release) {
			int countSpawned = 0;
			if (world instanceof ServerWorld serverWorld) {
				NbtList newList = new NbtList();
				NbtList ratNbtList = nbt.getList("rats", NbtType.COMPOUND);
				for (NbtElement ratTag : ratNbtList) {
					NbtCompound nbtCompound = (NbtCompound) ratTag;
					if (nbtCompound.contains("KO") && nbtCompound.getBoolean("KO")) {
						newList.add(ratTag);
					} else {
						releaseRat((ServerPlayerEntity) user, nbtCompound);
						countSpawned++;
					}
				}

				nbt.put("rats", newList);
				nbt.putFloat("filled", 0F);
			}
			return countSpawned > 0 ? TypedActionResult.success(stack) : TypedActionResult.pass(stack);
		} else {
			List<RatEntity> closestOwnedRats = getClosestRatsInRadius(world, user, 20f);

			if (!closestOwnedRats.isEmpty()) {
				for (int i = 0; i < ((RatPouchItem) stack.getItem()).size; i++) {
					for (RatEntity closestOwnedRat : closestOwnedRats) {
						if (!storeRat(closestOwnedRat, stack, false)) {
							break;
						}
					}
				}
				return TypedActionResult.success(stack);
			} else {
				return TypedActionResult.fail(stack);
			}
		}
	}

	private static void releaseRat(ServerPlayerEntity user, NbtCompound ratNbt) {
		ServerWorld world = user.getWorld();
		RatEntity rat = ModEntities.RAT.create(world);
		cleanNbt(ratNbt);
		rat.readNbt(ratNbt);
		rat.updatePosition(user.getX(), user.getY(), user.getZ());
		rat.setPos(user.getX(), user.getY(), user.getZ());
		rat.setOwner(user); // Failsafe cause sometimes rats aren't the same owner, possibly due to nicknames?
		rat.setFromPouch(true);

		rat.setYaw(world.random.nextFloat() * 360);
		rat.setPitch(0f);
		Vec3d dir = rat.getRotationVector().normalize().multiply(.5f);
		rat.setVelocity(dir);

		rat.fallDistance = 0f;
		world.spawnEntity(rat);
	}

	public static List<RatEntity> getClosestRatsInRadius(World world, PlayerEntity user, float radius) {
		return world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(radius), rat -> rat.getOwner() != null && rat.getOwner().equals(user));
	}

	public static boolean hasKos(ItemStack stack) {
		for (NbtElement ratTag : stack.getOrCreateSubNbt(RatsMischief.MOD_ID).getList("rats", NbtType.COMPOUND)) {
			NbtCompound nbtCompound = (NbtCompound) ratTag;
			if (nbtCompound.contains("KO") && nbtCompound.getBoolean("KO")) {
				return true;
			}
		}
		return false;
	}

	public static int reviveKOs(ItemStack stack) {
		NbtList newList = new NbtList();
		int ratsRevived = 0;
		for (NbtElement ratTag : stack.getOrCreateSubNbt(RatsMischief.MOD_ID).getList("rats", NbtType.COMPOUND)) {
			NbtCompound nbtCompound = (NbtCompound) ratTag;
			if (nbtCompound.contains("KO") && nbtCompound.getBoolean("KO")) {
				((NbtCompound) ratTag).putBoolean("KO", false);
				ratsRevived++;
			}
			newList.add(ratTag);
		}

		stack.getOrCreateSubNbt(RatsMischief.MOD_ID).put("rats", newList);
		stack.getOrCreateSubNbt(RatsMischief.MOD_ID).putFloat("filled", newList.isEmpty() ? 0f : 1f);

		return ratsRevived;
	}

	public static int getFreeSlotCount(ServerPlayerEntity player) {
		List<DefaultedList<ItemStack>> combinedInventory = ((PlayerInventoryAccessor) player.getInventory()).ratsmischief$getCombinedInventory();
		int ret = 0;
		for (List<ItemStack> list : combinedInventory) {
			for (ItemStack itemStack : list) {
				if (itemStack.getItem() instanceof RatPouchItem) {
					ret += RatPouchItem.getFreeSlotCount(itemStack);
				}
			}
		}
		return ret;
	}

	public static boolean hasPouchesWithRats(ServerPlayerEntity player) {
		List<DefaultedList<ItemStack>> combinedInventory = ((PlayerInventoryAccessor) player.getInventory()).ratsmischief$getCombinedInventory();
		int ret = 0;
		for (List<ItemStack> list : combinedInventory) {
			for (ItemStack itemStack : list) {
				if (itemStack.getItem() instanceof RatPouchItem ratPouchItem&& RatPouchItem.getFreeSlotCount(itemStack) < ratPouchItem.size) {
					return true;
				}
			}
		}
		return false;
	}

	private static int getFreeSlotCount(ItemStack itemStack) {
		return ((RatPouchItem) itemStack.getItem()).size - itemStack.getOrCreateSubNbt(RatsMischief.MOD_ID).getList("rats", NbtType.COMPOUND).size();
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (entity instanceof RatEntity rat && rat.getOwnerUuid() != null && rat.getOwnerUuid().equals(user.getUuid())) {
			return storeRat(rat, user.getStackInHand(hand), false) ? ActionResult.SUCCESS : ActionResult.FAIL;
		} else {
			return ActionResult.PASS;
		}
	}

	public static boolean storeRat(RatEntity rat, ItemStack itemStack, boolean ko) {
		NbtList nbtList = itemStack.getOrCreateSubNbt(RatsMischief.MOD_ID).getList("rats", NbtElement.COMPOUND_TYPE);
		if (rat.world instanceof ServerWorld serverWorld && !rat.isRemoved() && nbtList.size() < ((RatPouchItem) itemStack.getItem()).size) {
			NbtCompound nbtCompound = new NbtCompound();
			NbtCompound nbt = itemStack.getOrCreateSubNbt(RatsMischief.MOD_ID);
			float filled = 1f;
			if (ko) {
				rat.setHealth(rat.getMaxHealth());
				nbtCompound.putBoolean("KO", true);
				filled = 0f;
			}
			rat.saveNbt(nbtCompound);
			cleanNbt(nbtCompound);
			nbtList.add(nbtCompound);
			nbt.put("rats", nbtList);

			serverWorld.spawnParticles(ParticleTypes.SMOKE, rat.getX(), rat.getY() + rat.getHeight() * .5, rat.getZ(), 3, 0.15, 0.15, 0.15, 0.f);
			serverWorld.playSound(null, rat.getX(), rat.getY(), rat.getZ(), SoundEvents.ITEM_BUNDLE_INSERT, SoundCategory.NEUTRAL, .15f, 1f);

			rat.discard();
			nbt.putFloat("filled", nbt.getFloat("filled") == 1f ? 1f : filled);
			return true;
		} else {
			return false;
		}
	}

	private static void cleanNbt(NbtCompound nbtCompound) {
		nbtCompound.remove("InLove");
		nbtCompound.remove("LoveCause");
		nbtCompound.remove("Leash");
		nbtCompound.remove("ActiveEffects");
		nbtCompound.remove("AbsorptionAmount");
		nbtCompound.remove("HurtTime");
		nbtCompound.remove("DeathTime");
		nbtCompound.remove("FallFlying");
		nbtCompound.remove("SleepingX");
		nbtCompound.remove("SleepingY");
		nbtCompound.remove("SleepingZ");
		nbtCompound.remove("Pos");
		nbtCompound.remove("Motion");
		nbtCompound.remove("Rotation");
		nbtCompound.remove("FallDistance");
		nbtCompound.remove("Fire");
		nbtCompound.remove("Air");
		nbtCompound.remove("OnGround");
		nbtCompound.remove("PortalCooldown");
		nbtCompound.remove("UUID");
		nbtCompound.remove("TicksFrozen");
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

			// KO
			if (((NbtCompound) ratTag).getBoolean("KO")) {
				text = text.append(Text.literal(" [KO]").setStyle(EMPTY.withColor(Formatting.DARK_RED)));
			}

			// potion genes
//			var potionId = new Identifier(((NbtCompound) ratTag).getString("PotionGene"));
//			var statusEffect = Registry.STATUS_EFFECT.get(potionId);
//			if (statusEffect != null) {
//				text = text.append(" (").append(Text.translatable("item.ratsmischief.rat.tooltip.potion").setStyle(EMPTY.withColor(Formatting.GRAY)).append(MialeeText.withColor(Text.translatable(statusEffect.getTranslationKey()).setStyle(EMPTY), statusEffect.getColor()))).append(")");
//			}

			tooltip.add(text);
		}

		super.appendTooltip(stack, world, tooltip, context);
	}
}
