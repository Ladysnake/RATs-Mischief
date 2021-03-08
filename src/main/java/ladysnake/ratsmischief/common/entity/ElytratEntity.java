package ladysnake.ratsmischief.common.entity;

import ladysnake.ratsmischief.common.entity.ai.ChaseForFunGoal;
import ladysnake.ratsmischief.common.entity.ai.EatToHealGoal;
import ladysnake.ratsmischief.common.entity.ai.FollowOwnerRatGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class ElytratEntity extends RatEntity {
    public ElytratMovementType movementType;
    public BlockPos circlingCenter;
    public Vec3d targetPosition;

    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new EatToHealGoal(this));
        this.goalSelector.add(4, new StartAttackGoal());
        this.goalSelector.add(5, new SwoopMovementGoal());
        this.goalSelector.add(6, new CircleMovementGoal());
//        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0D, true));
//        this.goalSelector.add(5, new RatEntity.PickupItemGoal());
//        this.goalSelector.add(5, new BringItemToOwnerGoal(this, 1.0D, 16.0F, 1.0F, false));
        this.goalSelector.add(5, new FollowOwnerRatGoal(this, 1.0D, 20.0F, 2.0F, false));
//        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0D));
//        this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0D));
//        this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
//        this.goalSelector.add(10, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge());
        this.targetSelector.add(4, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, playerEntity -> this.shouldAngerAt((LivingEntity) playerEntity)));
        // wild rats chase HalfOf2
