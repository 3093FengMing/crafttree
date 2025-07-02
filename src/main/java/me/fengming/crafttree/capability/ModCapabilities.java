package me.fengming.crafttree.capability;

import me.fengming.crafttree.network.ModMessages;
import me.fengming.crafttree.network.S2CSyncCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

/**
 * @author FengMing
 */
public class ModCapabilities {
    public static Capability<CraftTreeCapability> CRAFT_TREE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static CraftTreeCapability getCraftTree(Player player) {
        if (player == null) throw new IllegalArgumentException("player is null");
        return player.getCapability(CRAFT_TREE).orElse(new CraftTreeCapability());
    }

    public static void syncWithPlayer(Player player) {
        if (player == null) throw new IllegalArgumentException("player is null");
        CraftTreeCapability cap = getCraftTree(player);
        ModMessages.sendToPlayer(new S2CSyncCapability(cap.serializeNBT()), (ServerPlayer) player);
    }
}
