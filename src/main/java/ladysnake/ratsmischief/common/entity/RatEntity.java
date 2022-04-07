package ladysnake.ratsmischief.common.entity;

import com.google.common.collect.ImmutableList;
import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.entity.ai.ChaseForFunGoal;
import ladysnake.ratsmischief.common.entity.ai.EatToHealGoal;
import ladysnake.ratsmischief.common.entity.ai.FollowOwnerRatGoal;
import ladysnake.ratsmischief.common.item.RatPouchItem;
import ladysnake.ratsmischief.common.item.RatStaffItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
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
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.*;
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
    private static final TrackedData<Integer> ROCKET_TIME = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private final AnimationFactory factory = new AnimationFactory(this);
    public Goal action;
    public int actionTimer = 0;

    // ELYTRAT
    public BlockPos circlingCenter = BlockPos.ORIGIN;
    public Vec3d targetPosition;
    private UUID targetUuid;

    public RatEntity(EntityType<? extends TameableEntity> type, World worldIn) {
        super(type, worldIn);
        this.ignoreCameraFrustum = false;
        this.stepHeight = 2f;
        this.moveControl = new RatMoveControl(this);
        this.lookControl = new RatLookControl(this);
    }

    public static DefaultAttributeContainer.Builder createEntityAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    public static Type getRandomNaturalType(Random random) {
        return NATURAL_TYPES.get(random.nextInt(NATURAL_TYPES.size()));
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        // try spawning a mask of rat
        if (this.isBaby() && this.getRandom().nextInt(200) == 0) {
            this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Mischief.RAT_MASK));
            return;
        }

        // try spawning a staff
        if (this.getRandom().nextInt(20) == 0) {
            switch (this.getRandom().nextInt(4)) {
                case 0 -> this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Mischief.HARVEST_STAFF));
                case 1 -> this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Mischief.COLLECTION_STAFF));
                case 2 -> this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Mischief.SKIRMISH_STAFF));
                case 3 -> this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Mischief.LOVE_STAFF));
            }
            return;
        }

        // try spawning with cake on birthday
        if (Mischief.IS_BIRTHDAY && !this.isBaby() && this.getRandom().nextInt(5) == 0) {
            this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Items.CAKE));
        }
    }


    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityTag) {
        this.initEquipment(difficulty);

        // init doctor4t type or random type on birthday
        if (Mischief.IS_RAT_BIRTHDAY && this.getRandom().nextInt(10) == 0) {
            this.setRatType(Type.DOCTOR4T);
        } else if (Mischief.IS_MISCHIEF_BIRTHDAY && this.getRandom().nextInt(5) == 0) {
            this.setRatType(List.of(Type.values()).get(this.getRandom().nextInt(Type.values().length)));
        }

        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    protected void initDataTracker() {
        super.initDataTracker();

        int bound = 150;
        if (Mischief.IS_WORLD_RAT_DAY) {
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
        this.dataTracker.startTracking(ROCKET_TIME, 0);

        this.dataTracker.startTracking(PARTY_HAT, PARTY_HATS.get(random.nextInt(PARTY_HATS.size())).toString());
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new EatToHealGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.3F));
        this.goalSelector.add(3, new RatEntity.StartAttackGoal()); // elytrat
        this.goalSelector.add(4, new RatEntity.SwoopMovementGoal()); // elytrat
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(5, new RatEntity.PickupItemGoal());
        this.goalSelector.add(5, new BringItemToOwnerGoal(this, 1.0D, false));
        this.goalSelector.add(6, new FlyWithOwnerGoal()); // elytrat
        this.goalSelector.add(6, new FollowOwnerRatGoal(this, 1.0D, 20.0F, 2.0F, false));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this)).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        // wild rats chase HalfOf2
