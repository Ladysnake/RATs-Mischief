package ladysnake.ratsmischief.common.compat;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import org.quiltmc.loader.api.QuiltLoader;

public class CompatManager {
	public static boolean shouldRenderArmour(EquipmentSlot slot, LivingEntity livingEntity) {
		if (QuiltLoader.isModLoaded("elegantarmour")) {
			return ElegantArmourCompat.shouldRenderArmour(slot, livingEntity);
		}
		return true;
	}
}
