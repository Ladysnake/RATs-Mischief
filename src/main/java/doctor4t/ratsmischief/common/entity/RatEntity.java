package doctor4t.ratsmischief.common.entity;

import com.google.common.collect.ImmutableList;
import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.RatsMischiefUtils;
import doctor4t.ratsmischief.common.entity.ai.ChaseForFunGoal;
import doctor4t.ratsmischief.common.entity.ai.EatToHealGoal;
import doctor4t.ratsmischief.common.entity.ai.FollowOwnerRatGoal;
import doctor4t.ratsmischief.common.entity.ai.RatMeleeAttackGoal;
import doctor4t.ratsmischief.common.init.ModEntities;
import doctor4t.ratsmischief.common.init.ModItems;
import doctor4t.ratsmischief.common.init.ModSoundEvents;
import doctor4t.ratsmischief.common.item.MasterRatArmorItem;
import doctor4t.ratsmischief.common.item.RatPouchItem;
import doctor4t.ratsmischief.common.item.RatStaffItem;
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
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;

public class RatEntity extends TameableEntity implements IAnimatable, Angerable {
	public static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = (itemEntity) -> !itemEntity.cannotPickup() && itemEntity.isAlive();
	public static final List<Type> NATURAL_TYPES = ImmutableList.of(
			Type.ALBINO, Type.BLACK, Type.GREY, Type.HUSKY, Type.CHOCOLATE, Type.LIGHT_BROWN, Type.RUSSIAN_BLUE
	);
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
	private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
	public Goal action;
	public int actionTimer = 0;
	private UUID targetUuid;
	private boolean shouldReturnToPlayerInventory = false;

	public RatEntity(EntityType<? extends TameableEntity> type, World worldIn) {
		super(type, worldIn);
		this.ignoreCameraFrustum = false;
		this.stepHeight = 2f;
	}

