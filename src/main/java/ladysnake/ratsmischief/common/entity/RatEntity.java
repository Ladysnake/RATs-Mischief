package ladysnake.ratsmischief.common.entity;

import com.google.common.collect.ImmutableList;
import dev.emi.stepheightentityattribute.StepHeightEntityAttributeMain;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.RatsMischiefUtils;
import ladysnake.ratsmischief.common.entity.ai.ChaseForFunGoal;
import ladysnake.ratsmischief.common.entity.ai.EatToHealGoal;
import ladysnake.ratsmischief.common.entity.ai.FollowOwnerRatGoal;
import ladysnake.ratsmischief.common.entity.ai.RatMeleeAttackGoal;
import ladysnake.ratsmischief.common.init.ModDamageTypes;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.common.init.ModSoundEvents;
import ladysnake.ratsmischief.common.init.ModTags;
import ladysnake.ratsmischief.common.item.RatMasterArmorItem;
import ladysnake.ratsmischief.common.util.PlayerRatOwner;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;

public class RatEntity extends TameableEntity implements GeoEntity, Angerable {
	public static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = (itemEntity) -> !itemEntity.cannotPickup() && itemEntity.isAlive();
	public static final List<Type> NATURAL_TYPES = ImmutableList.of(Type.ALBINO, Type.BLACK, Type.GREY, Type.HUSKY, Type.LIGHT_BROWN, Type.BLUE);
	private static final List<PartyHat> PARTY_HATS = List.of(PartyHat.values());
	private static final TrackedData<String> TYPE = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<String> COLOR = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<String> PARTY_HAT = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.STRING);
	private static final TrackedData<Boolean> SITTING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> EATING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> FLYING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
	private static final TrackedData<Boolean> SNIFFING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> AROUSED = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> SPY = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> ATTACK_RIDING_TIME = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> POTION_GENE = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> SLOT = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final RawAnimation ANIM_SITTING = RawAnimation.begin().then("animation.rat.flat", Animation.LoopType.LOOP);
	private static final RawAnimation ANIM_EATING = RawAnimation.begin().then("animation.rat.eat", Animation.LoopType.LOOP);
	private static final RawAnimation ANIM_FLYING = RawAnimation.begin().then("animation.rat.fly", Animation.LoopType.LOOP);
	private static final RawAnimation ANIM_SNIFFING = RawAnimation.begin().then("animation.rat.sniff", Animation.LoopType.PLAY_ONCE);
	private static final RawAnimation ANIM_DANCING = RawAnimation.begin().then("animation.rat.smug_dance", Animation.LoopType.LOOP);
	private static final RawAnimation ANIM_RUNNING = RawAnimation.begin().then("animation.rat.run", Animation.LoopType.LOOP);
	private static final RawAnimation ANIM_RUNNING_JERMA = RawAnimation.begin().then("animation.rat.jermarun", Animation.LoopType.LOOP);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	public Goal action;
	public int actionTimer = 0;
	private UUID targetUuid;
	private boolean shouldReturnToPlayerInventory = false;

	public RatEntity(EntityType<? extends TameableEntity> type, World worldIn) {
		super(type, worldIn);
		this.ignoreCameraFrustum = false;
	}

	public static DefaultAttributeContainer.Builder createRatAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.0D).add(StepHeightEntityAttributeMain.STEP_HEIGHT, 2.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.1).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
	}

	public static Type getRandomNaturalType(Random random) {
		return NATURAL_TYPES.get(random.nextInt(NATURAL_TYPES.size()));
	}

	@Override
	protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
		// try spawning with cake on birthday
		if (RatsMischiefUtils.IS_BIRTHDAY && !this.isBaby() && this.getRandom().nextInt(5) == 0) {
			this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.CAKE));
		}
	}

	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityTag) {
		this.initEquipment(this.random, difficulty);

		// init doctor4t type or random type on birthday
		if (RatsMischiefUtils.IS_RAT_BIRTHDAY && this.getRandom().nextInt(10) == 0) {
			this.setRatType(Type.DOCTOR4T);
		} else if (RatsMischiefUtils.IS_MISCHIEF_BIRTHDAY && this.getRandom().nextInt(5) == 0) {
			this.setRatType(List.of(Type.values()).get(this.getRandom().nextInt(Type.values().length)));
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
	}

	public void setShouldReturnToOwnerInventory(boolean shouldReturnToPlayerInventory) {
		this.shouldReturnToPlayerInventory = shouldReturnToPlayerInventory;
	}

	public boolean shouldReturnToOwnerInventory() {
		return this.shouldReturnToPlayerInventory;
	}

	public boolean canReturnToOwnerInventory() {
		return this.shouldReturnToOwnerInventory() && !this.hasVehicle() && this.getOwner() instanceof PlayerEntity player && player.getInventory().getEmptySlot() != -1;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();

		int bound = 150;
		if (RatsMischiefUtils.IS_WORLD_RAT_DAY) {
			bound = 30;
		}

		if (this.random.nextInt(bound) == 0) {
			this.dataTracker.startTracking(TYPE, Type.GOLD.toString());
		} else {
			this.dataTracker.startTracking(TYPE, Type.WILD.toString());
		}

		this.dataTracker.startTracking(ANGER_TIME, 0);
		this.dataTracker.startTracking(SITTING, false);
		this.dataTracker.startTracking(EATING, false);
		this.dataTracker.startTracking(SNIFFING, false);
		this.dataTracker.startTracking(COLOR, DyeColor.values()[(this.random.nextInt(DyeColor.values().length))].getName());
		this.dataTracker.startTracking(FLYING, false);
		this.dataTracker.startTracking(SPY, false);
		this.dataTracker.startTracking(AROUSED, false);
		this.dataTracker.startTracking(ATTACK_RIDING_TIME, 0);
		this.dataTracker.startTracking(POTION_GENE, -1);
		this.dataTracker.startTracking(SLOT, 0);

		this.dataTracker.startTracking(PARTY_HAT, PARTY_HATS.get(this.random.nextInt(PARTY_HATS.size())).toString());
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new SitGoal(this));
		this.goalSelector.add(3, new EatToHealGoal(this));
		this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.3F));
		this.goalSelector.add(4, new RatMeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.add(5, new PickupItemGoal());
		this.goalSelector.add(5, new BringItemToOwnerGoal(this, 1.0D, false));
		this.goalSelector.add(6, new FollowOwnerRatGoal(this, 1.0D, 20.0F, 2.0F, false));
		this.goalSelector.add(7, new AnimalMateGoal(this, 1.0D));
		this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0D));
		this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(10, new LookAroundGoal(this));
		this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
		this.targetSelector.add(2, new AttackWithOwnerGoal(this));
		this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
		this.targetSelector.add(8, new ChaseForFunGoal<>(this, CatEntity.class, true));
		this.targetSelector.add(8, new UniversalAngerGoal<>(this, true));
	}

	private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
		if (this.isFlying()) {
			return event.setAndContinue(ANIM_FLYING);
		} else if (this.isEating()) {
			return event.setAndContinue(ANIM_EATING);
		} else if (this.isSitting() || this.isSneaking()) {
			this.setSniffing(false);
			this.setEating(false);
			return event.setAndContinue(ANIM_SITTING);
		} else if (event.isMoving()) {
			this.setSniffing(false);
			this.setEating(false);
			if (this.getRatType() == Type.JERMA) {
				return event.setAndContinue(ANIM_RUNNING_JERMA);
			} else {
				return event.setAndContinue(ANIM_RUNNING);
			}
		} else if (this.isSniffing()) {
			if (this.getRatType() == Type.RAT_KID || RatsMischiefUtils.IS_WORLD_RAT_DAY || RatsMischiefUtils.IS_BIRTHDAY) {
				return event.setAndContinue(ANIM_DANCING);
			} else {
				return event.setAndContinue(ANIM_SNIFFING);
			}
		}

		return PlayState.STOP;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>(this, "controller", this::predicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public RatEntity createChild(ServerWorld world, PassiveEntity entity) {
		RatEntity ratEntity = ModEntities.RAT.create(world);
		if (ratEntity != null && entity instanceof RatEntity secondRat) {
			if (this.getRatType() == Type.RAT_KID && secondRat.getRatType() == Type.RAT_KID) {
				ratEntity.setRatType(Type.RAT_KID);
			} else {
				int bound = 150;
				if (RatsMischiefUtils.IS_WORLD_RAT_DAY) {
					bound = 30;
				}
				if (this.random.nextInt(bound) == 0) {
					this.dataTracker.set(TYPE, Type.GOLD.toString());
				} else {
					ratEntity.setRatType(getRandomNaturalType(random));
				}
			}
			UUID ownerUuid = this.getOwnerUuid();
			if (ownerUuid != null) {
				ratEntity.setOwnerUuid(ownerUuid);
				ratEntity.setTamed(true);
				ratEntity.setBaby(true);
				ratEntity.initEquipment(this.random, world.getLocalDifficulty(this.getBlockPos()));
			}
			if (this.getPotionGene() != null && this.getPotionGene() == secondRat.getPotionGene()) {
				ratEntity.setPotionGene(this.getPotionGene());
			} else if (this.random.nextFloat() <= 0.33f) {
				List<StatusEffect> firstEffects = this.getActiveStatusEffects().keySet().stream().toList();
				List<StatusEffect> secondEffects = secondRat.getActiveStatusEffects().keySet().stream().toList();
				List<StatusEffect> sharedEffects = new ArrayList<>();
				for (StatusEffect effect : firstEffects) {
					if (secondEffects.contains(effect)) {
						sharedEffects.add(effect);
					}
				}
				if (!sharedEffects.isEmpty()) {
					ratEntity.setPotionGene(sharedEffects.get(this.random.nextInt(sharedEffects.size())));
				}
			}
		}
		return ratEntity;
	}

	public Type getRatType() {
		return Type.valueOf(this.dataTracker.get(TYPE));
	}

	public void setRatType(Type type) {
		this.dataTracker.set(TYPE, type.toString());
	}

	public PartyHat getPartyHat() {
		return PartyHat.valueOf(this.dataTracker.get(PARTY_HAT));
	}

	public void setPartyHat(String partyHat) {
		this.dataTracker.set(PARTY_HAT, partyHat.toUpperCase());
	}

	public DyeColor getRatColor() {
		return DyeColor.byName(this.dataTracker.get(COLOR), DyeColor.WHITE);
	}

	public void setRatColor(DyeColor color) {
		this.dataTracker.set(COLOR, color.getName());
	}

	public StatusEffect getPotionGene() {
		if (this.dataTracker.get(POTION_GENE) == -1) {
			return null;
		}
		return Registries.STATUS_EFFECT.get(this.dataTracker.get(POTION_GENE));
	}

	public void setPotionGene(StatusEffect potion) {
		this.dataTracker.set(POTION_GENE, Registries.STATUS_EFFECT.getRawId(potion));
	}

	public int getSlot() {
		return this.dataTracker.get(SLOT);
	}

	public void setSlot(int slot) {
		this.dataTracker.set(SLOT, slot);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag) {
		super.readCustomDataFromNbt(tag);

		if (tag.contains("RatType")) {
			this.setRatType(Type.valueOf(tag.getString("RatType")));
		}
		this.readAngerFromNbt(this.getWorld(), tag);

		if (tag.contains("Sitting")) {
			this.setSitting(tag.getBoolean("Sitting"));
		}

		if (tag.contains("Color")) {
			this.setRatColor(DyeColor.byName(tag.getString("Color"), DyeColor.WHITE));
		}

		if (tag.contains("Aroused")) {
			this.setAroused(tag.getBoolean("Aroused"));
		}

		if (tag.contains("Flying")) {
			this.setFlying(tag.getBoolean("Flying"));
		}

		if (tag.contains("AttackRidingTime")) {
			this.setAttackRidingTime(tag.getInt("AttackRidingTime"));
		}

		if (tag.contains("Slot")) {
			this.setSlot(tag.getInt("Slot"));
		}

		if (tag.contains("ShouldReturnToOwnerInventory")) {
			this.setShouldReturnToOwnerInventory(tag.getBoolean("ShouldReturnToOwnerInventory"));
		}

		if (tag.contains("Spy")) {
			this.setSpy(tag.getBoolean("Spy"));
		}

		if (tag.contains("PartyHat")) {
			this.setPartyHat(tag.getString("PartyHat"));
		}

		if (tag.contains("PotionGene")) {
			Identifier id = Identifier.tryParse(tag.getString("PotionGene"));
			StatusEffect potion = Registries.STATUS_EFFECT.get(id);
			this.dataTracker.set(POTION_GENE, Registries.STATUS_EFFECT.getRawId(potion));
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound tag) {
		super.writeCustomDataToNbt(tag);

		tag.putString("RatType", this.getRatType().toString());
		tag.putBoolean("ShouldReturnToOwnerInventory", this.shouldReturnToOwnerInventory());
		tag.putString("Color", this.getRatColor().getName());
		this.writeAngerToNbt(tag);
		tag.putBoolean("Aroused", this.isAroused());
		tag.putBoolean("Flying", this.isFlying());
		tag.putBoolean("Spy", this.isSpy());
		tag.putString("PartyHat", this.dataTracker.get(PARTY_HAT));

		tag.putBoolean("Sitting", this.isSitting());
		tag.putInt("AttackRidingTime", this.getAttackRidingTime());
		tag.putInt("Slot", this.getSlot());
		if (this.getPotionGene() != null) {
			tag.putString("PotionGene", String.valueOf(Registries.STATUS_EFFECT.getId(this.getPotionGene())));
		}
	}

	@Override
	public void setYaw(float yaw) {
		if (!this.isFlying()) {
			super.setYaw(yaw);
		}
	}

	@Override
	public void setBodyYaw(float bodyYaw) {
		if (!this.isFlying()) {
			super.setBodyYaw(bodyYaw);
		}
	}

	@Override
	public void setHeadYaw(float headYaw) {
		if (!this.isFlying()) {
			super.setHeadYaw(headYaw);
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.isFlying()) {
			// turn saturation into regeneration
			if (this.hasStatusEffect(StatusEffects.SATURATION)) {
				StatusEffectInstance saturation = this.getStatusEffect(StatusEffects.SATURATION);
				if (saturation != null)
					this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, saturation.getDuration(), saturation.getAmplifier(), saturation.isAmbient(), saturation.shouldShowParticles(), saturation.shouldShowIcon()));
			}

			// sexually aroused rat
			if (!this.getWorld().isClient) {
				this.setAroused(this.getLoveTicks() > 0);
			}

			// attack riding
			if (this.getTarget() != null && this.getTarget().getFirstPassenger() == this && this.getAttackRidingTime() > 0) {
				if (this.getAttackRidingTime() > 200) {
					this.dismountVehicle();
					this.setTarget(null);
					this.setAttackRidingTime(0);
				}

				this.setAttackRidingTime(this.getAttackRidingTime() + 1);
			}

			// keep targeting the ridden entity
			if (this.hasVehicle() && this.getVehicle() instanceof LivingEntity livingEntity && this.getAttackRidingTime() > 0) {
				this.setTarget(livingEntity);
			}

			// return to owner
			if (this.getOwner() != null && this.canReturnToOwnerInventory() && this.getOwner().squaredDistanceTo(this.getPos()) <= 2f) {
				this.turnRatIntoItemAndGive((PlayerEntity) this.getOwner());
			}
		} else {
			this.jumping = true;
			HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
			boolean bl = false;
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
				BlockState blockState = this.getWorld().getBlockState(blockPos);
				if (blockState.isOf(Blocks.NETHER_PORTAL)) {
					this.setInNetherPortal(blockPos);
					bl = true;
				} else if (blockState.isOf(Blocks.END_GATEWAY)) {
					BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
					if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
						EndGatewayBlockEntity.tryTeleportingEntity(this.getWorld(), blockPos, blockState, this, (EndGatewayBlockEntity) blockEntity);
					}

					bl = true;
				}
			}

			if (hitResult.getType() != HitResult.Type.MISS && !bl) {
				this.onCollision(hitResult);
			}

			this.checkBlockCollision();
			Vec3d vec3d = this.getVelocity();
			double d = this.getX() + vec3d.x;
			double e = this.getY() + vec3d.y;
			double f = this.getZ() + vec3d.z;
