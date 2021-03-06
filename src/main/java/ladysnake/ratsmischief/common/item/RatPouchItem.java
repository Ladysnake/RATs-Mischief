package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
    private final int size;
    private static final Predicate<RatEntity> CLOSEST_RAT_PREDICATE = (ratEntity) -> ratEntity.isTamed();

    public RatPouchItem(Settings settings, int size) {
        super(settings);
        this.size = size;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && user.isSneaking()) {
            if (user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).getFloat("filled") == 1f) {
                for (Tag ratTag : user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).getList("rats", NbtType.COMPOUND)) {
                    RatEntity rat = Mischief.RAT.create(world);
                    rat.fromTag((CompoundTag) ratTag);
                    rat.updatePosition(user.getX(), user.getY(), user.getZ());
                    rat.setPos(user.getX(), user.getY(), user.getZ());
                    world.spawnEntity(rat);
                }

                user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).put("rats", new ListTag());
                user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).putFloat("filled", 0F);

                return TypedActionResult.success(user.getStackInHand(hand));
            } else {
                ListTag listTag = user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).getList("rats", NbtType.COMPOUND);

                List<RatEntity> closestTamedRats = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16.0D), CLOSEST_RAT_PREDICATE);
                List<RatEntity> closestOwnedRats = closestTamedRats.stream().filter(ratEntity -> ratEntity.getOwnerUuid() != null && ratEntity.getOwnerUuid().equals(user.getUuid())).collect(Collectors.toList());

                if (closestOwnedRats.size() > 0) {
                    for (int i = 0; i < this.size; i++) {
                        if (i < closestOwnedRats.size()) {
                            CompoundTag compoundTag = new CompoundTag();
                            closestOwnedRats.get(i).saveToTag(compoundTag);
                            listTag.add(compoundTag);
                            closestOwnedRats.get(i).playSpawnEffects();
                            closestOwnedRats.get(i).remove();
                        } else {
                            break;
                        }
                    }

                    user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).put("rats", listTag);
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
        ListTag listTag = user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).getList("rats", NbtType.COMPOUND);

        if (listTag.size() < this.size && entity instanceof RatEntity && ((RatEntity) entity).getOwnerUuid() != null && ((RatEntity) entity).getOwnerUuid().equals(user.getUuid())) {
            CompoundTag compoundTag = new CompoundTag();
            entity.saveToTag(compoundTag);
            listTag.add(compoundTag);
            user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).put("rats", listTag);
            ((RatEntity) entity).playSpawnEffects();
            entity.remove();
            user.getStackInHand(hand).getOrCreateSubTag(Mischief.MODID).putFloat("filled", 1F);

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ListTag listTag = stack.getOrCreateSubTag(Mischief.MODID).getList("rats", NbtType.COMPOUND);

        tooltip.add(new TranslatableText("item.ratsmischief.rat_pouch.tooltip.capacity", listTag.size(), this.size).setStyle(EMPTY.withColor(Formatting.GRAY)));

        for (Tag ratTag : listTag) {
            TranslatableText ratType = new TranslatableText("type.ratsmischief." + ((CompoundTag) ratTag).getString("RatType").toLowerCase());

            Style style = EMPTY.withColor(Formatting.DARK_GRAY);
            if (((CompoundTag) ratTag).getString("RatType").equals(RatEntity.Type.GOLD.name())) {
                style = EMPTY.withColor(Formatting.GOLD);
            }
            if (((CompoundTag) ratTag).contains("CustomName")) {
                Matcher matcher = Pattern.compile("\\{\"text\":\"(.+)\"\\}").matcher(((CompoundTag) ratTag).getString("CustomName"));
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
