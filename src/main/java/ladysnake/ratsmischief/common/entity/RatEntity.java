package ladysnake.ratsmischief.common.entity;

import com.google.common.collect.ImmutableList;
import ladysnake.ratsmischief.common.Rats;
import ladysnake.ratsmischief.common.entity.ai.ChaseForFunGoal;
import ladysnake.ratsmischief.common.entity.ai.EatToHealGoal;
import ladysnake.ratsmischief.common.entity.ai.FollowOwnerRatGoal;
import ladysnake.ratsmischief.common.item.RatPouchItem;
import ladysnake.ratsmischief.common.item.RatStaffItem;
import ladysnake.ratsmischief.common.network.Packets;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class RatEntity extends TameableEntity implements IAnimatable, Angerable {
    private final AnimationFactory factory = new AnimationFactory(this);

    private static final TrackedData<String> TYPE = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> COLOR = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> EATING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final IntRange ANGER_TIME_RANGE = Durations.betweenSeconds(20, 39);
    private UUID targetUuid;

    private static final TrackedData<Boolean> SNIFFING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = (itemEntity) -> {
        return !itemEntity.cannotPickup() && itemEntity.isAlive();
    };

    public static final int SPAWN_RADIUS = 100;

    public Goal action;
    public int actionTimer = 0;

    public RatEntity(EntityType<? extends PathAwareEntity> type, World worldIn) {
        super(Rats.RAT, worldIn);
        this.ignoreCameraFrustum = false;
        this.stepHeight = 2f;
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        if (!this.isBaby()) {
            if (this.getRandom().nextInt(10) == 0) {
                switch (this.getRandom().nextInt(4)) {
                    case 0:
                        this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Rats.HARVEST_STAFF));
                        break;
                    case 1:
                        this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Rats.COLLECTION_STAFF));
                        break;
                    case 2:
                        this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Rats.SKIRMISH_STAFF));
                        break;
                    case 3:
                        this.setStackInHand(Hand.MAIN_HAND, new ItemStack(Rats.LOVE_STAFF));
                        break;
                }
            }
        }
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        this.initEquipment(difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
    }

    protected void initDataTracker() {
        super.initDataTracker();

        if (this.random.nextInt(150) == 0) {
            this.dataTracker.startTracking(TYPE, Type.GOLD.toString());
        } else {
            this.dataTracker.startTracking(TYPE, getRandomNaturalType(this.random).toString());
        }

        this.dataTracker.startTracking(ANGER_TIME, 0);
        this.dataTracker.startTracking(SITTING, false);
        this.dataTracker.startTracking(EATING, false);
        this.dataTracker.startTracking(SNIFFING, false);
        this.dataTracker.startTracking(COLOR, DyeColor.values()[(this.random.nextInt(DyeColor.values().length))].getName());
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new EatToHealGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.3F));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(5, new RatEntity.PickupItemGoal());
        this.goalSelector.add(5, new BringItemToOwnerGoal(this, 1.0D, 16.0F, 1.0F, false));
        this.goalSelector.add(6, new FollowOwnerRatGoal(this, 1.0D, 20.0F, 2.0F, false));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge());
        this.targetSelector.add(4, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, playerEntity -> this.shouldAngerAt((LivingEntity) playerEntity)));
        // wild rats chase HalfOf2