	public static DefaultAttributeContainer.Builder createRatAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0.0D).add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
	}

	public static Type getRandomNaturalType(RandomGenerator random) {
		return NATURAL_TYPES.get(random.nextInt(NATURAL_TYPES.size()));
	}

	@Override
	protected void initEquipment(RandomGenerator random, LocalDifficulty localDifficulty) {
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
		return shouldReturnToPlayerInventory;
	}

	public boolean canReturnToOwnerInventory() {
		return shouldReturnToOwnerInventory() && !this.hasVehicle() && this.getOwner() instanceof PlayerEntity player && player.getInventory().getEmptySlot() != -1;
	}

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

		this.dataTracker.startTracking(PARTY_HAT, PARTY_HATS.get(random.nextInt(PARTY_HATS.size())).toString());
	}

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
		this.targetSelector.add(4, new TargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
		this.targetSelector.add(8, new ChaseForFunGoal<>(this, CatEntity.class, true));
		this.targetSelector.add(8, new UniversalAngerGoal<>(this, true));
	}

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		if (this.isFlying()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.fly", ILoopType.EDefaultLoopTypes.LOOP));
			return PlayState.CONTINUE;
		} else if (this.isEating()) {
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.eat", ILoopType.EDefaultLoopTypes.LOOP));
			return PlayState.CONTINUE;
		} else if (this.isSitting()) {
			this.setSniffing(false);
			this.setEating(false);
			event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.flat", ILoopType.EDefaultLoopTypes.LOOP));
			return PlayState.CONTINUE;
		} else if (event.isMoving()) {
			this.setSniffing(false);
			this.setEating(false);
			if (this.getRatType() == Type.JERMA) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.jermarun", ILoopType.EDefaultLoopTypes.LOOP));
			} else {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.run", ILoopType.EDefaultLoopTypes.LOOP));
			}
			return PlayState.CONTINUE;
		} else if (this.isSniffing()) {
			if (this.getRatType() == Type.RAT_KID || RatsMischiefUtils.IS_WORLD_RAT_DAY || RatsMischiefUtils.IS_BIRTHDAY) {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.smug_dance", ILoopType.EDefaultLoopTypes.LOOP));
			} else {
				event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.sniff", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
			}
			return PlayState.CONTINUE;
		}

		return PlayState.STOP;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public RatEntity createChild(ServerWorld world, PassiveEntity entity) {
		RatEntity ratEntity = ModEntities.RAT.create(world);
		if (ratEntity != null) {
			if (this.getRatType() == Type.RAT_KID && entity instanceof RatEntity && ((RatEntity) entity).getRatType() == Type.RAT_KID) {
				ratEntity.setRatType(Type.WILD);
//			ratEntity.setRatType(Type.RAT_KID);
			} else {
				int bound = 150;
				if (RatsMischiefUtils.IS_WORLD_RAT_DAY) {
					bound = 30;
				}
				if (this.random.nextInt(bound) == 0) {
					this.dataTracker.set(TYPE, Type.GOLD.toString());
				} else {
					ratEntity.setRatType(Type.WILD);
//				ratEntity.setRatType(getRandomNaturalType(random));
				}
			}
			UUID ownerUuid = this.getOwnerUuid();
			if (ownerUuid != null) {
				ratEntity.setOwnerUuid(ownerUuid);
				ratEntity.setTamed(true);
				ratEntity.setBaby(true);
				ratEntity.initEquipment(this.random, world.getLocalDifficulty(this.getBlockPos()));
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

	public DyeColor getRatColor() {
		return DyeColor.byName(this.dataTracker.get(COLOR), DyeColor.WHITE);
	}

	public void setRatColor(DyeColor color) {
		this.dataTracker.set(COLOR, color.getName());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound tag) {
		super.readCustomDataFromNbt(tag);

		if (tag.contains("RatType")) {
			this.setRatType(Type.valueOf(tag.getString("RatType")));
		}
		this.readAngerFromNbt(this.world, tag);

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

		if (tag.contains("ShouldReturnToOwnerInventory")) {
			this.setShouldReturnToOwnerInventory(tag.getBoolean("ShouldReturnToOwnerInventory"));
		}

		if (tag.contains("Spy")) {
			this.setSpy(tag.getBoolean("Spy"));
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

		tag.putBoolean("Sitting", this.isSitting());
		tag.putInt("AttackRidingTime", this.getAttackRidingTime());
	}

	@Override
	public boolean canMoveVoluntarily() {
		return super.canMoveVoluntarily();
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.isFlying()) {
			// turn saturation into regeneration
			if (this.hasStatusEffect(StatusEffects.SATURATION)) {
				StatusEffectInstance saturation = this.getStatusEffect(StatusEffects.SATURATION);
				if (saturation != null) this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, saturation.getDuration(), saturation.getAmplifier(), saturation.isAmbient(), saturation.shouldShowParticles(), saturation.shouldShowIcon()));
			}

			// sexually aroused rat
			if (!this.world.isClient) {
				this.setAroused(getLoveTicks() > 0);
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
			if (this.getOwner() != null && this.canReturnToOwnerInventory() && this.getOwner().squaredDistanceTo(this.getPos()) <= 1.5f) {
				turnRatIntoItemAndGive((PlayerEntity) this.getOwner());
			}
		} else {
			HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
			boolean bl = false;
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
				BlockState blockState = this.world.getBlockState(blockPos);
				if (blockState.isOf(Blocks.NETHER_PORTAL)) {
					this.setInNetherPortal(blockPos);
					bl = true;
				} else if (blockState.isOf(Blocks.END_GATEWAY)) {
					BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
					if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
						EndGatewayBlockEntity.tryTeleportingEntity(this.world, blockPos, blockState, this, (EndGatewayBlockEntity) blockEntity);
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
					this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
				}
			}
		}
	}

	protected boolean canHit(Entity entity) {
		if (!entity.isSpectator() && entity.isAlive() && entity.collides()) {
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
		this.setYaw(yaw);
		this.setFlying(true);
	}

	@SuppressWarnings("SuspiciousNameCombination")
	public void setVelocity(double x, double y, double z, float speed, float divergence) {
		Vec3d vec3d = new Vec3d(x, y, z)
				.normalize()
				.add(
						this.random.nextTriangular(0.0, 0.0172275 * (double) divergence),
						this.random.nextTriangular(0.0, 0.0172275 * (double) divergence),
						this.random.nextTriangular(0.0, 0.0172275 * (double) divergence)
				)
				.multiply(speed);
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
			this.world.emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Context.create(this, null));
		} else if (hitResult instanceof BlockHitResult blockHitResult) {
			BlockPos blockPos = blockHitResult.getBlockPos();
			this.world.emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.create(this, this.world.getBlockState(blockPos)));
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
					case "doctor4t" -> setRatType(Type.DOCTOR4T);
					case "ratater" -> setRatType(Type.RATATER);
					case "rat kid", "hat kid" -> setRatType(Type.RAT_KID);
					case "jotaro", "jorato" -> setRatType(Type.JORATO);
					case "jerma", "jerma985" -> setRatType(Type.JERMA);
					case "hollow rat", "hollow knight" -> setRatType(Type.HOLLOW);
					case "rateline", "madeline" -> setRatType(Type.RATELINE);
					case "biggie cheese" -> setRatType(Type.BIGGIE_CHEESE);
					case "arathain" -> setRatType(Type.ARATHAIN);
					case "astron", "astronyu" -> setRatType(Type.ASTRONYU);
				}
			}

			if (!this.hasAngerTime() && !this.moveControl.isMoving() && random.nextInt(100) == 0) {
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

	public void tickMovement() {
		super.tickMovement();

		if (this.isEating()) {
			setMovementSpeed(0.0f);
		} else {
			setMovementSpeed(0.5f);
		}

		if (!this.world.isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && !this.isEating()) {
			for (ItemEntity itemEntity : world.getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand(1, 0, 1))) {
				if (!itemEntity.isRemoved() && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup() && this.canGather(itemEntity.getStack())) {
					this.loot(itemEntity);
				}
			}
		}

		if (!this.world.isClient) {
			this.tickAngerLogic((ServerWorld) this.world, true);
		}
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (entity instanceof RatEntity) {
			super.pushAwayFrom(entity);
		}
	}

	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (this.world.isClient) {
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
						turnRatIntoItemAndGive(player);

						return ActionResult.SUCCESS;
					}

					// sitting
					if (!(item instanceof RatPouchItem) && !(item instanceof RatStaffItem) && (!this.isBreedingItem(itemStack)) && !(itemStack.getItem() instanceof DyeItem && this.getRatType() == Type.RAT_KID)) {
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
					this.world.sendEntityStatus(this, (byte) 7);
				} else {
					for (int i = 0; i < 7; ++i) {
						double d = this.random.nextGaussian() * 0.02D;
						double e = this.random.nextGaussian() * 0.02D;
						double f = this.random.nextGaussian() * 0.02D;
						((ServerWorld) this.world).spawnParticles(ParticleTypes.SMOKE, this.getParticleX(1.0D), this.getRandomBodyY() + 0.5D, this.getParticleZ(1.0D), 1, d, e, f, 0f);
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
		nbt.remove("UUID");
		ratItemStack.getOrCreateSubNbt(RatsMischief.MOD_ID).put("rat", nbt);
		player.giveItemStack(ratItemStack);
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

	public boolean canBeLeashedBy(PlayerEntity player) {
		return !this.hasAngerTime() && super.canBeLeashedBy(player);
	}

	@Override
	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		if (!(target instanceof CreeperEntity || (target instanceof GhastEntity))) {
			if (target instanceof RatEntity rat) {
				return !rat.isTamed() || rat.getOwner() != owner;
			} else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).shouldDamagePlayer((PlayerEntity) target)) {
				return false;
			} else if (target instanceof HorseEntity && ((HorseEntity) target).isTame()) {
				return false;
			} else {
				return !(target instanceof TameableEntity) || !((TameableEntity) target).isTamed();
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
		float damage = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
		damage *= MasterRatArmorItem.getDamageMultiplier(this.getOwner());
		target.timeUntilRegen = 0;
		if (target.damage(DamageSource.mob(this), damage)) {
			this.applyDamageEffects(this, target);
			this.onAttacking(target);
			return true;
		}
		return false;
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
	public void emitGameEvent(GameEvent event, @Nullable Entity entity) {}

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
			amount *= MasterRatArmorItem.getResistanceMultiplier(this.getOwner());
			return super.damage(source, amount);
		}
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		if (damageSource == DamageSource.CACTUS || damageSource == DamageSource.SWEET_BERRY_BUSH || damageSource.getAttacker() instanceof EnderDragonEntity || damageSource == DamageSource.CRAMMING) {
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
			return this.getVehicle().getHeight()*0.3f;
		} else {
			return super.getHeightOffset();
		}
	}

	@Override
	protected void pushAway(Entity entity) {
		if (!(entity instanceof PlayerEntity && this.isTamed() && entity.getUuid().equals(this.getOwnerUuid()))) {
			if (!this.isConnectedThroughVehicle(entity)) {
				if (!entity.noClip && !this.noClip) {
					double d = entity.getX() - this.getX();
					double e = entity.getZ() - this.getZ();
					double f = MathHelper.absMax(d, e);
					if (f >= 0.01F) {
						f = Math.sqrt(f);
						d /= f;
						e /= f;
						double g = 1.0 / f;
						if (g > 1.0) {
							g = 1.0;
						}

						d *= g;
						e *= g;
						d *= 0.05F;
						e *= 0.05F;
						if (!this.hasPassengers() && this.isPushable()) {
							this.addVelocity(-d, 0.0, -e);
						}

						if (!entity.hasPassengers() && entity.isPushable()) {
							entity.addVelocity(d, 0.0, e);
						}
					}

				}
			}
		}
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
	protected @Nullable
	SoundEvent getDeathSound() {
		return ModSoundEvents.ENTITY_RAT_DEATH;
	}

	@Override
	protected @Nullable
	SoundEvent getHurtSound(DamageSource source) {
		return ModSoundEvents.ENTITY_RAT_HURT;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		if (!state.getMaterial().isLiquid()) {
			BlockState blockState = this.world.getBlockState(pos.up());
			BlockSoundGroup blockSoundGroup = blockState.isOf(Blocks.SNOW) ? blockState.getSoundGroup() : state.getSoundGroup();
			this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.01F, blockSoundGroup.getPitch());
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		if (fallDistance > 20) {
			this.playSound(ModSoundEvents.ENTITY_RAT_CLAP, 1.0f, (float) (1.0f + random.nextGaussian() / 10f));
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
		this.goalSelector.remove(action);
		this.action = null;
	}

	public enum Type {
		WILD(new Identifier(RatsMischief.MOD_ID, "textures/entity/wild.png")),
		ALBINO(new Identifier(RatsMischief.MOD_ID, "textures/entity/albino.png")),
		BLACK(new Identifier(RatsMischief.MOD_ID, "textures/entity/black.png")),
		GREY(new Identifier(RatsMischief.MOD_ID, "textures/entity/grey.png")),
		HUSKY(new Identifier(RatsMischief.MOD_ID, "textures/entity/husky.png")),
		CHOCOLATE(new Identifier(RatsMischief.MOD_ID, "textures/entity/chocolate.png")),
		LIGHT_BROWN(new Identifier(RatsMischief.MOD_ID, "textures/entity/light_brown.png")),
		RUSSIAN_BLUE(new Identifier(RatsMischief.MOD_ID, "textures/entity/russian_blue.png")),
		GOLD(new Identifier(RatsMischief.MOD_ID, "textures/entity/gold.png")),
		DOCTOR4T(new Identifier(RatsMischief.MOD_ID, "textures/entity/named/doctor4t.png")),
		RAT_KID(null),
		RATATER(new Identifier(RatsMischief.MOD_ID, "textures/entity/named/ratater.png")),
		JORATO(new Identifier(RatsMischief.MOD_ID, "textures/entity/named/jorato.png")),
		JERMA(new Identifier(RatsMischief.MOD_ID, "textures/entity/named/jerma.png")),
		HOLLOW(new Identifier(RatsMischief.MOD_ID, "textures/entity/named/hollow.png")),
		RATELINE(new Identifier(RatsMischief.MOD_ID, "textures/entity/named/rateline.png")),
		BIGGIE_CHEESE(new Identifier(RatsMischief.MOD_ID, "textures/entity/named/biggie_cheese.png")),
		ARATHAIN(new Identifier(RatsMischief.MOD_ID, "textures/entity/named/arathain.png")),
		ASTRONYU(new Identifier(RatsMischief.MOD_ID, "textures/entity/named/astronyu.png"));

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

		public boolean canStart() {
			if (!RatEntity.this.isTamed() || !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
				return false;
			} else if (RatEntity.this.getTarget() == null && RatEntity.this.getAttacker() == null) {
				if (RatEntity.this.isSitting() && RatEntity.this.isEating()) {
					return false;
				} else if (RatEntity.this.getRandom().nextInt(10) != 0) {
					return false;
				} else {
					List<ItemEntity> list = RatEntity.this.world.getEntitiesByClass(ItemEntity.class, RatEntity.this.getBoundingBox().expand(10.0D, 10.0D, 10.0D), RatEntity.PICKABLE_DROP_FILTER);
					return !list.isEmpty() && RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
				}
			} else {
				return false;
			}
		}

		public void tick() {
			List<ItemEntity> list = RatEntity.this.world.getEntitiesByClass(ItemEntity.class, RatEntity.this.getBoundingBox().expand(10.0D, 10.0D, 10.0D), RatEntity.PICKABLE_DROP_FILTER);
			ItemStack itemStack = RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND);
			if (itemStack.isEmpty() && !list.isEmpty()) {
				RatEntity.this.getNavigation().startMovingTo(list.get(0), 1);
			}
		}

		public void start() {
			List<ItemEntity> list = RatEntity.this.world.getEntitiesByClass(ItemEntity.class, RatEntity.this.getBoundingBox().expand(10.0D, 10.0D, 10.0D), RatEntity.PICKABLE_DROP_FILTER);
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
			return super.canStart() && !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && RatEntity.this.isTamed() && !RatEntity.this.isSitting() && !RatEntity.this.isEating() && RatEntity.this.getOwner() != null && RatEntity.this.getOwner().isAlive() && ((PlayerEntity) RatEntity.this.getOwner()).getInventory().getEmptySlot() >= 0;
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && RatEntity.this.isTamed() && !RatEntity.this.isSitting() && !RatEntity.this.isEating() && RatEntity.this.getOwner() != null && RatEntity.this.getOwner().isAlive() && ((PlayerEntity) RatEntity.this.getOwner()).getInventory().getEmptySlot() >= 0;
		}
	}
}
