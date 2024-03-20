package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.client.render.item.RatItemRenderer;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.mialeemisc.util.MialeeText;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.text.Style.EMPTY;

public class RatItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

	public RatItem(Settings settings) {
		super(settings);
		SingletonGeoAnimatable.registerSyncedAnimatable(this);
	}

	public static void cycleRatReturn(ItemStack stack) {
		NbtCompound ratTag = getRatTag(stack);
		if (ratTag == null) {
			return;
		}
		if (ratTag.getBoolean("Spy")) {
			ratTag.putBoolean("ShouldReturnToOwnerInventory", false);
		}
		ratTag.putBoolean("ShouldReturnToOwnerInventory", !ratTag.getBoolean("ShouldReturnToOwnerInventory"));
	}

	public static String getRatName(ItemStack stack) {
		return stack.hasCustomName() ? stack.getName().getString() : null;
	}

	@Nullable
	public static NbtCompound getRatTag(ItemStack stack) {
		NbtCompound subNbt = stack.getOrCreateSubNbt(RatsMischief.MOD_ID);
		if (subNbt.contains("rat")) {
			return subNbt.getCompound("rat");
		}
		return null;
	}

	public static NbtCompound getRatTag(ItemStack stack, World world) {
		NbtCompound ratTag = getRatTag(stack);
		if (ratTag == null) {
			RatEntity rat = new RatEntity(ModEntities.RAT, world);
			NbtCompound nbt = new NbtCompound();
			rat.saveNbt(nbt);
			stack.getOrCreateSubNbt(RatsMischief.MOD_ID).put("rat", nbt);
			ratTag = stack.getOrCreateSubNbt(RatsMischief.MOD_ID).getCompound("rat");
		}
		return ratTag;
	}

	public static RatEntity.Type getRatType(ItemStack stack) {
		NbtCompound ratTag = getRatTag(stack);
		if (ratTag == null) {
			return RatEntity.Type.WILD;
		}
		String ratType = ratTag.getString("RatType").toUpperCase();
		return RatEntity.Type.byName(ratType, RatEntity.Type.WILD);
	}

	public static DyeColor getRatColor(ItemStack stack) {
		NbtCompound ratTag = getRatTag(stack);
		if (ratTag == null) {
			return DyeColor.WHITE;
		}
		String ratColor = ratTag.getString("Color").toUpperCase();
		return DyeColor.byName(ratColor.toLowerCase(), DyeColor.WHITE);
	}

	@Override
	public void createRenderer(Consumer<Object> consumer) {
		consumer.accept(new RenderProvider() {
			private RatItemRenderer renderer;

			@Override
			public BuiltinModelItemRenderer getCustomRenderer() {
				if (this.renderer == null)
					renderer = new RatItemRenderer();

				return renderer;
			}
		});
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public Supplier<Object> getRenderProvider() {
		return this.renderProvider;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "idle", 20, state -> PlayState.CONTINUE));
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
			RatEntity rat = this.getRatFromItem(world, user.getStackInHand(hand), new Vec3d(user.getX(), user.getEyeY() - 0.1, user.getZ()),
				hand == Hand.OFF_HAND ? PlayerInventory.OFF_HAND_SLOT : user.getInventory().getSlotWithStack(user.getStackInHand(hand)));
			if (rat == null) {
				return TypedActionResult.fail(user.getStackInHand(hand));
			}
			rat.sendFlying(user, user.getPitch(), user.getYaw(), user.getRoll(), 3f, 1f);
			world.spawnEntity(rat);
			user.setStackInHand(hand, ItemStack.EMPTY);
			return TypedActionResult.success(user.getStackInHand(hand));
		}
		return super.use(world, user, hand);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity owner = context.getPlayer();
		Hand hand = context.getHand();
		World world = context.getWorld();

		world.spawnEntity(this.getRatFromItem(world, owner.getStackInHand(hand), context.getHitPos(),
			hand == Hand.OFF_HAND ? PlayerInventory.OFF_HAND_SLOT : owner.getInventory().getSlotWithStack(owner.getStackInHand(hand))));
		owner.setStackInHand(hand, ItemStack.EMPTY);

		return ActionResult.SUCCESS;
	}

	public RatEntity getRatFromItem(World world, ItemStack ratItemStack, Vec3d spawnPos, int slot) {
		NbtCompound ratTag = getRatTag(ratItemStack, world);
		RatEntity rat = ModEntities.RAT.create(world);
		if (rat != null) {
			rat.readNbt(ratTag);
			rat.updatePosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
			rat.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
			rat.setSitting(false);

			// custom name
			if (ratItemStack.hasCustomName()) {
				rat.setCustomName(ratItemStack.getName());
			}

			// set slot to come back to
			rat.setSlot(slot);
		}
		return rat;
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		var ratTag = getRatTag(stack, world);
		var ratType = Text.translatable("type.ratsmischief." + getRatType(stack).name().toLowerCase());

		var style = EMPTY.withColor(Formatting.DARK_GRAY);
		if (ratTag.getString("RatType").equals(RatEntity.Type.GOLD.name())) {
			style = EMPTY.withColor(Formatting.GOLD);
		}

		tooltip.add(ratType.setStyle(style));

		// spy
		if (ratTag.getBoolean("Spy")) {
			tooltip.add(Text.translatable("item.ratsmischief.rat.tooltip.spy").setStyle(EMPTY.withColor(Formatting.DARK_GREEN)));
		}

		// potion genes
		var potionId = Identifier.tryParse(ratTag.getString("PotionGene"));
		var statusEffect = Registries.STATUS_EFFECT.get(potionId);
		if (statusEffect != null) {
			tooltip.add(Text.translatable("item.ratsmischief.rat.tooltip.potion").setStyle(EMPTY.withColor(Formatting.GRAY)).append(MialeeText.withColor(Text.translatable(statusEffect.getTranslationKey()).setStyle(EMPTY), statusEffect.getColor())));
		}

		// set to return
		if (ratTag.getBoolean("ShouldReturnToOwnerInventory")) {
			tooltip.add(Text.translatable("item.ratsmischief.rat.tooltip.return").setStyle(EMPTY.withItalic(true).withColor(Formatting.GRAY)));
		}

		super.appendTooltip(stack, world, tooltip, context);
	}
}
