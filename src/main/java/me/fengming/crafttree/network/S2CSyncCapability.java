package me.fengming.crafttree.network;

import me.fengming.crafttree.capability.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author FengMing
 */
public class S2CSyncCapability {
    private final CompoundTag tag;

    public S2CSyncCapability(CompoundTag tag) {
        this.tag = tag;
    }

    public S2CSyncCapability(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        var context = supplier.get();
        context.enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            var data = ModCapabilities.getCraftTree(player);
            data.deserializeNBT(tag);
        });
        return true;
    }
}
