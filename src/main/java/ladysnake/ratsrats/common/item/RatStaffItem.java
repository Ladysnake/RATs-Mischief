package ladysnake.ratsrats.common.item;

import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class RatStaffItem extends Item {
    public final RatEntity.Action action;

    public RatStaffItem(Settings settings, RatEntity.Action action) {
        super(settings);
        this.action = action;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        final List<RatEntity> ratEntityList = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16f), ratEntity -> ratEntity.isTamed() && ratEntity.getOwner().equals(user));
        ratEntityList.forEach(ratEntity -> ratEntity.setAction(this.action));

        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
