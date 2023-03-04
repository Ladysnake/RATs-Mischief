package doctor4t.ratsmischief.common.init;

import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.entity.RatEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEnchantments {
	Map<Enchantment, Identifier> ENCHANTMENTS = new LinkedHashMap<>();

	Enchantment RAT = createEntity("rat", new Enchantment() {
		@Override
		public Map<EquipmentSlot, ItemStack> getEquipment(LivingEntity entity) {
			return super.getEquipment(entity);
		}
	});

	private static Enchantment createEntity(String name, Enchantment entity) {
		ENCHANTMENTS.put(entity, new Identifier(RatsMischief.MOD_ID, name));
		return entity;
	}

	static void initialize() {
		ENCHANTMENTS.keySet().forEach(entityType -> Registry.register(Registry.ENTITY_TYPE, ENCHANTMENTS.get(entityType), entityType));
	}
}
