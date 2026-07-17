package ladysnake.ratsmischief.common.item;

import ladysnake.ratsmischief.common.init.ModSoundEvents;
import ladysnake.ratsmischief.mixin.PlayerInventoryAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.amymialee.mialeemisc.items.IClickConsumingItem;

import java.util.List;

public class RatCarnyxItem extends Item implements IClickConsumingItem {
	public RatCarnyxItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		return false;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.TOOT_HORN;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		List<DefaultedList<ItemStack>> combinedInventory = ((PlayerInventoryAccessor) user.getInventory()).ratsmischief$getCombinedInventory();
		int revivedCount = 0;
		for (List<ItemStack> list : combinedInventory) {
			for (ItemStack itemStack : list) {
				if (itemStack.getItem() instanceof RatPouchItem) {
					revivedCount += RatPouchItem.reviveKOs(itemStack);
				}
			}
		}

		if (!user.isCreative()) {
			user.getItemCooldownManager().set(this, revivedCount * 1 * 20); // 2 seconds of cooldown per rat revived
		}

		if (world instanceof ServerWorld serverWorld) {
			if (revivedCount > 0) {
				serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EVOKER_CAST_SPELL, user.getSoundCategory(), 0.5f, 1.5f);
				serverWorld.playSound(null, user.getX(), user.getY(), user.getZ(), ModSoundEvents.ITEM_CARNYX, user.getSoundCategory(), 2f, 1.5f);

			}
		}

		ItemStack stack = user.getStackInHand(hand);
		return revivedCount > 0 ? TypedActionResult.success(stack) : TypedActionResult.pass(stack);
	}

	@Override
	public void mialeeMisc$doAttack(ServerPlayerEntity serverPlayerEntity) {
		ServerWorld world = serverPlayerEntity.getWorld();
		ItemStack stack = serverPlayerEntity.getStackInHand(serverPlayerEntity.getActiveHand());
		NbtCompound nbt = stack.getOrCreateNbt();

		final boolean gather;
		gather = !RatPouchItem.getClosestRatsInRadius(world, serverPlayerEntity, 20f).isEmpty() && RatPouchItem.getFreeSlotCount(serverPlayerEntity) > 0;

		ServerWorld serverWorld = (ServerWorld) serverPlayerEntity.world;
		boolean success = false;
		List<DefaultedList<ItemStack>> combinedInventory = ((PlayerInventoryAccessor) serverPlayerEntity.getInventory()).ratsmischief$getCombinedInventory();
		for (List<ItemStack> list : combinedInventory) {
			for (ItemStack itemStack : list) {
				if (itemStack.getItem() instanceof RatPouchItem) {
					TypedActionResult<ItemStack> itemStackTypedActionResult = RatPouchItem.usePouch(world, serverPlayerEntity, itemStack, gather ? RatPouchItem.PouchActionOverride.GATHER : RatPouchItem.PouchActionOverride.RELEASE);
					if (itemStackTypedActionResult.getResult() == ActionResult.SUCCESS) {
						success = true;
					}
				}
			}
		}
		if (!gather && success) {
			serverWorld.playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.NEUTRAL, 1f, 1f);
		}

		if (success) {
			nbt.putBoolean("Gather", !gather);
			serverWorld.playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), ModSoundEvents.ITEM_CARNYX, serverPlayerEntity.getSoundCategory(), 2f, 1f + (gather ? .25f : 0f));
		}
	}
}
