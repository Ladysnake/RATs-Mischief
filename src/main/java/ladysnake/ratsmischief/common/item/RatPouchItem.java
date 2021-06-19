package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.minecraft.text.Style.EMPTY;

public class RatPouchItem extends Item {
    private static final Predicate<RatEntity> CLOSEST_RAT_PREDICATE = (ratEntity) -> ratEntity.isTamed();
    private final int size;

    public RatPouchItem(Settings settings, int size) {
        super(settings);
        this.size = size;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && user.isSneaking()) {
            if (user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).getFloat("filled") == 1f) {
                for (NbtElement ratTag : user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).getList("rats", NbtType.COMPOUND)) {
                    RatEntity rat = Mischief.RAT.create(world);
                    rat.readNbt((NbtCompound) ratTag);
                    rat.updatePosition(user.getX(), user.getY(), user.getZ());
                    rat.setPos(user.getX(), user.getY(), user.getZ());
                    world.spawnEntity(rat);
                }

                user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).put("rats", new NbtList());
                user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).putFloat("filled", 0F);

                return TypedActionResult.success(user.getStackInHand(hand));
            } else {
                NbtList NbtList = user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).getList("rats", NbtType.COMPOUND);

                List<RatEntity> closestTamedRats = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16.0D), CLOSEST_RAT_PREDICATE);
                List<RatEntity> closestOwnedRats = closestTamedRats.stream().filter(ratEntity -> ratEntity.getOwnerUuid() != null && ratEntity.getOwnerUuid().equals(user.getUuid())).collect(Collectors.toList());

                if (closestOwnedRats.size() > 0) {
                    for (int i = 0; i < this.size; i++) {
                        if (i < closestOwnedRats.size()) {
                            NbtCompound NbtCompound = new NbtCompound();
                            closestOwnedRats.get(i).saveNbt(NbtCompound);
                            NbtList.add(NbtCompound);
                            closestOwnedRats.get(i).playSpawnEffects();
                            closestOwnedRats.get(i).remove(Entity.RemovalReason.DISCARDED);
                        } else {
                            break;
                        }
                    }

                    user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).put("rats", NbtList);
                    user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).putFloat("filled", 1F);
                    return TypedActionResult.success(user.getStackInHand(hand));
                } else {
                    return TypedActionResult.fail(user.getStackInHand(hand));
                }
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        NbtList NbtList = user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).getList("rats", NbtType.COMPOUND);

        if (NbtList.size() < this.size && entity instanceof RatEntity && ((RatEntity) entity).getOwnerUuid() != null && ((RatEntity) entity).getOwnerUuid().equals(user.getUuid())) {
            NbtCompound NbtCompound = new NbtCompound();
            entity.saveNbt(NbtCompound);
            NbtList.add(NbtCompound);
            user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).put("rats", NbtList);
            ((RatEntity) entity).playSpawnEffects();
            entity.remove(Entity.RemovalReason.DISCARDED);
            user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).putFloat("filled", 1F);

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtList NbtList = stack.getOrCreateSubTag(Mischief.MODID).getList("rats", NbtType.COMPOUND);

        tooltip.add(new TranslatableText("item.ratsmischief.rat_pouch.tooltip.capacity", NbtList.size(), this.size).setStyle(EMPTY.withColor(Formatting.GRAY)));

        for (NbtElement ratTag : NbtList) {
            TranslatableText ratType = new TranslatableText("type.ratsmischief." + ((NbtCompound) ratTag).getString("RatType").toLowerCase());

            Style style = EMPTY.withColor(Formatting.DARK_GRAY);
            if (((NbtCompound) ratTag).getString("RatType").equals(RatEntity.Type.GOLD.name())) {
                style = EMPTY.withColor(Formatting.GOLD);
            }
            if (((NbtCompound) ratTag).contains("CustomName")) {
                Matcher matcher = Pattern.compile("\\{\"text\":\"(.+)\"\\}").matcher(((NbtCompound) ratTag).getString("CustomName"));
                if (matcher.find()) {
                    String name = matcher.group(1);
                    tooltip.add(new LiteralText(name).append(" (").append(ratType).append(")").setStyle(style));
                }
            } else {
                tooltip.add(ratType.setStyle(style));
            }
        }

        super.appendTooltip(stack, world, tooltip, context);
    }
}
