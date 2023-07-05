package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModSoundEvents {
	Map<SoundEvent, Identifier> SOUND_EVENTS = new LinkedHashMap<>();

	//	final SoundEvent SOUND_EVENT = createSoundEvent("sound.event.name");
	SoundEvent ENTITY_RAT_HURT = createSoundEvent("entity.rat.hurt");
	SoundEvent ENTITY_RAT_DEATH = createSoundEvent("entity.rat.death");
	SoundEvent ENTITY_RAT_CLAP = createSoundEvent("entity.rat.clap");
	SoundEvent ITEM_RAT_TOGGLE = createSoundEvent("item.rat.toggle");

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
