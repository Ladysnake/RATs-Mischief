package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.entity.ai.BreedGoal;
import ladysnake.ratsmischief.common.entity.ai.DigGoal;
import ladysnake.ratsmischief.common.entity.ai.HarvestPlantMealGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
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
        final List<RatEntity> ratEntityList = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16f), ratEntity -> ratEntity.isTamed() && ratEntity.getOwner() != null && ratEntity.getOwner().equals(user));
        ratEntityList.forEach(ratEntity -> {
            Goal goal = null;
            switch (action) {
                case HARVEST -> goal = new HarvestPlantMealGoal(ratEntity);
                case COLLECT -> ratEntity.removeCurrentActionGoal();
                case SKIRMISH -> goal = new TargetGoal<>(ratEntity, HostileEntity.class, 10, true, false, livingEntity -> true);
                case LOVE -> goal = new BreedGoal(ratEntity);
                default -> throw new IllegalStateException("Unexpected value: " + action);
            }

            if (goal != null) {
                ratEntity.setAction(goal);
            }
        });

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (this.action == Action.COLLECT) {
            PlayerEntity user = context.getPlayer();

            final List<RatEntity> ratEntityList = context.getWorld().getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16f), ratEntity -> ratEntity.isTamed() && ratEntity.getOwner() != null && ratEntity.getOwner().equals(user));
            ratEntityList.forEach(ratEntity -> {
                Goal goal = new DigGoal(ratEntity, context.getWorld().getBlockState(context.getBlockPos()).getBlock());
                ratEntity.setAction(goal);
            });

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    public static enum Action {
        HARVEST,
        COLLECT,
        SKIRMISH,
        LOVE
    }
}