//        this.targetSelector.add(7, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, playerEntity -> ((LivingEntity) playerEntity).getUuidAsString().equals("acc98050-d266-4524-a284-05c2429b540d") && !this.isTamed()));
        this.targetSelector.add(8, new ChaseForFunGoal(this, CatEntity.class, true));
        this.targetSelector.add(8, new UniversalAngerGoal(this, true));
    }

    public static DefaultAttributeContainer.Builder createEntityAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
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
            if (this.getRatType() == Type.RAT_KID) {
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
        RatEntity ratEntity = Rats.RAT.create(world);
        UUID ownerUuid = this.getOwnerUuid();
        if (ownerUuid != null) {
            ratEntity.setOwnerUuid(ownerUuid);
            ratEntity.setTamed(true);
        }

        return ratEntity;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return Packets.newSpawnPacket(this);
    }

    public Type getRatType() {
        return Type.valueOf(this.dataTracker.get(TYPE));
    }

    public void setRatType(Type type) {
        this.dataTracker.set(TYPE, type.toString());
    }

    public DyeColor getRatColor() {
        return DyeColor.byName(this.dataTracker.get(COLOR), DyeColor.WHITE);
    }

    public void setRatColor(DyeColor color) {
        this.dataTracker.set(COLOR, color.getName());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);

        if (tag.contains("RatType")) {
            this.setRatType(Type.valueOf(tag.getString("RatType")));
        }
        this.angerFromTag((ServerWorld) this.world, tag);

        if (tag.contains("Sitting")) {
            this.setSitting(tag.getBoolean("Sitting"));
        }

        if (tag.contains("Color")) {
            this.setRatColor(DyeColor.byName(tag.getString("Color"), DyeColor.WHITE));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);

        tag.putString("RatType", this.getRatType().toString());
        tag.putString("Color", this.getRatColor().getName());
        this.angerToTag(tag);

        tag.putBoolean("Sitting", this.isSitting());
    }

    @Override
    public void mobTick() {
//        this.setSprinting(this.getMoveControl().isMoving());

        if (this.isSitting() && !this.isEating() && !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
            this.dropStack(this.getEquippedStack(EquipmentSlot.MAINHAND));
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        if (this.isTouchingWater()) {
            this.setSitting(false);
            this.setSniffing(false);
        }

        if (this.hasCustomName()) {
            if (this.getCustomName().getString().equalsIgnoreCase("doctor4t")) {
                this.setRatType(Type.DOCTOR4T);
            } else if (this.getCustomName().getString().equalsIgnoreCase("ratater")) {
                this.setRatType(Type.RATATER);
            } else if (this.getCustomName().getString().equalsIgnoreCase("rat kid") || this.getCustomName().getString().equalsIgnoreCase("hat kid")) {
                this.setRatType(Type.RAT_KID);
            } else if (this.getCustomName().getString().equalsIgnoreCase("jotaro") || this.getCustomName().getString().equalsIgnoreCase("jorato")) {
                this.setRatType(Type.JORATO);
            } else if (this.getCustomName().getString().equalsIgnoreCase("jerma") || this.getCustomName().getString().equalsIgnoreCase("jerma985")) {
                this.setRatType(Type.JERMA);
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
        return type != EntityType.CREEPER && super.canTarget(type);
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
            List<ItemEntity> list = this.world.getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand(1.0D, 0.0D, 1.0D));
            Iterator var2 = list.iterator();

            while (var2.hasNext()) {
                ItemEntity itemEntity = (ItemEntity) var2.next();
                if (!itemEntity.removed && !itemEntity.getStack().isEmpty() && !itemEntity.cannotPickup() && this.canGather(itemEntity.getStack())) {
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
                    if (!player.abilities.creativeMode) {
                        itemStack.decrement(1);
                    }

                    this.heal((float) item.getFoodComponent().getHunger());
                    return ActionResult.SUCCESS;
                }

                if (this.isOwner(player) && !(item instanceof RatPouchItem) && !(item instanceof RatStaffItem) && (!this.isBreedingItem(itemStack)) && !(itemStack.getItem() instanceof DyeItem && this.getRatType() == Type.RAT_KID)) {
                    this.setSitting(!this.isSitting());
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
        return (Integer) this.dataTracker.get(ANGER_TIME);
    }

    @Override
    public void setAngerTime(int ticks) {
        this.dataTracker.set(ANGER_TIME, ticks);
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.choose(this.random));
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
        if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
            if (target instanceof RatEntity) {
                RatEntity ratEntity = (RatEntity) target;
                return !ratEntity.isTamed() || ratEntity.getOwner() != owner;
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
        return (Boolean) this.dataTracker.get(SNIFFING);
    }

    public void setSniffing(boolean sniffing) {
        this.dataTracker.set(SNIFFING, sniffing);
    }

    public boolean isEating() {
        return (Boolean) this.dataTracker.get(EATING);
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

            return super.damage(source, amount);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);

        // HalfOf2
        if (player.getUuidAsString().equals("acc98050-d266-4524-a284-05c2429b540d") && !this.isTamed()) {
            this.remove();
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
            this.method_29499(item);
            this.sendPickup(item, itemStack.getCount());
            item.remove();
        }
    }

    @Override
    protected @Nullable
    SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    @Override
    protected @Nullable
    SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_RABBIT_HURT;
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
        return super.computeFallDamage(fallDistance - 15.0f, damageMultiplier);
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

    public static boolean canSpawn(EntityType<RatEntity> entityType, ServerWorldAccess serverWorldAccess, SpawnReason spawnReason, BlockPos blockPos, Random random) {
        ServerWorld world = serverWorldAccess.toServerWorld();
        if (world.locateStructure(StructureFeature.VILLAGE, blockPos, 10, false) != null) {
            List<VillagerEntity> villagersNearby = world.getEntitiesByType(EntityType.VILLAGER, new Box(blockPos.getX() - SPAWN_RADIUS, blockPos.getY() - SPAWN_RADIUS, blockPos.getZ() - SPAWN_RADIUS, blockPos.getX() + SPAWN_RADIUS, blockPos.getY() + SPAWN_RADIUS, blockPos.getZ() + SPAWN_RADIUS), villagerEntity -> true);
            return villagersNearby.isEmpty();
        }

        return false;
    }

    public enum Type {
        ALBINO,
        BLACK,
        GREY,
        HUSKY,
        CHOCOLATE,
        LIGHT_BROWN,
        RUSSIAN_BLUE,
        GOLD,
        DOCTOR4T,
        RAT_KID,
        RATATER,
        JORATO,
        JERMA
    }

    public static Type getRandomNaturalType(Random random) {
        return NATURAL_TYPES.get(random.nextInt(NATURAL_TYPES.size()));
    }

    public static final List<Type> NATURAL_TYPES = ImmutableList.of(
            Type.ALBINO, Type.BLACK, Type.GREY, Type.HUSKY, Type.CHOCOLATE, Type.LIGHT_BROWN, Type.RUSSIAN_BLUE
    );

    public enum Action {
        NONE,
        HARVEST,
        EXCAVATE
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
                RatEntity.this.getNavigation().startMovingTo((Entity) list.get(0), 1);
            }
        }

        public void start() {
            List<ItemEntity> list = RatEntity.this.world.getEntitiesByClass(ItemEntity.class, RatEntity.this.getBoundingBox().expand(10.0D, 10.0D, 10.0D), RatEntity.PICKABLE_DROP_FILTER);
            if (!list.isEmpty()) {
                RatEntity.this.getNavigation().startMovingTo((Entity) list.get(0), 1);
            }
        }
    }

    public class BringItemToOwnerGoal extends FollowOwnerGoal {
        public BringItemToOwnerGoal(TameableEntity tameable, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
            super(tameable, speed, 0.0f, 0.0f, leavesAllowed);
        }

        @Override
        public void tick() {
            super.tick();

            if (RatEntity.this.getOwner() != null && RatEntity.this.getOwner().isAlive()) {
                if (RatEntity.this.squaredDistanceTo(RatEntity.this.getOwner()) <= 3.0f && RatEntity.this.getOwner() instanceof PlayerEntity) {
                    if (((PlayerEntity) RatEntity.this.getOwner()).inventory.getEmptySlot() >= 0) {
                        RatEntity.this.dropStack(RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND));
                        RatEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    }
                }
            }
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && RatEntity.this.isTamed() && !RatEntity.this.isSitting() && !RatEntity.this.isEating() && RatEntity.this.getOwner() != null && RatEntity.this.getOwner().isAlive();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && RatEntity.this.isTamed() && !RatEntity.this.isSitting() && !RatEntity.this.isEating() && RatEntity.this.getOwner() != null && RatEntity.this.getOwner().isAlive();
        }
    }


}
