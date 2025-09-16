package ladysnake.ratsmischief.mialeemisc;

import io.netty.buffer.ByteBuf;
import ladysnake.ratsmischief.common.RatsMischief;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;


public class ClickConsumePayload implements CustomPayload {
	public static final CustomPayload.Id<ClickConsumePayload> ID = new Id<>(RatsMischief.id("click_consume"));
	public static final ClickConsumePayload INSTANCE = new ClickConsumePayload();
	public static final PacketCodec<ByteBuf, ClickConsumePayload> PACKET_CODEC = PacketCodec.unit(INSTANCE);
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
