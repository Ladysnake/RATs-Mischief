package ladysnake.ratsmischief.common.item;

import net.minecraft.item.Item;

public class RatMasterMirrorItem extends Item {
	public RatMasterMirrorItem(Settings settings) {
		super(settings);
	}

	/*
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (!world.isClient()) {
			RatEntity rat = world.getClosestEntity(
				RatEntity.class,
				TargetPredicate.createAttackable().setPredicate(r -> ((RatEntity) r).isOwner(user) && ((RatEntity) r).isSpy()),
				user,
				user.getX(),
				user.getY(),
				user.getZ(),
				new Box(user.getBlockPos()).expand(160) // 16 chunks
			);
			if (rat != null) {
				RemnantComponent remnantComponent = RemnantComponent.get(user);
				remnantComponent.become(RatsMischiefRequiemPlugin.SPYING_RAT_REMNANT_TYPE);
				remnantComponent.splitPlayer(false)
					.ifPresent(res -> {
						res.soul().getItemCooldownManager().set(this, 40);
						res.soul().networkHandler.requestTeleport(rat.getX(), rat.getY(), rat.getZ(), rat.getYaw(), rat.getPitch());
						PossessionComponent.get(res.soul()).startPossessing(rat);
					});
			}
		}

		return TypedActionResult.success(user.getStackInHand(hand));
	}
	 */
}
