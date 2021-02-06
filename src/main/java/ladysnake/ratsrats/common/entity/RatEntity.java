package ladysnake.ratsrats.common.entity;

import com.google.common.collect.ImmutableList;
import ladysnake.ratsrats.common.Rats;
import ladysnake.ratsrats.common.entity.ai.HarvestAndPlantGoal;
import ladysnake.ratsrats.common.item.RatPouchItem;
import ladysnake.ratsrats.common.network.Packets;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.IntRange;
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

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

public class RatEntity extends TameableEntity implements IAnimatable, Angerable {
    private final AnimationFactory factory = new AnimationFactory(this);

    private static final TrackedData<String> TYPE = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);;

    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final IntRange ANGER_TIME_RANGE = Durations.betweenSeconds(20, 39);
    private UUID targetUuid;

    private static final TrackedData<Boolean> SNIFFING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = (itemEntity) -> {
        return !itemEntity.cannotPickup() && itemEntity.isAlive();
    };

    public Action action = Action.NONE;
    public int actionTimer = 0;

    public RatEntity(EntityType<? extends PathAwareEntity> type, World worldIn) {
        super(Rats.RAT, worldIn);
        this.ignoreCameraFrustum = false;
        this.stepHeight = 2f;
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
        this.dataTracker.startTracking(SNIFFING, false);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.3F));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(5, new RatEntity.PickupItemGoal());
        this.goalSelector.add(5, new BringItemToOwnerGoal(this, 1.0D, 10.0F, 1.0F, false));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge());
        this.targetSelector.add(4, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, playerEntity -> this.shouldAngerAt((LivingEntity) playerEntity)));
        // wild rats chase HalfOf2
        this.targetSelector.add(7, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, playerEntity -> ((PlayerEntity) playerEntity).getUuidAsString().equals("acc98050-d266-4524-a284-05c2429b540d") && !this.isTamed()));
        this.targetSelector.add(7, new FollowTargetGoal(this, CatEntity.class, true));
        this.targetSelector.add(8, new UniversalAngerGoal(this, true));
    }

    public static DefaultAttributeContainer.Builder createEntityAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.isSitting()) {
            this.setSniffing(false);
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.flat", true));
            return PlayState.CONTINUE;
        } else if (event.isMoving()) {
            this.setSniffing(false);
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.run", true));
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

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);

        if (tag.contains("RatType")) {
            this.setRatType(Type.valueOf(tag.getString("RatType")));
        }
        this.angerFromTag((ServerWorld)this.world, tag);

        if (tag.contains("Sitting")) {
            this.setSitting(tag.getBoolean("Sitting"));
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);

        tag.putString("RatType", this.getRatType().toString());
        this.angerToTag(tag);

        tag.putBoolean("Sitting", this.isSitting());
    }

    @Override
    public void mobTick() {
//        this.setSprinting(this.getMoveControl().isMoving());

        if (this.isSitting() && !this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
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
            } else if (this.getCustomName().getString().equalsIgnoreCase("rat kid") || this.getCustomName().getString().equalsIgnoreCase("hat kid")) {
                this.setRatType(Type.RAT_KID);
            }
        }

        if (!this.hasAngerTime() && !this.moveControl.isMoving() && random.nextInt(100) == 0) {
            this.setSniffing(false);
            this.setSniffing(true);
        }

        // reset
        if (this.actionTimer <= 0) {
            System.out.println("REMOVED ALL SPECIAL GOALS");
            this.removeAllSpecialGoals();
        }

        if (this.action != null && this.actionTimer > 0) {
            this.actionTimer--;
        }
    }

    public void tickMovement() {
        super.tickMovement();

        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        super.interactMob(player, hand);

        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (this.world.isClient) {
            boolean bl = this.isOwner(player) || this.isTamed() && !this.isTamed() && !this.hasAngerTime();
            return bl ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            if (this.isTamed()) {
                if (this.isBreedingItem(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.abilities.creativeMode) {
                        itemStack.decrement(1);
                    }

                    this.heal((float)item.getFoodComponent().getHunger());
                    return ActionResult.SUCCESS;
                }

                if (this.isOwner(player) && !(item instanceof RatPouchItem) && (!this.isBreedingItem(itemStack))) {
                    this.setSitting(!this.isSitting());
                }
            } else if (((this.getRatType() != Type.GOLD && item == Items.MELON_SLICE) || (this.getRatType() == Type.GOLD && item == Items.GLISTERING_MELON_SLICE)) && !this.hasAngerTime()) {
                if (!player.abilities.creativeMode) {
                    itemStack.decrement(1);
                }

                this.setOwner(player);
                this.navigation.stop();
                this.setTarget(null);
                this.world.sendEntityStatus(this, (byte)7);

                return ActionResult.SUCCESS;
            }

            return ActionResult.PASS;
        }
    }

    @Override
    public int getAngerTime() {
        return (Integer)this.dataTracker.get(ANGER_TIME);
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
                RatEntity ratEntity = (RatEntity)target;
                return !ratEntity.isTamed() || ratEntity.getOwner() != owner;
            } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)target)) {
                return false;
            } else if (target instanceof HorseBaseEntity && ((HorseBaseEntity)target).isTame()) {
                return false;
            } else {
                return !(target instanceof TameableEntity) || !((TameableEntity)target).isTamed();
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.getItem() == Items.MELON_SLICE || stack.getItem() == Items.COOKED_CHICKEN || stack.getItem() == Items.COOKED_BEEF;
    }

    @Override
    public boolean tryAttack(Entity target) {
        target.timeUntilRegen = 0;
        return super.tryAttack(target);
    }

    @Override
    public boolean isSitting() {
        return (Boolean) this.dataTracker.get(SITTING);
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
        return this.isTamed() && !this.isSitting();
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
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_RABBIT_DEATH;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_RABBIT_HURT;
    }

    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return super.computeFallDamage(fallDistance-15.0f, damageMultiplier);
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
        RAT_KID
    }

    public static Type getRandomNaturalType(Random random) {
        return NATURAL_TYPES.get(random.nextInt(NATURAL_TYPES.size()));
    }

    public static final List<Type> NATURAL_TYPES = ImmutableList.of(
            Type.ALBINO, Type.BLACK, Type.GREY, Type.HUSKY, Type.CHOCOLATE, Type.LIGHT_BROWN, Type.RUSSIAN_BLUE
    );

    private final HarvestAndPlantGoal HARVEST_GOAL = new HarvestAndPlantGoal(this);

    public enum Action {
        NONE,
        HARVEST
    }

    public void setAction(Action action) {
        removeAllSpecialGoals();
        this.actionTimer = 300; // 15s

        switch (action) {
            case HARVEST:
                this.goalSelector.add(4, HARVEST_GOAL);
                break;
        }
    }

    public void removeAllSpecialGoals() {
        this.action = Action.NONE;
        this.actionTimer = 0;
        this.goalSelector.remove(HARVEST_GOAL);
    }

    class PickupItemGoal extends Goal {
        public PickupItemGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        public boolean canStart() {
            if (!RatEntity.this.isTamed() || !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
                return false;
            } else if (RatEntity.this.getTarget() == null && RatEntity.this.getAttacker() == null) {
                if (RatEntity.this.isSitting()) {
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
                RatEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2000000476837158D);
            }
        }

        public void start() {
            List<ItemEntity> list = RatEntity.this.world.getEntitiesByClass(ItemEntity.class, RatEntity.this.getBoundingBox().expand(10.0D, 10.0D, 10.0D), RatEntity.PICKABLE_DROP_FILTER);
            if (!list.isEmpty()) {
                RatEntity.this.getNavigation().startMovingTo((Entity)list.get(0), 1.2000000476837158D);
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

            if (RatEntity.this.squaredDistanceTo(RatEntity.this.getOwner()) <= 1.0f) {
                RatEntity.this.dropStack(RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND));
                RatEntity.this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() && RatEntity.this.isTamed();
        }

        @Override
        public boolean shouldContinue() {
            return super.shouldContinue() && !RatEntity.this.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty();
        }
    }


}
