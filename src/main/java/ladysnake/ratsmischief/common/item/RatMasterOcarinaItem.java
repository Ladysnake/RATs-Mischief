package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.client.RatsMischiefClientHelper;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.entity.ai.BreedGoal;
import ladysnake.ratsmischief.common.entity.ai.DigGoal;
import ladysnake.ratsmischief.common.entity.ai.HarvestPlantMealGoal;
import ladysnake.ratsmischief.mialeemisc.items.IClickConsumingItem;
import ladysnake.ratsmischief.mialeemisc.util.MialeeMath;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RatMasterOcarinaItem extends Item implements IClickConsumingItem {
	public RatMasterOcarinaItem(Settings settings) {
		super(settings);
	}

	public static Action getAction(ItemStack stack) {
		NbtCompound compound = stack.getOrCreateNbt();
		return Action.values()[MialeeMath.clampLoop(compound.getInt("action"), 0, Action.values().length)];
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		final List<RatEntity> ratEntityList = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16f), ratEntity -> ratEntity.isTamed() && ratEntity.getOwner() != null && ratEntity.getOwner().equals(user));
		ratEntityList.forEach(ratEntity -> {
			Goal goal = null;
			switch (getAction(user.getStackInHand(hand))) {
				case HARVEST -> goal = new HarvestPlantMealGoal(ratEntity);
				case COLLECT -> ratEntity.removeCurrentActionGoal();
				case SKIRMISH ->
					goal = new TargetGoal<>(ratEntity, HostileEntity.class, 10, true, false, livingEntity -> true);
				case LOVE -> goal = new BreedGoal(ratEntity);
			}
			if (goal != null) {
				ratEntity.setAction(goal);
			}
		});
		ItemStack stack = user.getStackInHand(hand);
		world.playSoundFromEntity(null, user, SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(), user.getSoundCategory(), 1f, 0.5f + (stack.getOrCreateNbt().getInt("action") / 4f));
		return TypedActionResult.success(user.getStackInHand(hand));
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity user = context.getPlayer();
		if (user != null && getAction(context.getStack()) == Action.COLLECT) {
			List<RatEntity> ratEntityList = context.getWorld().getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16f), ratEntity -> user.equals(ratEntity.getOwner()));
			ratEntityList.forEach(ratEntity -> {
				Goal goal = new DigGoal(ratEntity, context.getWorld().getBlockState(context.getBlockPos()).getBlock());
				ratEntity.setAction(goal);
			});
			return ActionResult.SUCCESS;
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return false;
	}

	@Override
	public void mialeeMisc$doAttack(ServerPlayerEntity serverPlayerEntity) {
		ItemStack stack = serverPlayerEntity.getMainHandStack();
		NbtCompound compound = stack.getOrCreateNbt();
		compound.putInt("action", MialeeMath.clampLoop(compound.getInt("action") + (serverPlayerEntity.isSneaking() ? -1 : 1), 0, Action.values().length));
	}

	@Override
	public Text getName(ItemStack stack) {
		return super.getName(stack).copy().append(" (" + getAction(stack).name() + ")");
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.ratsmischief.rat_master_ocarina.tooltip").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("item.ratsmischief.rat_master_ocarina.%s.tooltip".formatted(getAction(stack).name().toLowerCase())).formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("item.ratsmischief.rat_master_ocarina.bring_items.%s".formatted(RatsMischiefClientHelper.shouldRatsBringItems())).formatted(Formatting.GRAY));
		super.appendTooltip(stack, world, tooltip, context);
	}

	public enum Action {
		HARVEST,
		COLLECT,
		SKIRMISH,
		LOVE
	}
}
