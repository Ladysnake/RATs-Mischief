package ladysnake.ratsmischief.common.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.item.RatPouchItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KORatsScoreboardComponent implements ServerTickingComponent {
	public static final ComponentKey<KORatsScoreboardComponent> KEY = ComponentRegistry.getOrCreate(RatsMischief.id("korats"), KORatsScoreboardComponent.class);

	private final Scoreboard provider;
	private final MinecraftServer server;

	public KORatsScoreboardComponent(Scoreboard provider, @Nullable MinecraftServer server) {
		this.provider = provider;
		this.server = server;
	}

	private final Map<UUID, NbtList> koRats = new HashMap<>();

	public void koRatToPouchOrQueue(RatEntity rat) {
		UUID ownerUuid = rat.getOwnerUuid();
		if (rat.getWorld() instanceof ServerWorld serverWorld && ownerUuid != null) {
			// int owner rat list if empty
			if (!this.koRats.containsKey(ownerUuid)) {
				this.koRats.put(ownerUuid, new NbtList());
			}

			if (!RatPouchItem.storeRatInInventory(rat, (PlayerEntity) rat.getOwner(), true)) {
				NbtList ratList = this.koRats.get(ownerUuid);
				if (ratList == null) {
					ratList = new NbtList();
				}
				ratList.add(rat.getRatNbt(true));
				RatPouchItem.playRatStoreEffects(rat, serverWorld);
				rat.discard();
			}
		}
	}

	@Override
	public void readFromNbt(NbtCompound nbt) {
		this.koRats.clear();

		for (NbtElement nbtElement : nbt.getList("KORats", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound = ((NbtCompound) nbtElement);
			UUID ownerUUID = nbtCompound.getUuid("OwnerUUID");
			NbtList ratList = nbtCompound.getList("RatList", NbtElement.COMPOUND_TYPE);

			this.koRats.put(ownerUUID, ratList);
		}
	}

	@Override
	public void writeToNbt(NbtCompound nbt) {
		NbtList nbtList = new NbtList();
		for (UUID ownerUUID : this.koRats.keySet()) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putUuid("OwnerUUID", ownerUUID);
			nbtCompound.put("RatList", this.koRats.get(ownerUUID));
			nbtList.add(nbtCompound);
		}
		nbt.put("KORats", nbtList);
	}

	@Override
	public void serverTick() {
		for (ServerWorld world : server.getWorlds()) {
			if (world.getTime() % 20 == 0) {
				for (UUID ownerUUID : this.koRats.keySet()) {
					NbtList ratList = this.koRats.get(ownerUUID);
					if (ratList.isEmpty()) return;

					PlayerEntity player = world.getPlayerByUuid(ownerUUID);
					if (player != null) {
						for (int i = 0; i < ratList.size(); i++) {
							NbtCompound ratCompound = (NbtCompound) ratList.get(i);
							if (RatPouchItem.storeRatInInventory(ratCompound, player, true)) {
								ratList.set(i, new NbtCompound());
							}
						}

						ratList.removeIf(nbtElement -> ((NbtCompound) nbtElement).isEmpty());
					}
				}
			}
		}
	}
}