//        this.targetSelector.add(7, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, playerEntity -> ((LivingEntity) playerEntity).getUuidAsString().equals("acc98050-d266-4524-a284-05c2429b540d") && !this.isTamed()));
//        this.targetSelector.add(8, new ChaseForFunGoal(this, CatEntity.class, true));
        this.targetSelector.add(8, new UniversalAngerGoal(this, true));
    }

    public ElytratEntity(EntityType<? extends ElytratEntity> entityType, World world) {
        super(entityType, world);
        this.circlingCenter = BlockPos.ORIGIN;
        this.movementType = ElytratMovementType.CIRCLE;
        this.experiencePoints = 5;
        this.moveControl = new ElytratEntity.ElytratMoveControl(this);
        this.lookControl = new ElytratEntity.ElytratLookControl(this);
    }

    @Override
    protected int computeFallDamage(float fallDistance, float damageMultiplier) {
        return 0;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.isTamed() && this.getOwner() != null) {
            this.circlingCenter = this.getOwner().getBlockPos().add(0, 10, 0);
        }
    }

    class StartAttackGoal extends Goal {
        private int cooldown;

        private StartAttackGoal() {
        }

        public boolean canStart() {
            LivingEntity livingEntity = ElytratEntity.this.getTarget();
            return livingEntity != null ? ElytratEntity.this.isTarget(ElytratEntity.this.getTarget(), TargetPredicate.DEFAULT) : false;
        }

        public void start() {
            this.cooldown = 10;
            ElytratEntity.this.movementType = ElytratEntity.ElytratMovementType.CIRCLE;
            this.startSwoop();
            ElytratEntity.this.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, 10.0F, 0.95F + ElytratEntity.this.random.nextFloat() * 0.1F);
        }

        public void stop() {
            ElytratEntity.this.circlingCenter = ElytratEntity.this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, ElytratEntity.this.circlingCenter).up(10 + ElytratEntity.this.random.nextInt(20));
        }

        public void tick() {
            if (ElytratEntity.this.movementType == ElytratEntity.ElytratMovementType.CIRCLE) {
                --this.cooldown;
                if (this.cooldown <= 0) {
                    ElytratEntity.this.movementType = ElytratEntity.ElytratMovementType.SWOOP;
                    this.startSwoop();
                    this.cooldown = (8 + ElytratEntity.this.random.nextInt(4)) * 20;
                }
            }

        }

        private void startSwoop() {
            ElytratEntity.this.circlingCenter = ElytratEntity.this.getTarget().getBlockPos().up(20 + ElytratEntity.this.random.nextInt(20));
            if (ElytratEntity.this.circlingCenter.getY() < ElytratEntity.this.world.getSeaLevel()) {
                ElytratEntity.this.circlingCenter = new BlockPos(ElytratEntity.this.circlingCenter.getX(), ElytratEntity.this.world.getSeaLevel() + 1, ElytratEntity.this.circlingCenter.getZ());
            }

        }
    }

    class SwoopMovementGoal extends ElytratEntity.MovementGoal {
        private SwoopMovementGoal() {
            super();
        }

        public boolean canStart() {
            return ElytratEntity.this.getTarget() != null && ElytratEntity.this.movementType == ElytratEntity.ElytratMovementType.SWOOP;
        }

        public boolean shouldContinue() {
            LivingEntity livingEntity = ElytratEntity.this.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (livingEntity instanceof PlayerEntity && (((PlayerEntity)livingEntity).isSpectator() || ((PlayerEntity)livingEntity).isCreative())) {
                return false;
            } else if (!this.canStart()) {
                return false;
            } else {
                if (ElytratEntity.this.age % 20 == 0) {
                    List<CatEntity> list = ElytratEntity.this.world.getEntitiesByClass(CatEntity.class, ElytratEntity.this.getBoundingBox().expand(16.0D), EntityPredicates.VALID_ENTITY);
                    if (!list.isEmpty()) {
                        Iterator var3 = list.iterator();

                        while(var3.hasNext()) {
                            CatEntity catEntity = (CatEntity)var3.next();
                            catEntity.hiss();
                        }

                        return false;
                    }
                }

                return true;
            }
        }

        public void start() {
        }

        public void stop() {
            ElytratEntity.this.setTarget((LivingEntity)null);
            ElytratEntity.this.movementType = ElytratEntity.ElytratMovementType.CIRCLE;
        }

        public void tick() {
            LivingEntity livingEntity = ElytratEntity.this.getTarget();
            ElytratEntity.this.targetPosition = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5D), livingEntity.getZ());
            if (ElytratEntity.this.getBoundingBox().expand(0.20000000298023224D).intersects(livingEntity.getBoundingBox())) {
                ElytratEntity.this.tryAttack(livingEntity);
                ElytratEntity.this.movementType = ElytratEntity.ElytratMovementType.CIRCLE;
                if (!ElytratEntity.this.isSilent()) {
                    ElytratEntity.this.world.syncWorldEvent(1039, ElytratEntity.this.getBlockPos(), 0);
                }
            } else if (ElytratEntity.this.horizontalCollision || ElytratEntity.this.hurtTime > 0) {
                ElytratEntity.this.movementType = ElytratEntity.ElytratMovementType.CIRCLE;
            }

        }
    }

    class CircleMovementGoal extends ElytratEntity.MovementGoal {
        private float angle;
        private float radius;
        private float yOffset;
        private float circlingDirection;

        private CircleMovementGoal() {
            super();
        }

        public boolean canStart() {
            return ElytratEntity.this.getTarget() == null || ElytratEntity.this.movementType == ElytratEntity.ElytratMovementType.CIRCLE;
        }

        public void start() {
            this.radius = 5.0F + ElytratEntity.this.random.nextFloat() * 10.0F;
            this.yOffset = -4.0F + ElytratEntity.this.random.nextFloat() * 9.0F;
            this.circlingDirection = ElytratEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.adjustDirection();
        }

        public void tick() {
            if (ElytratEntity.this.random.nextInt(350) == 0) {
                this.yOffset = -4.0F + ElytratEntity.this.random.nextFloat() * 9.0F;
            }

            if (ElytratEntity.this.random.nextInt(250) == 0) {
                ++this.radius;
                if (this.radius > 15.0F) {
                    this.radius = 5.0F;
                    this.circlingDirection = -this.circlingDirection;
                }
            }

            if (ElytratEntity.this.random.nextInt(450) == 0) {
                this.angle = ElytratEntity.this.random.nextFloat() * 2.0F * 3.1415927F;
                this.adjustDirection();
            }

            if (this.isNearTarget()) {
                this.adjustDirection();
            }

            if (ElytratEntity.this.targetPosition.y < ElytratEntity.this.getY() && !ElytratEntity.this.world.isAir(ElytratEntity.this.getBlockPos().down(1))) {
                this.yOffset = Math.max(1.0F, this.yOffset);
                this.adjustDirection();
            }

            if (ElytratEntity.this.targetPosition.y > ElytratEntity.this.getY() && !ElytratEntity.this.world.isAir(ElytratEntity.this.getBlockPos().up(1))) {
                this.yOffset = Math.min(-1.0F, this.yOffset);
                this.adjustDirection();
            }

        }

        private void adjustDirection() {
            if (BlockPos.ORIGIN.equals(ElytratEntity.this.circlingCenter)) {
                ElytratEntity.this.circlingCenter = ElytratEntity.this.getBlockPos();
            }

            this.angle += this.circlingDirection * 15.0F * 0.017453292F;
            ElytratEntity.this.targetPosition = Vec3d.of(ElytratEntity.this.circlingCenter).add((double)(this.radius * MathHelper.cos(this.angle)), (double)(-4.0F + this.yOffset), (double)(this.radius * MathHelper.sin(this.angle)));
        }
    }

    abstract class MovementGoal extends Goal {
        public MovementGoal() {
            this.setControls(EnumSet.of(Control.MOVE));
        }

        protected boolean isNearTarget() {
            return ElytratEntity.this.targetPosition.squaredDistanceTo(ElytratEntity.this.getX(), ElytratEntity.this.getY(), ElytratEntity.this.getZ()) < 4.0D;
        }
    }

    class ElytratLookControl extends LookControl {
        public ElytratLookControl(MobEntity entity) {
            super(entity);
        }

        public void tick() {
            ElytratEntity.this.headYaw = ElytratEntity.this.bodyYaw;
            ElytratEntity.this.bodyYaw = ElytratEntity.this.yaw;
        }
    }

    class ElytratBodyControl extends BodyControl {
        public ElytratBodyControl(MobEntity entity) {
            super(entity);
        }

        public void tick() {
        }
    }

    class ElytratMoveControl extends MoveControl {
        private float targetSpeed = 0.1F;

        public ElytratMoveControl(MobEntity owner) {
            super(owner);
        }

        public void tick() {
            if (ElytratEntity.this.horizontalCollision) {
                ElytratEntity var10000 = ElytratEntity.this;
                var10000.yaw += 180.0F;
                this.targetSpeed = 0.1F;
            }

            float f = (float)(ElytratEntity.this.targetPosition.x - ElytratEntity.this.getX());
            float g = (float)(ElytratEntity.this.targetPosition.y - ElytratEntity.this.getY());
            float h = (float)(ElytratEntity.this.targetPosition.z - ElytratEntity.this.getZ());
            double d = (double)MathHelper.sqrt(f * f + h * h);
            double e = 1.0D - (double)MathHelper.abs(g * 0.7F) / d;
            f = (float)((double)f * e);
            h = (float)((double)h * e);
            d = (double)MathHelper.sqrt(f * f + h * h);
            double i = (double)MathHelper.sqrt(f * f + h * h + g * g);
            float j = ElytratEntity.this.yaw;
            float k = (float)MathHelper.atan2((double)h, (double)f);
            float l = MathHelper.wrapDegrees(ElytratEntity.this.yaw + 90.0F);
            float m = MathHelper.wrapDegrees(k * 57.295776F);
            ElytratEntity.this.yaw = MathHelper.stepUnwrappedAngleTowards(l, m, 4.0F) - 90.0F;
            ElytratEntity.this.bodyYaw = ElytratEntity.this.yaw;
            if (MathHelper.angleBetween(j, ElytratEntity.this.yaw) < 3.0F) {
                this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 5F, 0.02F * (5F / this.targetSpeed));
            } else {
                this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 0.8F, 0.1F);
            }

            float n = (float)(-(MathHelper.atan2((double)(-g), d) * 57.2957763671875D));
            ElytratEntity.this.pitch = n;
            float o = ElytratEntity.this.yaw + 90.0F;
            double p = (double)(this.targetSpeed * MathHelper.cos(o * 0.017453292F)) * Math.abs((double)f / i);
            double q = (double)(this.targetSpeed * MathHelper.sin(o * 0.017453292F)) * Math.abs((double)h / i);
            double r = (double)(this.targetSpeed * MathHelper.sin(n * 0.017453292F)) * Math.abs((double)g / i);
            Vec3d vec3d = ElytratEntity.this.getVelocity();
            ElytratEntity.this.setVelocity(vec3d.add((new Vec3d(p, r, q)).subtract(vec3d).multiply(0.2D)));
        }
    }

    public static enum ElytratMovementType {
        CIRCLE,
        SWOOP;

        private ElytratMovementType() {
        }
    }
}
