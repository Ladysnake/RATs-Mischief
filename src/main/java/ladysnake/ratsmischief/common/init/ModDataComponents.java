package ladysnake.ratsmischief.common.init;

import com.mojang.serialization.Codec;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.RatMasterMaskItem;
import ladysnake.ratsmischief.common.item.RatMasterOcarinaItem;
import ladysnake.ratsmischief.common.item.RatPouchItem;
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
	ComponentType<RatMasterOcarinaItem.Action> OCARINA_ACTION = createComponentType("ocarina_action", RatMasterOcarinaItem.Action.CODEC, RatMasterOcarinaItem.Action.PACKET_CODEC);
	ComponentType<RatPouchItem.StoredRats> STORED_RATS = createComponentType("stored_rats", RatPouchItem.StoredRats.CODEC, RatPouchItem.StoredRats.PACKET_CODEC);
	ComponentType<Integer> RAT_POUCH_CAPACITY = createComponentType("rat_pouch_capacity", Codec.INT, PacketCodecs.INTEGER);
	ComponentType<Boolean> RAT_POUCH_FILLED = createComponentType("rat_pouch_filled", Codec.BOOL, PacketCodecs.BOOLEAN);
	ComponentType<RatMasterMaskItem.Offset> RAT_MASTER_MASK_OFFSET = createComponentType("rat_master_mask_offset", RatMasterMaskItem.Offset.CODEC, RatMasterMaskItem.Offset.PACKET_CODEC);

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
		ComponentTooltipAppenderRegistry.addLast(OCARINA_ACTION);
		ComponentTooltipAppenderRegistry.addLast(STORED_RATS);
		ComponentTooltipAppenderRegistry.addLast(RAT_MASTER_MASK_OFFSET);

	}
}
