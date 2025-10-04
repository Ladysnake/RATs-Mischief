package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.mixin.object.builder.DefaultAttributeRegistryAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEntities {
	Map<EntityType<? extends Entity>, Identifier> ENTITIES = new LinkedHashMap<>();

	EntityType<RatEntity> RAT = createEntity("rat", EntityType.Builder.<RatEntity>create(RatEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 0.4F).maxTrackingRange(128).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, RatsMischief.id("rat"))));

	private static <T extends EntityType<? extends Entity>> T createEntity(String name, T entity) {
		ENTITIES.put(entity, RatsMischief.id(name));
		return entity;
	}

	static void initialize() {
		ENTITIES.keySet().forEach(entityType -> Registry.register(Registries.ENTITY_TYPE, ENTITIES.get(entityType), entityType));
		FabricDefaultAttributeRegistry.register(RAT, RatEntity.createRatAttributes());
	}
}
