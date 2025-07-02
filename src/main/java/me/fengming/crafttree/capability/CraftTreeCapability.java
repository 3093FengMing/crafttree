package me.fengming.crafttree.capability;

import me.fengming.crafttree.config.CraftTreeConfig;
import me.fengming.crafttree.graph.CraftNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author FengMing
 */
public class CraftTreeCapability implements INBTSerializable<CompoundTag> {
    private final Set<CraftNode> trackingNodes = ConcurrentHashMap.newKeySet();
    private final Set<CraftNode> unlockedNodes = ConcurrentHashMap.newKeySet();

    public Set<CraftNode> getUnlockedNodes() {
        return this.unlockedNodes;
    }

    public Set<CraftNode> getTrackingNodes() {
        return this.trackingNodes;
    }

    public void addTrackingNode(CraftNode node) {
        this.trackingNodes.add(node);
    }

    public void addUnlockedNode(CraftNode node) {
        this.unlockedNodes.add(node);
    }

    public void removeTrackingNode(CraftNode node) {
        this.trackingNodes.remove(node);
    }

    public void removeUnlockedNode(CraftNode node) {
        this.unlockedNodes.remove(node);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag tracking = new ListTag();
        for (CraftNode node : trackingNodes) {
            tracking.add(StringTag.valueOf(node.getId()));
        }
        tag.put("tracking", tracking);

        ListTag unlocked = new ListTag();
        for (CraftNode node : unlockedNodes) {
            unlocked.add(StringTag.valueOf(node.getId()));
        }
        tag.put("unlocked", unlocked);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        trackingNodes.clear();
        ListTag trackingListTag = tag.getList("tracking", Tag.TAG_STRING);
        for (int i = 0, nodesSize = trackingListTag.size(); i < nodesSize; i++) {
            String nodeId = trackingListTag.getString(i);
            trackingNodes.add(CraftTreeConfig.getNode(nodeId));
        }

        unlockedNodes.clear();
        ListTag unlockedListTag = tag.getList("unlocked", Tag.TAG_STRING);
        for (int i = 0, nodesSize = unlockedListTag.size(); i < nodesSize; i++) {
            String nodeId = unlockedListTag.getString(i);
            unlockedNodes.add(CraftTreeConfig.getNode(nodeId));
        }
    }
}
