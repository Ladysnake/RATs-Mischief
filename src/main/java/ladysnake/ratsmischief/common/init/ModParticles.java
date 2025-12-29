//package ladysnake.ratsmischief.common.init;
//
//import net.minecraft.particle.ParticleType;
//import net.minecraft.registry.Registries;
//import net.minecraft.registry.Registry;
//import net.minecraft.util.Identifier;
//
//import java.util.function.BiConsumer;
//
//public interface ModParticles {
////	ParticleWithTypeParticleType PARTICLE_WITH_TYPE = new ParticleWithTypeParticleType(true);
////	DefaultParticleType DEFAULT_PARTICLE = FabricParticleTypes.simple(true);
//
//	static void init() {
//		initParticles(bind(Registries.PARTICLE_TYPE));
//	}
//
//	static void registerFactories() {
////		ParticleFactoryRegistry.getInstance().register(PARTICLE_WITH_TYPE, ParticleWithTypeParticleType.Factory::new);
////		ParticleFactoryRegistry.getInstance().register(DEFAULT_PARTICLE, DefaultParticle.Factory::new);
//	}
//
//	private static void initParticles(BiConsumer<ParticleType<?>, Identifier> registry) {
////		registry.accept(PARTICLE_WITH_TYPE, new Identifier(Expedition.MOD_ID, "particle_with_type"));
////		registry.accept(DEFAULT_PARTICLE, new Identifier(Expedition.MOD_ID, "default_particle"));
//	}
//
//	private static <T> BiConsumer<T, Identifier> bind(Registry<? super T> registry) {
//		return (t, id) -> Registry.register(registry, id, t);
//	}
//}
//