//			this.updateRotation();
			if (this.isTouchingWater()) {
				for (int i = 0; i < 4; ++i) {
					this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
				}
			}
		}
	}

	protected boolean canHit(Entity entity) {
		if (!entity.isSpectator() && entity.isAlive() && entity.canHit()) {
			Entity entity2 = this.getOwner();
			return entity2 == null || entity != entity2 || !entity2.isConnectedThroughVehicle(entity);
		} else {
			return false;
		}
	}

	public void sendFlying(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
		float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		float g = -MathHelper.sin((pitch + roll) * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		this.setVelocity(f, g, h, modifierZ, modifierXYZ);
		Vec3d vec3d = user.getVelocity();
		this.setVelocity(this.getVelocity().add(vec3d.x, user.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
		this.setPitch(pitch);
		this.setHeadYaw(yaw);
		this.setYaw(yaw);
		this.setFlying(true);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	public void setVelocity(double x, double y, double z, float speed, float divergence) {
		Vec3d vec3d = new Vec3d(x, y, z).normalize().add(this.random.nextTriangular(0.0, 0.0172275 * (double) divergence), this.random.nextTriangular(0.0, 0.0172275 * (double) divergence), this.random.nextTriangular(0.0, 0.0172275 * (double) divergence)).multiply(speed);
		this.setVelocity(vec3d);
		double d = vec3d.horizontalLength();
		this.setYaw((float) (MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float) Math.PI));
		this.setPitch((float) (MathHelper.atan2(vec3d.y, d) * 180.0F / (float) Math.PI));
		this.prevYaw = this.getYaw();
		this.prevPitch = this.getPitch();
	}

	protected void onCollision(HitResult hitResult) {
		if (hitResult instanceof EntityHitResult entityHitResult) {
			this.onEntityHit(entityHitResult);
			this.emitGameEvent(GameEvent.PROJECTILE_LAND, this);
		} else if (hitResult instanceof BlockHitResult blockHitResult) {
			this.emitGameEvent(GameEvent.PROJECTILE_LAND, this);
		}

		this.setFlying(false);
	}

	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (entityHitResult.getEntity() instanceof LivingEntity livingEntity && !(entityHitResult.getEntity() instanceof TameableEntity tameable && tameable.getOwner() == this.getOwner())) {
			this.setTarget(livingEntity);
			this.startRiding(livingEntity);
			this.setAttackRidingTime(1);
		}
	}

	@Override
	public void mobTick() {
		if (!this.isFlying()) {
			if (this.isSitting() && !this.isEating() && !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
				this.dropStack(this.getEquippedStack(EquipmentSlot.MAINHAND));
				this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
			}

			if (this.isTouchingWater()) {
				this.setSitting(false);
				this.setSniffing(false);
			}

			if (this.getCustomName() != null) {
				switch (this.getCustomName().getString().toLowerCase(Locale.ROOT)) {
					case "doctor4t" -> this.setRatType(Type.DOCTOR4T);
					case "astron", "astronyu" -> this.setRatType(Type.ASTRONYU);

					case "rat kid", "hat kid" -> this.setRatType(Type.RAT_KID);

					case "remy" -> this.setRatType(Type.REMY);
					case "ratater" -> this.setRatType(Type.RATATER);
					case "jerma", "jerma985" -> this.setRatType(Type.JERMA);
					case "biggie cheese" -> this.setRatType(Type.BIGGIE_CHEESE);

//					case "gin" -> this.setRatType(Type.HUSKY);
//					case "splinter", "tonic" -> this.setRatType(Type.HUSKY_GHOST);
//					case "sai-sai", "shiro" -> this.setRatType(Type.WILD_GHOST);
				}
			}

			if (!this.hasAngerTime() && !this.moveControl.isMoving() && this.random.nextInt(100) == 0) {
				this.setSniffing(false);
				this.setSniffing(true);
			}

			// reset
			if (this.actionTimer <= 0 && this.action != null) {
				this.removeCurrentActionGoal();
			}

			if (this.action != null && this.actionTimer > 0) {
				this.actionTimer--;
			}
		}
	}

	@Override
	public boolean canTarget(EntityType<?> type) {
		return type != EntityType.CREEPER;
	}

	@Override
	protected void tickStatusEffects() {
		super.tickStatusEffects();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();

		if (this.isEating()) {
			this.setMovementSpeed(0.0f);
		} else {
			this.setMovementSpeed(0.5f);
		}

		if (!this.getWorld().isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && !this.isEating()) {
			for (ItemEntity itemEntity : this.getWorld().getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand(1, 0, 1))) {
				if (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup() && this.canGather(itemEntity.getStack())) {
					this.loot(itemEntity);
				}
			}
		}

		if (!this.getWorld().isClient) {
			this.tickAngerLogic((ServerWorld) this.getWorld(), true);
		}

		if (this.getWorld().isClient) {
			if (this.getPotionGene() != null) {
				StatusEffect effect = this.getPotionGene();
				int k = effect.getColor();
				double d = (float) ((k >> 16 & 0xFF)) / 255.0F;
				double e = (float) ((k >> 8 & 0xFF)) / 255.0F;
				double f = (float) ((k & 0xFF)) / 255.0F;
				this.getWorld().addParticle(ParticleTypes.ENTITY_EFFECT, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), d, e, f);
			}
		}
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (entity instanceof RatEntity) {
			super.pushAwayFrom(entity);
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (this.getWorld().isClient) {
			boolean bl = this.isOwner(player) || this.isTamed() && !this.isTamed() && !this.hasAngerTime();
			return bl ? ActionResult.CONSUME : ActionResult.PASS;
		} else {
			// dye rat kids
			if (itemStack.getItem() instanceof DyeItem && this.getRatType() == Type.RAT_KID) {
				this.setRatColor(((DyeItem) itemStack.getItem()).getColor());
				if (!player.isCreative()) {
					itemStack.decrement(1);
				}
				return ActionResult.CONSUME;
			}

			if (this.isTamed()) {
				// healing
				if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
					if (!player.getAbilities().creativeMode) {
						itemStack.decrement(1);
					}
					if (item.getFoodComponent() != null) {
						this.heal(item.getFoodComponent().getHunger());
					}
					return ActionResult.SUCCESS;
				}

				if (this.isOwner(player)) {
					// picking up
					if (itemStack.isEmpty() && player.isSneaking()) {
						this.turnRatIntoItemAndGive(player);

						return ActionResult.SUCCESS;
					}

					// sitting
					if (!(itemStack.isIn(ModTags.RAT_POUCHES)) && !(itemStack.isOf(ModItems.RAT_MASTER_OCARINA)) && (!this.isBreedingItem(itemStack)) && !(itemStack.getItem() instanceof DyeItem && this.getRatType() == Type.RAT_KID)) {
						this.setSitting(!this.isSitting());
						return ActionResult.SUCCESS;
					}
				}
			} else if (item.getFoodComponent() != null && !this.hasAngerTime()) { // taming
				player.getStackInHand(hand).decrement(1);

				if (this.random.nextInt(Math.max(1, 6 - item.getFoodComponent().getHunger())) == 0) {
					this.setOwner(player);
					this.navigation.stop();
					this.setTarget(null);
					this.getWorld().sendEntityStatus(this, (byte) 7);
				} else {
					for (int i = 0; i < 7; ++i) {
						double d = this.random.nextGaussian() * 0.02D;
						double e = this.random.nextGaussian() * 0.02D;
						double f = this.random.nextGaussian() * 0.02D;
						((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.SMOKE, this.getParticleX(1.0D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), 1, d, e, f, 0f);
					}
				}

				return ActionResult.SUCCESS;
			}
		}

		return super.interactMob(player, hand);
	}

	public void turnRatIntoItemAndGive(PlayerEntity player) {
		ItemStack ratItemStack = new ItemStack(ModItems.RAT);
		NbtCompound nbt = new NbtCompound();
		this.saveNbt(nbt);
		ratItemStack.getOrCreateSubNbt(RatsMischief.MOD_ID).put("rat", nbt);

		// custom name
		if (this.hasCustomName()) {
			ratItemStack.setCustomName(this.getCustomName());
		}

		if (this.getSlot() >= 0 && player.getInventory().getStack(this.getSlot()).isEmpty()) {
			player.getInventory().setStack(this.getSlot(), ratItemStack);
		} else {
			player.giveItemStack(ratItemStack);
		}

		this.discard();
	}

	@Override
	public int getAngerTime() {
		return this.dataTracker.get(ANGER_TIME);
	}

	@Override
	public void setAngerTime(int ticks) {
		this.dataTracker.set(ANGER_TIME, ticks);
	}

	public int getAttackRidingTime() {
		return this.dataTracker.get(ATTACK_RIDING_TIME);
	}

	public void setAttackRidingTime(int ticks) {
		this.dataTracker.set(ATTACK_RIDING_TIME, ticks);
	}


	public boolean isAroused() {
		return this.dataTracker.get(AROUSED);
	}

	public void setAroused(boolean aroused) {
		this.dataTracker.set(AROUSED, aroused);
	}

	public boolean isSpy() {
		return this.dataTracker.get(SPY);
	}

	public void setSpy(boolean spy) {
		this.dataTracker.set(SPY, spy);
	}

	public boolean isFlying() {
		return this.dataTracker.get(FLYING);
	}

	public void setFlying(boolean flying) {
		this.dataTracker.set(FLYING, flying);
	}

	@Override
	public void chooseRandomAngerTime() {
		this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
	}

	@Override
	public UUID getAngryAt() {
		return this.targetUuid;
	}

	@Override
	public void setAngryAt(@Nullable UUID uuid) {
		this.targetUuid = uuid;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity player) {
		return !this.hasAngerTime() && super.canBeLeashedBy(player);
	}

	@Override
	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		if (!(target instanceof CreeperEntity || (target instanceof GhastEntity))) {
			if (target instanceof RatEntity rat) {
				return !rat.isTamed() || rat.getOwner() != owner;
			} else if (target instanceof PlayerEntity targetPlayer && owner instanceof PlayerEntity ownerPlayer && !ownerPlayer.shouldDamagePlayer(targetPlayer)) {
				return false;
			} else if (target instanceof HorseEntity horse && horse.isTame()) {
				return false;
			} else {
				return !(target instanceof TameableEntity tameable) || !(tameable.isTamed());
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return stack.getItem().isFood();
	}

	@Override
	public boolean tryAttack(Entity target) {
		float damage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		damage *= RatMasterArmorItem.getDamageMultiplier(this.getOwner());
		target.timeUntilRegen = 0;
		if (target.damage(ModDamageTypes.ratDamage(this), damage)) {
			if (this.getPotionGene() != null && target instanceof LivingEntity livingEntity) {
				livingEntity.addStatusEffect(new StatusEffectInstance(this.getPotionGene(), 5 * 20));
			}
			this.applyDamageEffects(this, target);
			this.onAttacking(target);
			return true;
		}
		return false;
	}

	@Override
	public boolean canHaveStatusEffect(StatusEffectInstance effect) {
		return super.canHaveStatusEffect(effect) && this.getPotionGene() == null;
	}

	@Override
	public boolean isSitting() {
		return this.dataTracker.get(SITTING) || this.dataTracker.get(EATING);
	}

	@Override
	public void setSitting(boolean sitting) {
		this.dataTracker.set(SITTING, sitting);
	}

	public boolean isSniffing() {
		return this.dataTracker.get(SNIFFING);
	}

	public void setSniffing(boolean sniffing) {
		this.dataTracker.set(SNIFFING, sniffing);
	}

	public boolean isEating() {
		return this.dataTracker.get(EATING);
	}

	public void setEating(boolean eating) {
		this.dataTracker.set(EATING, eating);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			Entity entity = source.getAttacker();
			this.setSitting(false);
			if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof PersistentProjectileEntity)) {
				amount = (amount + 1.0F) / 2.0F;
			}
			amount *= RatMasterArmorItem.getResistanceMultiplier(this.getOwner());
			return super.damage(source, amount);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		if (damageSource.getAttacker() == this.getOwner() && RatMasterArmorItem.getEquippedPieces(this.getOwner()) >= 4) {
			return true;
		}
		if (damageSource.isOf(DamageTypes.CACTUS) || damageSource.isOf(DamageTypes.SWEET_BERRY_BUSH) || damageSource.getAttacker() instanceof EnderDragonEntity || damageSource.isOf(DamageTypes.CRAMMING)) {
			return true;
		} else {
			return super.isInvulnerableTo(damageSource);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		super.onPlayerCollision(player);
	}

	@Override
	public double getHeightOffset() {
		if (this.getVehicle() != null) {
			return this.getVehicle().getHeight() * 0.3f;
		} else {
			return super.getHeightOffset();
		}
	}

	@Override
	protected void pushAway(Entity entity) {
		this.pushAwayFrom(entity);
	}

	@Override
	public boolean canPickUpLoot() {
		return !this.isSitting();
	}

	@Override
	protected void loot(ItemEntity item) {
		ItemStack itemStack = item.getStack();
		if (this.getMainHandStack().isEmpty()) {
			this.equipStack(EquipmentSlot.MAINHAND, itemStack);
			this.triggerItemPickedUpByEntityCriteria(item);
			this.sendPickup(item, itemStack.getCount());
			item.remove(RemovalReason.DISCARDED);
		}
	}

	@Override
	protected @Nullable SoundEvent getDeathSound() {
		return ModSoundEvents.ENTITY_RAT_DEATH;
	}

	@Override
	protected @Nullable SoundEvent getHurtSound(DamageSource source) {
		return ModSoundEvents.ENTITY_RAT_HURT;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		if (!state.isLiquid()) {
			BlockState blockState = this.getWorld().getBlockState(pos.up());
			BlockSoundGroup blockSoundGroup = blockState.isOf(Blocks.SNOW) ? blockState.getSoundGroup() : state.getSoundGroup();
			this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.01F, blockSoundGroup.getPitch());
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		if (fallDistance > 20 && !this.isBaby()) {
			this.playSound(ModSoundEvents.ENTITY_RAT_CLAP, 1.0f, (float) (1.0f + this.random.nextGaussian() / 10f));
		}
		return false;
	}

	@Override
	protected int computeFallDamage(float fallDistance, float damageMultiplier) {
		return 0;
	}

	@Nullable
	@Override
	public ItemEntity dropStack(ItemStack stack) {
		if (this.isTamed()) {
			ItemEntity item = super.dropStack(stack);
			LivingEntity owner = this.getOwner();
			if (owner != null && item != null) {
				Vec3d dir = owner.getPos().subtract(this.getPos()).normalize().multiply(0.6f);
				item.setVelocity(dir);
			}

			return item;
		} else {
			return super.dropStack(stack);
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		if (this.isTamed()) {
			this.dropStack(this.getMainHandStack());
		}

		super.onDeath(source);
	}

	@Override
	public int getMaxAir() {
		return 1500;
	}

	public void setAction(Goal action) {
		this.removeCurrentActionGoal();
		this.actionTimer = 300; // 15s
		this.action = action;
		this.goalSelector.add(4, action);
	}

	public void removeCurrentActionGoal() {
		this.goalSelector.remove(this.action);
		this.action = null;
	}

	@Override
	public EntityView method_48926() {
		return this.getWorld();
	}

	public enum Type {
		WILD(RatsMischief.id("textures/entity/wild.png")),
		ALBINO(RatsMischief.id("textures/entity/albino.png")),
		BLACK(RatsMischief.id("textures/entity/black.png")),
		GREY(RatsMischief.id("textures/entity/grey.png")),
		HUSKY(RatsMischief.id("textures/entity/husky.png")),
		LIGHT_BROWN(RatsMischief.id("textures/entity/light_brown.png")),
		BLUE(RatsMischief.id("textures/entity/blue.png")),
		GOLD(RatsMischief.id("textures/entity/gold.png")),

		RAT_KID(null),

		DOCTOR4T(RatsMischief.id("textures/entity/named/doctor4t.png")),
		ASTRONYU(RatsMischief.id("textures/entity/named/astronyu.png")),

		REMY(RatsMischief.id("textures/entity/named/remy.png")),
		RATATER(RatsMischief.id("textures/entity/named/ratater.png")),
		JERMA(RatsMischief.id("textures/entity/named/jerma.png")),
		BIGGIE_CHEESE(RatsMischief.id("textures/entity/named/biggie_cheese.png"));

		public final Identifier ratTexture;

		Type(Identifier ratTexture) {
			this.ratTexture = ratTexture;
		}

		public static Type byName(String name, @Nullable Type defaultColor) {
			for (Type type : values()) {
				if (type.name().equals(name)) {
					return type;
				}
			}
			return defaultColor;
		}
	}

	public enum PartyHat {
		BLUE, BRAND, BROWN, CYAN, GREEN, LIGHT_BLUE, LIGHT_GREEN, MAGENTA, ORANGE, PINK, PURPLE, RAINBOW, RED, YELLOW
	}

	abstract static class MovementGoal extends Goal {
		public MovementGoal() {
			this.setControls(EnumSet.of(Control.MOVE));
		}
	}

	class PickupItemGoal extends Goal {
		public PickupItemGoal() {
			this.setControls(EnumSet.of(Control.MOVE));
		}

		@Override
		public boolean canStart() {
			if (RatEntity.this.isSpy()) {
				return false;
			} else if (!RatEntity.this.isTamed() || !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
				return false;
			} else if (RatEntity.this.getTarget() == null && RatEntity.this.getAttacker() == null) {
				if (RatEntity.this.isSitting() && RatEntity.this.isEating()) {
					return false;
				} else if (RatEntity.this.getRandom().nextInt(10) != 0) {
					return false;
				} else {
					List<ItemEntity> list = RatEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, RatEntity.this.getBoundingBox().expand(10.0D, 10.0D, 10.0D), RatEntity.PICKABLE_DROP_FILTER);
					return !list.isEmpty() && RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
				}
			} else {
				return false;
			}
		}

		@Override
		public void tick() {
			List<ItemEntity> list = RatEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, RatEntity.this.getBoundingBox().expand(10.0D, 10.0D, 10.0D), RatEntity.PICKABLE_DROP_FILTER);
			ItemStack itemStack = RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (itemStack.isEmpty() && !list.isEmpty()) {
				RatEntity.this.getNavigation().startMovingTo(list.get(0), 1);
			}
		}

		@Override
		public void start() {
			List<ItemEntity> list = RatEntity.this.getWorld().getEntitiesByClass(ItemEntity.class, RatEntity.this.getBoundingBox().expand(10.0D, 10.0D, 10.0D), RatEntity.PICKABLE_DROP_FILTER);
			if (!list.isEmpty()) {
				RatEntity.this.getNavigation().startMovingTo(list.get(0), 1);
			}
		}
	}

	public class BringItemToOwnerGoal extends FollowOwnerGoal {
		public BringItemToOwnerGoal(TameableEntity tameable, double speed, boolean leavesAllowed) {
			super(tameable, speed, 0.0f, 0.0f, leavesAllowed);
		}

		@Override
		public void tick() {
			super.tick();

			if (RatEntity.this.getOwner() != null && RatEntity.this.getOwner().isAlive() && ((PlayerEntity) RatEntity.this.getOwner()).getInventory().getEmptySlot() >= 0) {
				if (RatEntity.this.squaredDistanceTo(RatEntity.this.getOwner()) <= 3.0f && RatEntity.this.getOwner() instanceof PlayerEntity) {
					RatEntity.this.dropStack(RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND));
					RatEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
			}
		}

		@Override
		public boolean canStart() {
			if (RatEntity.this.getOwner() instanceof PlayerRatOwner playerRatOwner) {
				if (!playerRatOwner.mischief$shouldBringItems()) {
					return false;
				}
			}
			return super.canStart() && !RatEntity.this.isSpy() && !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && RatEntity.this.isTamed() && !RatEntity.this.isSitting() && !RatEntity.this.isEating() && RatEntity.this.getOwner() != null && RatEntity.this.getOwner().isAlive() && ((PlayerEntity) RatEntity.this.getOwner()).getInventory().getEmptySlot() >= 0;
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && !RatEntity.this.isSpy() && !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && RatEntity.this.isTamed() && !RatEntity.this.isSitting() && !RatEntity.this.isEating() && RatEntity.this.getOwner() != null && RatEntity.this.getOwner().isAlive() && ((PlayerEntity) RatEntity.this.getOwner()).getInventory().getEmptySlot() >= 0;
		}
	}
}
