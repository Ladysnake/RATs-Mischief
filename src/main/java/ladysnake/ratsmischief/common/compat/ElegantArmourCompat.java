package ladysnake.ratsmischief.common.compat;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import xyz.amymialee.elegantarmour.util.IEleganttable;

public class ElegantArmourCompat {
	public static boolean shouldRenderArmour(EquipmentSlot slot, LivingEntity livingEntity) {
		if (livingEntity instanceof IEleganttable eleganttable) {
			return !eleganttable.isElegantPartEnabled(slot);
		}
		return true;
	}
}
