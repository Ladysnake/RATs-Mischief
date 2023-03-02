package doctor4t.ratsmischief.common;

import doctor4t.ratsmischief.common.init.ModBlocks;
import doctor4t.ratsmischief.common.init.ModEntities;
import doctor4t.ratsmischief.common.init.ModItems;
import doctor4t.ratsmischief.common.init.ModSoundEvents;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import software.bernie.geckolib3.GeckoLib;

public class RatsMischief implements ModInitializer {
	public static final String MOD_ID = "ratsmischief";

	@Override
	public void onInitialize(ModContainer mod) {
		GeckoLib.initialize();

		// initializing stuff
		ModEntities.initialize();
		ModBlocks.initialize();
//		ModItemGroup.initialize();
		ModItems.initialize();
		ModSoundEvents.initialize();

		// rat kid painting
		Registry.register(Registry.PAINTING_VARIANT, new Identifier(MOD_ID, "a_rat_in_time"), new PaintingVariant(64, 48));
	}
}
