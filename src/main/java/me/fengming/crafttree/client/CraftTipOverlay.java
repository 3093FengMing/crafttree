package me.fengming.crafttree.client;

import me.fengming.crafttree.capability.CraftTreeCapability;
import me.fengming.crafttree.capability.ModCapabilities;
import me.fengming.crafttree.graph.CraftNode;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Set;

/**
 * @author FengMing
 */
public class CraftTipOverlay {
    public static final IGuiOverlay HUD_CRAFT_TIP = ((gui, graphics, partialTick, width, height) -> {
        Minecraft mc = gui.getMinecraft();
        int x = width - 100;
        int y = height / 2;

        CraftTreeCapability cap = ModCapabilities.getCraftTree(getPlayer(mc));
        Set<CraftNode> nodes = cap.getTrackingNodes();
        if (nodes.isEmpty()) return;
        CraftNode node = (CraftNode) nodes.toArray()[0];

        var name = node.getName();
        // graphics.renderItem(new ItemStack(Items.APPLE), 200, 200);
        graphics.drawString(mc.font, name, x - mc.font.width(name), y, 0xffffff);
    });

    private static Player getPlayer(Minecraft mc) {
        var p = mc.player;
        if (p == null) throw new IllegalStateException("Unexpected invoke");
        return p;
    }
}
