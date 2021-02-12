package ladysnake.ratsrats.common.world;

import java.util.List;
import java.util.Random;

import ladysnake.ratsrats.common.Rats;
import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class RatSpawner implements Spawner {
    private int ticksUntilNextSpawn;

    public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
        if (spawnAnimals && world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
            --this.ticksUntilNextSpawn;
            if (this.ticksUntilNextSpawn > 0) {
                return 0;
            } else {
                this.ticksUntilNextSpawn = 1200;
                PlayerEntity playerEntity = world.getRandomAlivePlayer();
                if (playerEntity == null) {
                    return 0;
                } else {
                    Random random = world.random;
                    int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                    int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                    BlockPos blockPos = playerEntity.getBlockPos().add(i, 0, j);
                    if (!world.isRegionLoaded(blockPos.getX() - 10, blockPos.getY() - 10, blockPos.getZ() - 10, blockPos.getX() + 10, blockPos.getY() + 10, blockPos.getZ() + 10)) {
                        return 0;
                    } else {
                        if (SpawnHelper.canSpawn(SpawnRestriction.Location.ON_GROUND, world, blockPos, Rats.RAT)) {
                            return this.spawnInHouse(world, blockPos);
                        }

                        return 0;
                    }
                }
            }
        } else {
            return 0;
        }
    }

    private int spawnInHouse(ServerWorld world, BlockPos pos) {
        long bedCount = world.getPointOfInterestStorage().count(PointOfInterestType.HOME.getCompletionCondition(), pos, 48, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
        if (bedCount > 4L) {
            List<RatEntity> list = world.getNonSpectatingEntities(RatEntity.class, (new Box(pos)).expand(48.0D, 8.0D, 48.0D));
            if (list.size() < bedCount * 3) {
                return this.spawn(world, pos);
            }
        }

        return 0;
    }

    private int spawn(ServerWorld world, BlockPos pos) {
        RatEntity ratEntity = Rats.RAT.create(world);
        if (ratEntity == null) {
            return 0;
        } else {
            ratEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.NATURAL, (EntityData)null, (CompoundTag)null);
            ratEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
            world.spawnEntityAndPassengers(ratEntity);
            return 1;
        }
    }
}
