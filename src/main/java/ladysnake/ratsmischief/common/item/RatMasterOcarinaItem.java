package ladysnake.ratsmischief.common.item;

import com.mojang.serialization.Codec;
import ladysnake.ratsmischief.client.RatsMischiefClientHelper;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.entity.ai.BreedGoal;
import ladysnake.ratsmischief.common.entity.ai.DigGoal;
import ladysnake.ratsmischief.common.entity.ai.HarvestPlantMealGoal;
import ladysnake.ratsmischief.common.init.ModDataComponents;
import ladysnake.ratsmischief.mialeemisc.items.IClickConsumingItem;
import ladysnake.ratsmischief.mialeemisc.util.MialeeMath;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RatMasterOcarinaItem extends Item implements IClickConsumingItem {
	public RatMasterOcarinaItem(Settings settings) {
		super(settings);
	}

	public static Action getAction(ItemStack stack) {
		return stack.get(ModDataComponents.OCARINA_ACTION);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		final List<RatEntity> ratEntityList = world.getEntitiesByClass(RatEntity.class, user.getBoundingBox().expand(16f), ratEntity -> ratEntity.isTamed() && ratEntity.getOwner() != null && ratEntity.getOwner().equals(user));
		ratEntityList.forEach(ratEntity -> {
			Goal goal = null;
			switch (getAction(user.getStackInHand(hand))) {
				case HARVEST -> goal = new HarvestPlantMealGoal(ratEntity);
				case COLLECT -> ratEntity.removeCurrentActionGoal();
				case SKIRMISH ->
					goal = new ActiveTargetGoal<>(ratEntity, HostileEntity.class, 10, true, false, (target, world1) -> true);
				case LOVE -> goal = new BreedGoal(ratEntity);
			}
			if (goal != null) {
				ratEntity.setAction(goal);
			}
		});
		ItemStack stack = user.getStackInHand(hand);
		world.playSoundFromEntity(null, user, SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(), user.getSoundCategory(), 1f, 0.5f + (stack.get(ModDataComponents.OCARINA_ACTION).getIndex()));
		return ActionResult.SUCCESS;
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
	public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
		return false;
	}

	@Override
	public void mialeeMisc$doAttack(ServerPlayerEntity serverPlayerEntity) {
		ItemStack stack = serverPlayerEntity.getMainHandStack();
		stack.set(ModDataComponents.OCARINA_ACTION, Action.byIndex(MialeeMath.clampLoop(stack.get(ModDataComponents.OCARINA_ACTION).getIndex() + (serverPlayerEntity.isSneaking() ? -1 : 1), 0, Action.values().length)));
	}

	@Override
	public Text getName(ItemStack stack) {
		return super.getName(stack).copy().append(" (" + getAction(stack).name() + ")");
	}


	public enum Action implements TooltipAppender {
		HARVEST(0),
		COLLECT(1),
		SKIRMISH(2),
		LOVE(3);
		public static Codec<Action> CODEC = Codec.intRange(0, 3).xmap(Action::byIndex, Action::getIndex);
		public static PacketCodec<PacketByteBuf, Action> PACKET_CODEC = PacketCodec.ofStatic(PacketByteBuf::writeEnumConstant, buf -> buf.readEnumConstant(Action.class));
		Action(int index){
			this.index = index;
		}
		private int index;

		public int getIndex() {
			return index;
		}

		public static Action byIndex(int index){
			return Arrays.stream(values()).filter(action -> action.getIndex() == index).findAny().orElse(Action.HARVEST);
		}

		@Override
		public void appendTooltip(TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
			textConsumer.accept(Text.translatable("item.ratsmischief.rat_master_ocarina.tooltip").formatted(Formatting.GRAY));
			textConsumer.accept(Text.translatable("item.ratsmischief.rat_master_ocarina.%s.tooltip".formatted(this.name().toLowerCase())).formatted(Formatting.GRAY));
			textConsumer.accept(Text.translatable("item.ratsmischief.rat_master_ocarina.bring_items.%s".formatted(RatsMischiefClientHelper.shouldRatsBringItems())).formatted(Formatting.GRAY));
		}
	}
}
