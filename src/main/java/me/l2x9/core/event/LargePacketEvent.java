package me.l2x9.core.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketDataSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public abstract class LargePacketEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = true;
    private final Player player;
    private Packet<?> packet;
    private final PacketDataSerializer buf;
    private final int length;

    protected LargePacketEvent(final Player player, final Packet<?> packet, final PacketDataSerializer buf, final int length) {
        this.player = player;
        this.packet = packet;
        this.buf = buf;
        this.length = length;
    }
    public Player getPlayer() {
        return this.player;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public void setPacket(final Packet<?> packet) {
        this.packet = packet;
    }
    public PacketDataSerializer getBuf() {
        return this.buf;
    }
    public int getLenght() {
        return this.length;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static class Incoming extends LargePacketEvent {
        public Incoming(Player player, Packet<?> packet, PacketDataSerializer buf, int length) {
            super(player, packet, buf, length);
        }
    }

    public static class Outgoing extends LargePacketEvent {

        public Outgoing(Player player, Packet<?> packet, PacketDataSerializer buf, int length) {
            super(player, packet, buf, length);
        }
    }
}