//        this.targetSelector.add(7, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, playerEntity -> ((LivingEntity) playerEntity).getUuidAsString().equals("acc98050-d266-4524-a284-05c2429b540d") && !this.isTamed()));
        this.targetSelector.add(8, new ChaseForFunGoal<>(this, CatEntity.class, true));
        this.targetSelector.add(8, new UniversalAngerGoal<>(this, true));
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.isFlying()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.fly", true));
            return PlayState.CONTINUE;
        }
        if (this.isEating()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.eat", true));
            return PlayState.CONTINUE;
        } else if (this.isSitting()) {
            this.setSniffing(false);
            this.setEating(false);
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.flat", true));
            return PlayState.CONTINUE;
        } else if (event.isMoving()) {
            this.setSniffing(false);
            this.setEating(false);
            if (this.getRatType() == Type.JERMA) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.jermarun", true));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.run", true));
            }
            return PlayState.CONTINUE;
        } else if (this.isSniffing()) {
            if (this.getRatType() == Type.RAT_KID || Mischief.IS_WORLD_RAT_DAY || Mischief.IS_BIRTHDAY) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.smug_dance", true));
            } else {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.sniff", false));
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
        RatEntity ratEntity = Mischief.RAT.create(world);

        if (this.getRatType() == Type.RAT_KID && entity instanceof RatEntity && ((RatEntity) entity).getRatType() == Type.RAT_KID) {
            ratEntity.setRatType(Type.RAT_KID);
        } else {
            int bound = 150;
            if (Mischief.IS_WORLD_RAT_DAY) {
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
            ratEntity.initEquipment(world.getLocalDifficulty(this.getBlockPos()));
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
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound tag) {
        super.writeCustomDataToNbt(tag);

        tag.putString("RatType", this.getRatType().toString());
        tag.putString("Color", this.getRatColor().getName());
        this.writeAngerToNbt(tag);

        tag.putBoolean("Sitting", this.isSitting());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isFlying() && this.isElytrat() && this.world.isClient && this.getRocketTime() > 0) {
            this.setRocketTime(this.getRocketTime() - 1);
            this.world.addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY() + 0.1, this.getZ(), this.random.nextGaussian() * 0.05D, -this.getVelocity().y * 0.5D, this.random.nextGaussian() * 0.05D);
        }

        // turn saturation into regeneration
        if (this.hasStatusEffect(StatusEffects.SATURATION)) {
            StatusEffectInstance saturation = this.getStatusEffect(StatusEffects.SATURATION);
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, saturation.getDuration(), saturation.getAmplifier(), saturation.isAmbient(), saturation.shouldShowParticles(), saturation.shouldShowIcon()));
        }
    }

    @Override
    public void mobTick() {
//        this.setSprinting(this.getMoveControl().isMoving());

        if (this.getTarget() == null && !(this.getOwner() != null && this.getOwner().isFallFlying()) && (this.isTouchingWater() || this.isOnGround())) {
            this.setFlying(false);
        }

        // automatically equip elytrat
        if (!this.getMainHandStack().isEmpty() && !this.isElytrat() && this.getMainHandStack().getItem() == Mischief.ELYTRAT) {
            this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
            this.getMainHandStack().decrement(1);
            this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, 1.0F, 1.2F);
        }

        if (this.age % 20 == 0 && this.isTamed() && this.getOwner() != null && !this.getOwner().getEquippedStack(EquipmentSlot.HEAD).isEmpty() && this.getOwner().getEquippedStack(EquipmentSlot.HEAD).getItem() == Mischief.RAT_MASK) {
            this.getOwner().getActiveStatusEffects().forEach((statusEffect, statusEffectInstance) -> {
                if (!this.hasStatusEffect(statusEffect) || (this.getStatusEffect(statusEffect) != null && this.getStatusEffect(statusEffect).getAmplifier() <= statusEffectInstance.getAmplifier())) {
                    this.addStatusEffect(new StatusEffectInstance(statusEffect, 60, statusEffectInstance.getAmplifier()));
                }
            });
        }

        if (this.isSitting() && !this.isEating() && !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            this.dropStack(this.getEquippedStack(EquipmentSlot.MAINHAND));
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        if (this.isTouchingWater()) {
            this.setSitting(false);
            this.setSniffing(false);
        }

        if (this.hasCustomName()) {
            switch (getCustomName().asString().toLowerCase(Locale.ROOT)) {
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

        if (this.isElytrat() && this.getTarget() != null && this.getTarget().getY() > this.getY() && !this.isFlying()) {
            this.addVelocity(0, 0.5, 0);
            this.setFlying(true);
        }

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

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (this.world.isClient) {
            boolean bl = this.isOwner(player) || this.isTamed() && !this.isTamed() && !this.hasAngerTime();
            return bl ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            if (itemStack.getItem() instanceof DyeItem && this.getRatType() == Type.RAT_KID) {
                this.setRatColor(((DyeItem) itemStack.getItem()).getColor());
                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                return ActionResult.CONSUME;
            }

            if (this.isTamed()) {
                if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    this.heal((float) item.getFoodComponent().getHunger());
                    return ActionResult.SUCCESS;
                }

                if (this.isOwner(player)) {
                    if (player.isSneaking()) {
                        if (!this.isElytrat() && item == Mischief.ELYTRAT) {
                            this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.ELYTRA));
                            if (!player.isCreative()) {
                                itemStack.decrement(1);
                            }
                            this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, 1.0F, 1.2F);
                            return ActionResult.CONSUME;
                        } else {
                            if (this.isElytrat()) {
                                this.dropStack(new ItemStack(Mischief.ELYTRAT));
                                this.playSound(SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA, 1.0F, 1.2F);
                                this.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
                            }
                        }
                    } else if (!(item instanceof RatPouchItem) && !(item instanceof RatStaffItem) && (!this.isBreedingItem(itemStack)) && !(itemStack.getItem() instanceof DyeItem && this.getRatType() == Type.RAT_KID)) {
                        this.setSitting(!this.isSitting());
                    }
                }
            } else if (item.isFood() && !this.hasAngerTime()) {
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

    @Override
    public int getAngerTime() {
        return this.dataTracker.get(ANGER_TIME);
    }

    @Override
    public void setAngerTime(int ticks) {
        this.dataTracker.set(ANGER_TIME, ticks);
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
        if (!(target instanceof CreeperEntity || (target instanceof GhastEntity && !this.isElytrat()))) {
            if (target instanceof RatEntity rat) {
                return !rat.isTamed() || rat.getOwner() != owner;
            } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).shouldDamagePlayer((PlayerEntity) target)) {
                return false;
            } else if (target instanceof HorseBaseEntity && ((HorseBaseEntity) target).isTame()) {
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
        target.timeUntilRegen = 0;
        return super.tryAttack(target);
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

    public boolean isFlying() {
        return this.dataTracker.get(FLYING);
    }

    public void setFlying(boolean eating) {
        this.dataTracker.set(FLYING, eating);
    }

    public int getRocketTime() {
        return this.dataTracker.get(ROCKET_TIME);
    }

    public void setRocketTime(int ticks) {
        this.dataTracker.set(ROCKET_TIME, ticks);
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

            return super.damage(source, amount);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if (damageSource == DamageSource.CACTUS || damageSource == DamageSource.SWEET_BERRY_BUSH || damageSource.getAttacker() instanceof EnderDragonEntity || damageSource.getAttacker() instanceof PlayerEntity && ((PlayerEntity) damageSource.getAttacker()).getEquippedStack(EquipmentSlot.HEAD).getItem() == Mischief.RAT_MASK) {
            return true;
        } else {
            return super.isInvulnerableTo(damageSource);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);

        // HalfOf2 and will_bl
        if ((player.getUuidAsString().equals("acc98050-d266-4524-a284-05c2429b540d") || player.getUuidAsString().equals("9b035372-0d8d-4513-8bd5-9808d7f4a9b3")) && !this.isTamed()) {
            this.remove(RemovalReason.DISCARDED);
            world.createExplosion(this, this.getX(), this.getY(), this.getZ(), 1f, Explosion.DestructionType.NONE);
        }
    }

    @Override
    protected void pushAway(Entity entity) {
        if (!(entity instanceof PlayerEntity && this.isTamed() && entity.getUuid().equals(this.getOwnerUuid()))) {
            super.pushAway(entity);
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
        return Mischief.ENTITY_RAT_DEATH;
    }

    @Override
    protected @Nullable
    SoundEvent getHurtSound(DamageSource source) {
        return Mischief.ENTITY_RAT_HURT;
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
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        if (this.isElytrat()) {
            return 0;
        } else {
            return super.computeFallDamage(fallDistance - 15.0f, damageMultiplier);
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (this.isElytrat()) {
            return false;
        } else {
            return super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
        }
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

        if (this.isElytrat()) {
            this.dropStack(new ItemStack(Mischief.ELYTRAT));
        }

        super.onDeath(source);
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (slot == EquipmentSlot.HEAD) {
            if (this.isTamed() && this.getOwner() != null && !this.getOwner().getEquippedStack(EquipmentSlot.HEAD).isEmpty() && this.getOwner().getEquippedStack(EquipmentSlot.HEAD).getItem() == Mischief.RAT_MASK) {
                return this.getOwner().getEquippedStack(EquipmentSlot.HEAD);
            }
        }

        return super.getEquippedStack(slot);
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

    public boolean isElytrat() {
        return !this.getEquippedStack(EquipmentSlot.CHEST).isEmpty() && this.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem;
    }

    protected BodyControl createBodyControl() {
        return new ElytratBodyControl(this);
    }

    public enum Type {
        WILD(new Identifier(Mischief.MODID, "textures/entity/wild.png"), null),
        ALBINO(new Identifier(Mischief.MODID, "textures/entity/albino.png"), null),
        BLACK(new Identifier(Mischief.MODID, "textures/entity/black.png"), null),
        GREY(new Identifier(Mischief.MODID, "textures/entity/grey.png"), null),
        HUSKY(new Identifier(Mischief.MODID, "textures/entity/husky.png"), null),
        CHOCOLATE(new Identifier(Mischief.MODID, "textures/entity/chocolate.png"), null),
        LIGHT_BROWN(new Identifier(Mischief.MODID, "textures/entity/light_brown.png"), null),
        RUSSIAN_BLUE(new Identifier(Mischief.MODID, "textures/entity/russian_blue.png"), null),
        GOLD(new Identifier(Mischief.MODID, "textures/entity/gold.png"), new Identifier(Mischief.MODID, "textures/entity/gold_elytrat.png")),
        DOCTOR4T(new Identifier(Mischief.MODID, "textures/entity/named/doctor4t.png"), new Identifier(Mischief.MODID, "textures/entity/named/doctor4t_elytrat.png")),
        RAT_KID(null, null),
        RATATER(new Identifier(Mischief.MODID, "textures/entity/named/ratater.png"), new Identifier(Mischief.MODID, "textures/entity/named/ratater_elytrat.png")),
        JORATO(new Identifier(Mischief.MODID, "textures/entity/named/jorato.png"), new Identifier(Mischief.MODID, "textures/entity/named/jorato_elytrat.png")),
        JERMA(new Identifier(Mischief.MODID, "textures/entity/named/jerma.png"), new Identifier(Mischief.MODID, "textures/entity/named/jerma_elytrat.png")),
        HOLLOW(new Identifier(Mischief.MODID, "textures/entity/named/hollow.png"), new Identifier(Mischief.MODID, "textures/entity/named/hollow_elytrat.png")),
        RATELINE(new Identifier(Mischief.MODID, "textures/entity/named/rateline.png"), new Identifier(Mischief.MODID, "textures/entity/named/rateline_elytrat.png")),
        BIGGIE_CHEESE(new Identifier(Mischief.MODID, "textures/entity/named/biggie_cheese.png"), new Identifier(Mischief.MODID, "textures/entity/named/biggie_cheese_elytrat.png")),
        ARATHAIN(new Identifier(Mischief.MODID, "textures/entity/named/arathain.png"), new Identifier(Mischief.MODID, "textures/entity/named/arathain_elytrat.png")),
        ASTRONYU(new Identifier(Mischief.MODID, "textures/entity/named/astronyu.png"), null);

        public final Identifier ratTexture, elytratTexture;

        Type(Identifier ratTexture, Identifier elytratTexture) {
            this.ratTexture = ratTexture;
            this.elytratTexture = elytratTexture;
        }
    }

    public enum PartyHat {
        BLUE, BRAND, BROWN, CYAN, GREEN, LIGHT_BLUE, LIGHT_GREEN, MAGENTA, ORANGE, PINK, PURPLE, RAINBOW, RED, YELLOW
    }

    class PickupItemGoal extends Goal {
        public PickupItemGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
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

    class StartAttackGoal extends Goal {
        private StartAttackGoal() {
        }

        public boolean canStart() {
            return !RatEntity.this.isTouchingWater() && RatEntity.this.isElytrat() && RatEntity.this.getTarget() != null && RatEntity.this.isTarget(RatEntity.this.getTarget(), TargetPredicate.DEFAULT);
        }

        public void start() {
            this.startSwoop();
            RatEntity.this.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 1f, 0.95F + RatEntity.this.random.nextFloat() * 0.1F);
            RatEntity.this.setRocketTime(20 + RatEntity.this.random.nextInt(10));
            RatEntity.this.addVelocity(0, 1, 0);
            RatEntity.this.setFlying(true);
        }

        public void stop() {
            RatEntity.this.circlingCenter = RatEntity.this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, RatEntity.this.circlingCenter).up(10 + RatEntity.this.random.nextInt(20));
            RatEntity.this.setFlying(false);
        }

        private void startSwoop() {
            RatEntity.this.circlingCenter = RatEntity.this.getTarget().getBlockPos().up(20 + RatEntity.this.random.nextInt(20));
            if (RatEntity.this.circlingCenter.getY() < RatEntity.this.world.getSeaLevel()) {
                RatEntity.this.circlingCenter = new BlockPos(RatEntity.this.circlingCenter.getX(), RatEntity.this.world.getSeaLevel() + 1, RatEntity.this.circlingCenter.getZ());
            }
        }
    }

    class SwoopMovementGoal extends MovementGoal {
        private SwoopMovementGoal() {
            super();
        }

        public boolean canStart() {
            return RatEntity.this.isFlying() && RatEntity.this.isElytrat() && RatEntity.this.getTarget() != null;
        }

        public boolean shouldContinue() {
            LivingEntity livingEntity = RatEntity.this.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (livingEntity instanceof PlayerEntity && (livingEntity.isSpectator() || ((PlayerEntity) livingEntity).isCreative())) {
                return false;
            } else if (!this.canStart()) {
                return false;
            } else {
                return RatEntity.this.isFlying();
            }
        }

        public void tick() {
            LivingEntity livingEntity = RatEntity.this.getTarget();
            RatEntity.this.targetPosition = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5D), livingEntity.getZ());
            if (RatEntity.this.getBoundingBox().expand(0.20000000298023224D).intersects(livingEntity.getBoundingBox())) {
                RatEntity.this.tryAttack(livingEntity);
                if (!RatEntity.this.isSilent()) {
                    world.playSoundFromEntity(null, RatEntity.this, Mischief.ENTITY_RAT_BITE, RatEntity.this.getSoundCategory(), RatEntity.this.getSoundVolume(), RatEntity.this.getSoundPitch());
                }
            }
        }
    }

    class FlyWithOwnerGoal extends MovementGoal {
        private FlyWithOwnerGoal() {
            super();
        }

        public boolean canStart() {
            return RatEntity.this.getOwner() != null && RatEntity.this.getOwner().isFallFlying() && !RatEntity.this.isTouchingWater() && RatEntity.this.isElytrat();
        }

        public boolean shouldContinue() {
            LivingEntity owner = RatEntity.this.getOwner();
            if (owner == null) {
                return false;
            } else if (!owner.isAlive()) {
                return false;
            } else if (owner instanceof PlayerEntity && (owner.isSpectator() || ((PlayerEntity) owner).isCreative())) {
                return false;
            } else if (!this.canStart()) {
                return false;
            } else {
                return RatEntity.this.isFlying();
            }
        }

        public void tick() {
            LivingEntity owner = RatEntity.this.getOwner();
            if (owner != null) {
                RatEntity.this.setFlying(true);
                RatEntity.this.targetPosition = new Vec3d(owner.getX(), owner.getBodyY(0.5D), owner.getZ());
            }
        }
    }

    abstract static class MovementGoal extends Goal {
        public MovementGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }
    }

    class RatLookControl extends LookControl {
        public RatLookControl(MobEntity entity) {
            super(entity);
        }

        public void tick() {
            if (!RatEntity.this.isFlying()) {
                super.tick();
            }
        }
    }

    class ElytratBodyControl extends BodyControl {
        public ElytratBodyControl(MobEntity entity) {
            super(entity);
        }

        public void tick() {
            RatEntity.this.headYaw = RatEntity.this.bodyYaw;
            RatEntity.this.bodyYaw = RatEntity.this.getYaw();
        }
    }

    class RatMoveControl extends MoveControl {
        private float targetSpeed = 0.1F;

        public RatMoveControl(MobEntity owner) {
            super(owner);
        }

        public void tick() {
            if (RatEntity.this.isFlying()) {
                if (RatEntity.this.horizontalCollision) {
                    RatEntity var10000 = RatEntity.this;
                    var10000.setYaw(var10000.getYaw() + 180.0F);
                    this.targetSpeed = 0.1F;
                }

                if (RatEntity.this.targetPosition != null) {
                    float f = (float) (RatEntity.this.targetPosition.x - RatEntity.this.getX());
                    float g = (float) (RatEntity.this.targetPosition.y - RatEntity.this.getY());
                    float h = (float) (RatEntity.this.targetPosition.z - RatEntity.this.getZ());
                    double d = MathHelper.sqrt(f * f + h * h);
                    double e = 1.0D - (double) MathHelper.abs(g * 0.7F) / d;
                    f = (float) ((double) f * e);
                    h = (float) ((double) h * e);
                    d = MathHelper.sqrt(f * f + h * h);
                    double i = MathHelper.sqrt(f * f + h * h + g * g);
                    float k = (float) MathHelper.atan2(h, f);
                    float l = MathHelper.wrapDegrees(RatEntity.this.getYaw() + 90.0F);
                    float m = MathHelper.wrapDegrees(k * 57.295776F);
                    RatEntity.this.setYaw(MathHelper.stepUnwrappedAngleTowards(l, m, 4.0F) - 90.0F);
                    RatEntity.this.bodyYaw = RatEntity.this.getYaw();
                    this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 2.0F, 0.01F * (2.0F / this.targetSpeed));

                    float n = (float) (-(MathHelper.atan2(-g, d) * 57.2957763671875D));
                    RatEntity.this.setPitch(n);
                    float o = RatEntity.this.getYaw() + 90.0F;
                    double p = (double) (this.targetSpeed * MathHelper.cos(o * 0.017453292F)) * Math.abs((double) f / i);
                    double q = (double) (this.targetSpeed * MathHelper.sin(o * 0.017453292F)) * Math.abs((double) h / i);
                    double r = (double) (this.targetSpeed * MathHelper.sin(n * 0.017453292F)) * Math.abs((double) g / i);
                    Vec3d vec3d = RatEntity.this.getVelocity();
                    RatEntity.this.setVelocity(vec3d.add((new Vec3d(p, r, q)).subtract(vec3d).multiply(0.2D)));
                }
            } else {
                super.tick();
            }
        }
    }
}
