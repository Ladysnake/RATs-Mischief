package ladysnake.ratsmischief.common.item;

import dev.emi.trinkets.api.TrinketItem;
import ladysnake.ratsmischief.common.cca.RatHornsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class RatHornsItem extends TrinketItem {
    public RatHornsItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(@NotNull World world, @NotNull PlayerEntity user, Hand hand) {
        var itemStack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.success(itemStack);
        var component = RatHornsComponent.KEY.get(user);
        if (!component.hasHorns()) {
            component.setHorns(true);
            itemStack.decrement(1);
        } else {
            component.setHorns(false);
        }
        return TypedActionResult.success(itemStack);
    }
}