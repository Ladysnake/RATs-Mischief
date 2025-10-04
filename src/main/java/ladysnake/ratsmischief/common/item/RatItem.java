package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.client.render.item.RatItemRenderer;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.init.ModDataComponents;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.mialeemisc.util.MialeeText;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.text.Style.EMPTY;

public class RatItem extends Item implements GeoItem {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public RatItem(Settings settings) {
		super(settings);
		GeoItem.registerSyncedAnimatable(this);
	}

	public static void cycleRatReturn(ItemStack stack) {
		NbtCompound ratTag = getRatTag(stack);
		if (ratTag == null) {
			return;
		}
		if (ratTag.getBoolean("Spy", false)) {
			ratTag.putBoolean("ShouldReturnToOwnerInventory", false);
		}
		ratTag.putBoolean("ShouldReturnToOwnerInventory", !ratTag.getBoolean("ShouldReturnToOwnerInventory", false));
	}

	public static String getRatName(ItemStack stack) {
		return stack.get(DataComponentTypes.CUSTOM_NAME) != null ? stack.getName().getString() : null;
	}

	@Nullable
	public static NbtCompound getRatTag(ItemStack stack) {
		if(stack.contains(ModDataComponents.RAT_ENTITY_DATA)){
			return stack.get(ModDataComponents.RAT_ENTITY_DATA).ratTag();
		}
		return null;
	}

	public static NbtCompound getRatTag(ItemStack stack, World world) {
		NbtCompound ratTag = getRatTag(stack);
		if (ratTag == null) {
			RatEntity rat = new RatEntity(ModEntities.RAT, world);
			NbtWriteView writeView = NbtWriteView.create(ErrorReporter.EMPTY, world.getRegistryManager());
			rat.writeCustomData(writeView);
			ratTag = writeView.getNbt();
		}
		return ratTag;
	}
	public static RatEntity.Type getRatType(ItemStack stack) {
		NbtCompound ratTag = getRatTag(stack);
		return getRatType(ratTag);
	}

	public static DyeColor getRatColor(ItemStack stack) {
		NbtCompound ratTag = getRatTag(stack);
		return getRatColor(ratTag);
	}
	public static RatEntity.Type getRatType(NbtCompound ratTag) {
		if (ratTag == null) {
			return RatEntity.Type.WILD;
		}
		String ratType = ratTag.getString("RatType", "").toUpperCase();
		return RatEntity.Type.byName(ratType, RatEntity.Type.WILD);
	}

	public static DyeColor getRatColor(NbtCompound ratTag) {
		if (ratTag == null) {
			return DyeColor.WHITE;
		}
		String ratColor = ratTag.getString("Color","").toUpperCase();
		return DyeColor.byId(ratColor.toLowerCase(), DyeColor.WHITE);
	}
	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private RatItemRenderer renderer;

			@Override
			public GeoItemRenderer<?> getGeoItemRenderer() {
				if(renderer == null){
					renderer = new RatItemRenderer();
				}
				return renderer;
			}
		});
	}


	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>("idle", 20, state -> PlayState.CONTINUE));
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
			RatEntity rat = this.getRatFromItem(world, user.getStackInHand(hand), new Vec3d(user.getX(), user.getEyeY() - 0.1, user.getZ()),
				hand == Hand.OFF_HAND ? PlayerInventory.OFF_HAND_SLOT : user.getInventory().getSlotWithStack(user.getStackInHand(hand)));
			if (rat == null) {
				return ActionResult.FAIL;
			}
			rat.sendFlying(user, user.getPitch(), user.getYaw(), 0, 3f, 1f);
			world.spawnEntity(rat);
			user.setStackInHand(hand, ItemStack.EMPTY);
			return ActionResult.SUCCESS;
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
		RatEntity rat = ModEntities.RAT.create(world, SpawnReason.SPAWN_ITEM_USE);
		if (rat != null) {
			rat.readCustomData(NbtReadView.create(ErrorReporter.EMPTY, world.getRegistryManager(), ratTag));
			rat.updatePosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
			rat.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
			rat.setSitting(false);

			// custom name
			if (ratItemStack.contains(DataComponentTypes.CUSTOM_NAME)) {
				rat.setCustomName(ratItemStack.getName());
			}

			// set slot to come back to
			rat.setSlot(slot);
		}
		return rat;
	}
}
