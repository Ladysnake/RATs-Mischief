package ladysnake.ratsmischief.common.init;

import com.mojang.serialization.Codec;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.util.RatData;
import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public interface ModDataComponents {
	Map<ComponentType<?>, Identifier> COMPONENT_TYPES = new LinkedHashMap<>();

	ComponentType<RatData> RAT_ENTITY_DATA = createComponentType("rat_entity_data", RatData.CODEC, RatData.PACKET_CODEC);

	private static <T> ComponentType<T> createComponentType(String name, Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
		ComponentType<T> componentType = ComponentType.<T>builder().codec(codec).packetCodec(packetCodec).build();
		COMPONENT_TYPES.put(componentType, RatsMischief.id(name));
		return componentType;
	}

	static void initialize() {
		COMPONENT_TYPES.keySet().forEach(item -> {
			Registry.register(Registries.DATA_COMPONENT_TYPE, COMPONENT_TYPES.get(item), item);
		});
		ComponentTooltipAppenderRegistry.addLast(RAT_ENTITY_DATA);
	}
}
