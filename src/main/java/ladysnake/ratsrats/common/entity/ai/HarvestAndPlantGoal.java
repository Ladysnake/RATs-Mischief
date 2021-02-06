package ladysnake.ratsrats.common.entity.ai;

import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class HarvestAndPlantGoal extends Goal {
    protected final RatEntity rat;
    protected BlockPos targetBlockPos;
    protected double prevSquareDistance;

    public HarvestAndPlantGoal(RatEntity rat) {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        this.rat = rat;
    }

    public boolean canStart() {
        if (this.rat.getTarget() == null && this.rat.getAttacker() == null && !this.rat.isSitting() && this.rat.isTamed() && (this.rat.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || (this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem()).getBlock() instanceof CropBlock))) {
//        List<ItemEntity> list = this.rat.world.getEntitiesByClass(ItemEntity.class, this.rat.getBoundingBox().expand(10.0D, 10.0D, 10.0D), RatEntity.PICKABLE_DROP_FILTER);
            ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);
            for (int i = 0; i < 20; i++) {
                if (itemStack.isEmpty()) {
                    int minX = (int) this.rat.getX() - 5;
                    int maxX = (int) this.rat.getX() + 5;
                    int minY = (int) this.rat.getY() - 1;
                    int maxY = (int) this.rat.getY() + 1;
                    int minZ = (int) this.rat.getZ() - 5;
                    int maxZ = (int) this.rat.getZ() + 5;
                    int randomX = this.rat.getRandom().nextInt(maxX - minX) + minX;
                    int randomY = this.rat.getRandom().nextInt(maxY - minY) + minY;
                    int randomZ = this.rat.getRandom().nextInt(maxZ - minZ) + minZ;

                    BlockPos blockPos = new BlockPos(randomX, randomY, randomZ);
                    BlockState blockState = this.rat.world.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isMature(blockState)) {
                        targetBlockPos = blockPos;
                    }
                } else if (itemStack.getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) itemStack.getItem()).getBlock() instanceof CropBlock) {
                    int minX = (int) this.rat.getX() - 5;
                    int maxX = (int) this.rat.getX() + 5;
                    int minY = (int) this.rat.getY() - 1;
                    int maxY = (int) this.rat.getY() + 1;
                    int minZ = (int) this.rat.getZ() - 5;
                    int maxZ = (int) this.rat.getZ() + 5;
                    int randomX = this.rat.getRandom().nextInt(maxX - minX) + minX;
                    int randomY = this.rat.getRandom().nextInt(maxY - minY) + minY;
                    int randomZ = this.rat.getRandom().nextInt(maxZ - minZ) + minZ;

                    BlockPos blockPos = new BlockPos(randomX, randomY, randomZ);
                    BlockState blockState = this.rat.world.getBlockState(blockPos.add(0, -1, 0));
                    if (blockState.getBlock() == Blocks.FARMLAND && this.rat.world.getBlockState(blockPos).isAir()) {
                        targetBlockPos = blockPos;
                    }
                }
            }

            return targetBlockPos != null;
        } else {
            return false;
        }
    }

    public void tick() {
        ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

        if (itemStack.isEmpty()) {
            if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) <= 2) {
                this.rat.world.breakBlock(targetBlockPos, true, this.rat);
                targetBlockPos = null;
            } else {
                this.rat.getNavigation().startMovingTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 1D);
            }
        } else {
            if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) == prevSquareDistance) {
                this.stop();
            } else {
                this.prevSquareDistance = this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ());
            }

            if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) <= 2) {
                this.rat.world.setBlockState(targetBlockPos, ((AliasedBlockItem) itemStack.getItem()).getBlock().getDefaultState());
                this.rat.getEquippedStack(EquipmentSlot.MAINHAND).decrement(1);
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
