package ladysnake.ratsrats.common.item;

import ladysnake.ratsrats.common.entity.RatEntity;
import ladysnake.ratsrats.common.entity.ai.DigGoal;
import ladysnake.ratsrats.common.entity.ai.HarvestAndPlantGoal;
import net.minecraft.block.Material;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class RatStaffItem extends Item {
    public final Action action;

    public RatStaffItem(Settings settings, Action action) {
        super(settings);
        this.action = action;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final List<RatEntity> ratEntityList = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16f), ratEntity -> ratEntity.isTamed() && ratEntity.getOwner().equals(user));
        ratEntityList.forEach(ratEntity -> {
            Goal goal;
            switch (action) {
                case HARVEST:
                    goal = new HarvestAndPlantGoal(ratEntity);
                    break;
                case COLLECT:
                    Hand blockHand;
                    if (user.getMainHandStack().getItem() instanceof RatStaffItem) {
                        blockHand = Hand.OFF_HAND;
                    } else {
                        blockHand = Hand.MAIN_HAND;
                    }
                    goal = new DigGoal(ratEntity, null);
                    if (user.getStackInHand(blockHand).getItem() instanceof BlockItem) {
                        if (user.getStackInHand(blockHand).getItem() instanceof BlockItem && ((BlockItem) user.getStackInHand(blockHand).getItem()).getBlock().getBlastResistance() <= 0.6f && ((BlockItem) user.getStackInHand(blockHand).getItem()).getBlock().getDefaultState().getMaterial() != Material.GLASS && !((BlockItem) user.getStackInHand(blockHand).getItem()).getBlock().getDefaultState().isToolRequired() || ((BlockItem) user.getStackInHand(blockHand).getItem()).getBlock().getDefaultState().getMaterial() == Material.SNOW_LAYER || ((BlockItem) user.getStackInHand(blockHand).getItem()).getBlock().getDefaultState().getMaterial() == Material.SNOW_BLOCK) {
                            goal = new DigGoal(ratEntity, ((BlockItem) user.getStackInHand(blockHand).getItem()).getBlock());
                        }
                    }
                    break;
                case SKIRMISH:
                    goal = new FollowTargetGoal<>(ratEntity, HostileEntity.class, 10, true, false, livingEntity -> true);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + action);
            }

            ratEntity.setAction(goal);
        });

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public static enum Action {
        HARVEST,
        COLLECT,
        SKIRMISH
    }
}
