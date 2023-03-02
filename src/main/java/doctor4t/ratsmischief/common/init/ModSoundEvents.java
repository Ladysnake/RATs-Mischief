package doctor4t.ratsmischief.common.init;

import doctor4t.ratsmischief.common.RatsMischief;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModSoundEvents {
	Map<SoundEvent, Identifier> SOUND_EVENTS = new LinkedHashMap<>();

	//	public static final SoundEvent SOUND_EVENT = createSoundEvent("sound.event.name");
	public static SoundEvent ENTITY_RAT_HURT = createSoundEvent("entity.rat.hurt");
	public static SoundEvent ENTITY_RAT_DEATH = createSoundEvent("entity.rat.death");

	static void initialize() {
		SOUND_EVENTS.keySet().forEach(soundEvent -> {
			Registry.register(Registry.SOUND_EVENT, SOUND_EVENTS.get(soundEvent), soundEvent);
		});
	}

	private static SoundEvent createSoundEvent(String path) {
		SoundEvent soundEvent = new SoundEvent(new Identifier(RatsMischief.MOD_ID, path));
		SOUND_EVENTS.put(soundEvent, new Identifier(RatsMischief.MOD_ID, path));
		return soundEvent;
	}

}
