package ladysnake.ratsrats.common.entity;

import ladysnake.ratsrats.common.Rats;
import ladysnake.ratsrats.common.item.RatPouchItem;
import ladysnake.ratsrats.common.network.Packets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class RatEntity extends TameableEntity implements IAnimatable, Angerable {
    private final AnimationFactory factory = new AnimationFactory(this);

    private static final TrackedData<String> TYPE = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Boolean> SITTING = DataTracker.registerData(RatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);;

    private static final TrackedData<Integer> ANGER_TIME = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final IntRange ANGER_TIME_RANGE = Durations.betweenSeconds(20, 39);
    private UUID targetUuid;

    public RatEntity(EntityType<? extends PathAwareEntity> type, World worldIn) {
        super(Rats.RAT, worldIn);
        this.ignoreCameraFrustum = false;
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
    }

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.3F));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge());
        this.targetSelector.add(4, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, livingEntity -> this.shouldAngerAt((LivingEntity) livingEntity)));
        this.targetSelector.add(7, new FollowTargetGoal(this, CatEntity.class, true));
        this.targetSelector.add(8, new UniversalAngerGoal(this, true));
    }

    public static DefaultAttributeContainer.Builder createEntityAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5D).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        if (this.isSitting()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.flat", true));
            return PlayState.CONTINUE;
        }

        if (event.isMoving()) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.rat.run", true));
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

    }

    public void tickMovement() {
        super.tickMovement();

        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }
    }

    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        Item item = itemStack.getItem();
        if (this.world.isClient) {
            if (this.isOwner(player) && !(item instanceof RatPouchItem)) {
                this.setSitting(!this.isSitting());
            }
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

                if (this.isOwner(player) && !(item instanceof RatPouchItem)) {
                    this.setSitting(!this.isSitting());
                }
            } else if (((this.getRatType() != Type.GOLD && item == Items.MELON_SLICE) || (this.getRatType() == Type.GOLD && item == Items.GLISTERING_MELON_SLICE)) && !this.hasAngerTime()) {
                if (!player.abilities.creativeMode) {
                    itemStack.decrement(1);
                }

                this.setOwner(player);
                this.navigation.stop();
                this.setTarget((LivingEntity)null);
                this.world.sendEntityStatus(this, (byte)7);

                return ActionResult.SUCCESS;
            }

            return super.interactMob(player, hand);
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

    public enum Type {
        ALBINO,
        BLACK,
        GREY,
        HUSKY,
        CHOCOLATE,
        LIGHT_BROWN,
        RUSSIAN_BLUE,
        GOLD
    }

    public static Type getRandomNaturalType(Random random) {
        return NATURAL_TYPES.get(random.nextInt(NATURAL_TYPES.size()));
    }

    public static final ArrayList<Type> NATURAL_TYPES = new ArrayList<Type>(List.of(
            Type.ALBINO, Type.BLACK, Type.GREY, Type.HUSKY, Type.CHOCOLATE, Type.LIGHT_BROWN, Type.RUSSIAN_BLUE
    ));
}
