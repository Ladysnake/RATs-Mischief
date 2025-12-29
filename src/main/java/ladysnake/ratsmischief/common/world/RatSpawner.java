package ladysnake.ratsmischief.common.world;

import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.minecraft.world.spawner.Spawner;

import java.util.List;

public class RatSpawner implements Spawner {
	public static final int SPAWN_RADIUS = 100;
	private int ticksUntilNextSpawn;


	public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
		if (spawnAnimals && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
			--this.ticksUntilNextSpawn;
			if (this.ticksUntilNextSpawn <= 0) {
				this.ticksUntilNextSpawn = 100;
				Random random = world.random;
				world.getPlayers().forEach(serverPlayerEntity -> {
					int x = (8 + random.nextInt(32)) * (random.nextBoolean() ? -1 : 1);
					int y = (random.nextInt(4)) * (random.nextBoolean() ? -1 : 1);
					int z = (8 + random.nextInt(32)) * (random.nextBoolean() ? -1 : 1);
					BlockPos blockPos = serverPlayerEntity.getBlockPos().add(x, y, z);

					// test early if the rat can spawn
					if (RatEntity.canMobSpawn(ModEntities.RAT, world, SpawnReason.NATURAL, blockPos, world.getRandom())) {
						BlockPos villagePos = world.locateStructure(StructureTags.VILLAGE, blockPos, 5, false);
						// if a village was found and it's close enough
						if (villagePos != null && blockPos.getManhattanDistance(villagePos) <= 300) {
							List<VillagerEntity> villagersNearby = world.getEntitiesByType(EntityType.VILLAGER, new Box(blockPos.getX() - SPAWN_RADIUS, blockPos.getY() - SPAWN_RADIUS, blockPos.getZ() - SPAWN_RADIUS, blockPos.getX() + SPAWN_RADIUS, blockPos.getY() + SPAWN_RADIUS, blockPos.getZ() + SPAWN_RADIUS), villagerEntity -> true);

							if (villagersNearby.isEmpty() && world.isRegionLoaded(blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10)) {
								if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos, ModEntities.RAT)) {
									for (int i = 0; i <= random.nextInt(5); i++) {
										this.spawnInHouse(world, blockPos);
									}
								}
							}
						}
					}
				});
			}
		}

		return 0;
	}

	private int spawnInHouse(ServerWorld world, BlockPos pos) {
		if (world.getPointOfInterestStorage().count((registryEntry) -> registryEntry.matchesKey(PointOfInterestTypes.HOME), pos, 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE) > 4L) {
			List<RatEntity> list = world.getNonSpectatingEntities(RatEntity.class, (new Box(pos)).expand(48.0D, 8.0D, 48.0D));
			if (list.size() < 10) {
				return this.spawn(pos, world);
			}
		}

		return 0;
	}

	private int spawn(BlockPos pos, ServerWorld world) {
		RatEntity catEntity = ModEntities.RAT.create(world);
		if (catEntity == null) {
			return 0;
		} else {
			catEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.NATURAL, null, null);
			catEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
			world.spawnEntityAndPassengers(catEntity);
			return 1;
		}
	}
}

