package ladysnake.ratsmischief.common.world;

import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.List;
import java.util.Random;

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
                    if (RatEntity.canMobSpawn(Mischief.RAT, world, SpawnReason.NATURAL, blockPos, world.getRandom())) {
                        BlockPos villagePos = world.locateStructure(StructureFeature.VILLAGE, blockPos, 5, false);
                        // if a village was found and it's close enough
                        if (villagePos != null && blockPos.getManhattanDistance(villagePos) <= 300) {
                            List<VillagerEntity> villagersNearby = world.getEntitiesByType(EntityType.VILLAGER, new Box(blockPos.getX() - SPAWN_RADIUS, blockPos.getY() - SPAWN_RADIUS, blockPos.getZ() - SPAWN_RADIUS, blockPos.getX() + SPAWN_RADIUS, blockPos.getY() + SPAWN_RADIUS, blockPos.getZ() + SPAWN_RADIUS), villagerEntity -> true);

                            if (villagersNearby.isEmpty() && world.isRegionLoaded(blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10)) {
                                if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos, Mischief.RAT)) {
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

    private void spawnInHouse(ServerWorld world, BlockPos pos) {
        long bedCount = world.getPointOfInterestStorage().count(PointOfInterestType.HOME.getCompletionCondition(), pos, 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
        List<RatEntity> list = world.getNonSpectatingEntities(RatEntity.class, (new Box(pos)).expand(96.0, 16.0D, 96.0D));

        if (list.size() < bedCount * 3 && list.size() < 20) {
            this.spawn(world, pos);
        }
    }

    private void spawn(ServerWorld world, BlockPos pos) {
        RatEntity ratEntity = Mischief.RAT.create(world);
        if (ratEntity != null) {
            ratEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.NATURAL, (EntityData) null, (NbtCompound) null);
            ratEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
            world.spawnEntityAndPassengers(ratEntity);
        }
    }
}
