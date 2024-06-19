package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public interface ModDamageTypes {
	RegistryKey<DamageType> RAT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, RatsMischief.id("rat"));

	 static DamageSource ratDamage(RatEntity rat) {
		return new DamageSource(rat.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).getHolderOrThrow(RAT), rat);
	}
}
