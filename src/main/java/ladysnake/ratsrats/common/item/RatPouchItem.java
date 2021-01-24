package ladysnake.ratsrats.common.item;

import ladysnake.ratsrats.common.Rats;
import ladysnake.ratsrats.common.entity.RatEntity;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RatPouchItem extends Item {
    private final int size;

    public RatPouchItem(Settings settings, int size) {
        super(settings);
        this.size = size;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && user.isSneaking()) {
            if (user.getStackInHand(hand).getOrCreateSubTag(Rats.MODID).getFloat("filled") == 1f) {
                for (Tag ratTag : user.getStackInHand(hand).getOrCreateSubTag(Rats.MODID).getList("rats", NbtType.COMPOUND)) {
                    RatEntity rat = Rats.RAT.create(world);
                    rat.fromTag((CompoundTag) ratTag);
                    rat.updatePosition(user.getX(), user.getY(), user.getZ());
                    rat.setPos(user.getX(), user.getY(), user.getZ());
                    world.spawnEntity(rat);
                }

                user.getStackInHand(hand).getOrCreateSubTag(Rats.MODID).put("rats", new ListTag());
                user.getStackInHand(hand).getOrCreateSubTag(Rats.MODID).putFloat("filled", 0F);

                return TypedActionResult.success(user.getStackInHand(hand));
            } else {
                return TypedActionResult.pass(user.getStackInHand(hand));
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        ListTag listTag = user.getStackInHand(hand).getOrCreateSubTag(Rats.MODID).getList("rats", NbtType.COMPOUND);

        if (listTag.size() < this.size && entity instanceof RatEntity) {
            CompoundTag compoundTag = new CompoundTag();
            entity.saveToTag(compoundTag);
            listTag.add(compoundTag);
            user.getStackInHand(hand).getOrCreateSubTag(Rats.MODID).put("rats", listTag);
            entity.remove();
            user.getStackInHand(hand).getOrCreateSubTag(Rats.MODID).putFloat("filled", 1F);

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

}
