package ladysnake.ratsmischief.common.entity.ai;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class HarvestPlantMealGoal extends Goal {
    protected final RatEntity rat;
    protected BlockPos targetBlockPos;

    public HarvestPlantMealGoal(RatEntity rat) {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        this.rat = rat;
    }

    public boolean canStart() {
        targetBlockPos = null;

        if (this.rat.getTarget() == null && this.rat.getAttacker() == null && !this.rat.isSitting() && this.rat.isTamed() && (this.rat.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || (this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem()).getBlock() instanceof CropBlock) || this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem() instanceof BoneMealItem)) {
            ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

            for (BlockPos blockPos : BlockPos.iterateOutwards(this.rat.getBlockPos(), 8, 2, 8)) {
                if (itemStack.isEmpty()) {
                    // harvest
                    BlockState blockState = this.rat.world.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isMature(blockState)) {
                        if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
                            targetBlockPos = blockPos;
                            return true;
                        }
                    }
                } else if (itemStack.getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) itemStack.getItem()).getBlock() instanceof CropBlock) {
                    // plant
                    BlockState blockState = this.rat.world.getBlockState(blockPos.add(0, -1, 0));
                    if (blockState.getBlock() instanceof FarmlandBlock && this.rat.world.getBlockState(blockPos).isAir()) {
                        if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
                            targetBlockPos = blockPos;
                            return true;
                        }
                    }
                } else if (itemStack.getItem() instanceof BoneMealItem) {
                    // bonemeal
                    BlockState blockState = this.rat.world.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isFertilizable(this.rat.world, blockPos, this.rat.world.getBlockState(blockPos), this.rat.world.isClient())) {
                        if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
                            targetBlockPos = blockPos;
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void tick() {
        ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

        if (itemStack.isEmpty()) {
            // harvest
            BlockState blockState = this.rat.world.getBlockState(targetBlockPos);
            if (!(blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isMature(blockState))) {
                this.canStart();
            }

            if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) <= 5) {
                this.rat.world.breakBlock(targetBlockPos, true, this.rat);
                targetBlockPos = null;
            } else {
                this.rat.getNavigation().startMovingTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 1D);
            }
        } else if (itemStack.getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) itemStack.getItem()).getBlock() instanceof CropBlock) {
            // plant
            BlockState blockState = this.rat.world.getBlockState(targetBlockPos.add(0, -1, 0));
            if (!(blockState.getBlock() instanceof FarmlandBlock && this.rat.world.getBlockState(targetBlockPos).isAir())) {
                this.canStart();
            }

            if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) <= 5) {
                this.rat.world.setBlockState(targetBlockPos, ((AliasedBlockItem) itemStack.getItem()).getBlock().getDefaultState());
                this.rat.getEquippedStack(EquipmentSlot.MAINHAND).decrement(1);
                targetBlockPos = null;
            } else {
                this.rat.getNavigation().startMovingTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 1D);
            }
        } else if (itemStack.getItem() instanceof BoneMealItem) {
            // bonemeal
            BlockState blockState = this.rat.world.getBlockState(targetBlockPos);
            if (blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isFertilizable(this.rat.world, targetBlockPos, this.rat.world.getBlockState(targetBlockPos), this.rat.world.isClient())) {
                this.canStart();
            }

            if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) <= 5) {
                BoneMealItem.useOnFertilizable(itemStack, this.rat.world, targetBlockPos);

                ServerWorld serverWorld = (ServerWorld) this.rat.getWorld();

                serverWorld.playSound(null, targetBlockPos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.NEUTRAL, 1.0f, 1.0f);

                serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 15, 0.5f, 0.2f, 0.5f, 0);

                targetBlockPos = null;
            } else {
                this.rat.getNavigation().startMovingTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 1D);
            }
        }
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue();
    }

    @Override
    public void start() {
        super.start();
    }
}
