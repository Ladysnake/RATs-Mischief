package doctor4t.ratsmischief.common.init;

import doctor4t.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;

public interface ModDamageSources {
	static DamageSource ratDamage(RatEntity rat) {
		return new EntityDamageSource("rat", rat);
	}
}
