package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEntities {
	Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();

	EntityType<RatEntity> RAT = createEntity("rat", FabricEntityTypeBuilder.<RatEntity>createMob().entityFactory(RatEntity::new).defaultAttributes(RatEntity::createRatAttributes).dimensions(EntityDimensions.changing(0.6F, 0.4F)).trackRangeChunks(128).build());

	private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
		ENTITIES.put(entity, RatsMischief.id(name));
		return entity;
	}

	static void initialize() {
		ENTITIES.keySet().forEach(entityType -> Registry.register(Registries.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
	}
}
