package me.l2x9.core.patches.listeners.packetsize.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import me.l2x9.core.event.LargePacketEvent;
import me.l2x9.core.util.Utils;
import net.minecraft.server.v1_12_R1.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;

public class CustomPacketEncoder extends MessageToByteEncoder<Packet<?>> {
    private final Logger logger = LogManager.getLogger();
    private final Player player;

    public CustomPacketEncoder(Player player) {
        this.player = player;
    }

    @Override
    protected void encode(ChannelHandlerContext context, Packet<?> packet, ByteBuf buf) throws Exception {
        EnumProtocol protocol = context.channel().attr(NetworkManager.c).get();
        if (protocol == null) throw new RuntimeException("ConnectionProtocol unknown: " + packet.toString());
        Integer id = protocol.a(EnumProtocolDirection.CLIENTBOUND, packet);
        if (id == null) throw new IOException("Can't serialize unregistered packet");
        PacketDataSerializer dataSerializer = new PacketDataSerializer(buf);
        dataSerializer.d(id);
        try {
            packet.b(dataSerializer);
            int len = dataSerializer.readableBytes();
            if (len >= 2097152) {
                String longPacketFormat = "&aPrevented a large&r&3 %s &r&apacket with length of&r&3 %d/%d &r&afrom being sent to player&r&3 %s&r&a near &r&a%s&r";
                Utils.log(String.format(longPacketFormat, packet.getClass().getSimpleName(), len, 2097152, player.getName(), Utils.formatLocation(player.getLocation())));
                LargePacketEvent.Outgoing outgoing = new LargePacketEvent.Outgoing(player, packet, dataSerializer, len);
                Bukkit.getServer().getPluginManager().callEvent(outgoing);
                if (outgoing.isCancelled()) dataSerializer.clear();
            }
        } catch (Throwable throwable) {
            logger.error(throwable);
        }
    }
}

