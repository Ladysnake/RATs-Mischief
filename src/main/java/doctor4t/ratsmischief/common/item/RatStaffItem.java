package doctor4t.ratsmischief.common.item;

import doctor4t.ratsmischief.common.entity.RatEntity;
import doctor4t.ratsmischief.common.entity.ai.BreedGoal;
import doctor4t.ratsmischief.common.entity.ai.DigGoal;
import doctor4t.ratsmischief.common.entity.ai.HarvestPlantMealGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TargetGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import xyz.amymialee.mialeemisc.items.IClickConsumingItem;
import xyz.amymialee.mialeemisc.util.MialeeMath;

import java.util.List;

public class RatStaffItem extends Item implements IClickConsumingItem {
	public RatStaffItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		final List<RatEntity> ratEntityList = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16f), ratEntity -> ratEntity.isTamed() && ratEntity.getOwner() != null && ratEntity.getOwner().equals(user));
		ratEntityList.forEach(ratEntity -> {
			Goal goal = null;
			switch (getAction(user.getStackInHand(hand))) {
				case HARVEST -> goal = new HarvestPlantMealGoal(ratEntity);
				case COLLECT -> ratEntity.removeCurrentActionGoal();
				case SKIRMISH -> goal = new TargetGoal<>(ratEntity, HostileEntity.class, 10, true, false, livingEntity -> true);
				case LOVE -> goal = new BreedGoal(ratEntity);
			}
			if (goal != null) {
				ratEntity.setAction(goal);
			}
		});
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
	public void mialeeMisc$doAttack(ServerPlayerEntity serverPlayerEntity) {
		ItemStack stack = serverPlayerEntity.getMainHandStack();
		NbtCompound compound = stack.getOrCreateNbt();
		compound.putInt("action", MialeeMath.clampLoop(compound.getInt("action") + (serverPlayerEntity.isSneaking() ? -1 : 1), 0, Action.values().length));
	}

	public static Action getAction(ItemStack stack) {
		NbtCompound compound = stack.getOrCreateNbt();
		return Action.values()[MialeeMath.clampLoop(compound.getInt("action"), 0, Action.values().length)];
	}

	public enum Action {
		HARVEST,
		COLLECT,
		SKIRMISH,
		LOVE
	}
}
