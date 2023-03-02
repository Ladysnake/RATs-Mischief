package doctor4t.ratsmischief.common.item;

import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.entity.RatEntity;
import doctor4t.ratsmischief.common.init.ModEntities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.SoundKeyframeEvent;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.network.GeckoLibNetwork;
import software.bernie.geckolib3.network.ISyncable;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.text.Style.EMPTY;

public class RatItem extends Item implements IAnimatable, ISyncable {
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public RatItem(Settings settings) {
		super(settings);
		GeckoLibNetwork.registerSyncable(this);
	}

	private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		AnimationController<JackInTheBoxItem> controller = new AnimationController(this, "idle", 20,
				this::predicate);

		data.addAnimationController(controller);
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public void onAnimationSync(int id, int state) {

	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
			NbtElement ratTag = user.getStackInHand(hand).getOrCreateSubNbt(RatsMischief.MOD_ID).get("rat");
			RatEntity rat = ModEntities.RAT.create(world);
			rat.readNbt((NbtCompound) ratTag);
			rat.updatePosition(user.getX(), user.getY(), user.getZ());
			rat.setPos(user.getX(), user.getY(), user.getZ());
			world.spawnEntity(rat);

			return TypedActionResult.success(user.getStackInHand(hand));
		}

		return super.use(world, user, hand);
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		NbtElement ratTag = stack.getOrCreateSubNbt(RatsMischief.MOD_ID).get("rat");
		if (ratTag == null) {
			RatEntity rat = new RatEntity(ModEntities.RAT, world);
			NbtCompound nbt = new NbtCompound();
			rat.saveNbt(nbt);
			stack.getOrCreateSubNbt(RatsMischief.MOD_ID).put("rat", nbt);

			ratTag = stack.getOrCreateSubNbt(RatsMischief.MOD_ID).get("rat");
		}

		MutableText ratType = Text.translatable("type.ratsmischief." + ((NbtCompound) ratTag).getString("RatType").toLowerCase());

		Style style = EMPTY.withColor(Formatting.DARK_GRAY);
		if (((NbtCompound) ratTag).getString("RatType").equals(RatEntity.Type.GOLD.name())) {
			style = EMPTY.withColor(Formatting.GOLD);
		}
		if (((NbtCompound) ratTag).contains("CustomName")) {
			Matcher matcher = Pattern.compile("\\{\"text\":\"(.+)\"\\}").matcher(((NbtCompound) ratTag).getString("CustomName"));
			if (matcher.find()) {
				String name = matcher.group(1);
				tooltip.add(Text.literal(name).append(" (").append(ratType).append(")").setStyle(style));
			}
		} else {
			tooltip.add(ratType.setStyle(style));
		}

		super.appendTooltip(stack, world, tooltip, context);
	}

	public static RatEntity.Type getRatType(ItemStack stack) {
		String ratType = ((NbtCompound) stack.getOrCreateSubNbt(RatsMischief.MOD_ID).get("rat")).getString("RatType").toUpperCase();

		return RatEntity.Type.valueOf(ratType);
	}

	public static String getRatName(ItemStack stack) {
		return stack.hasCustomName() ? stack.getName().getString() : null;
	}

	public static DyeColor getRatColor(ItemStack stack) {
		String ratColor = ((NbtCompound) stack.getOrCreateSubNbt(RatsMischief.MOD_ID).get("rat")).getString("Color").toUpperCase();

		return DyeColor.valueOf(ratColor);
	}

}
