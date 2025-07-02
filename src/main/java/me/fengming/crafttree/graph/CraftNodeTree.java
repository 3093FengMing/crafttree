package me.fengming.crafttree.graph;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * @author FengMing
 */
public class CraftNodeTree {
    private final Map<String, CraftNode> id2Node = Maps.newHashMap();

    private CraftNodeTree() {
    }

    public static CraftNodeTree load(JsonObject jsonObject) {
        CraftNodeTree tree = new CraftNodeTree();
        JsonArray nodes = jsonObject.getAsJsonArray("nodes");
        for (JsonElement element : nodes) {
            var node = CraftNode.load(tree, element.getAsJsonObject());
            if (node == null) continue;
            tree.id2Node.put(node.getId(), node);
        }
        return tree;
    }

    public CraftNode getNode(String id) {
        return id2Node.get(id);
    }

    public CraftNode getOrCreate(String id) {
        return id2Node.computeIfAbsent(id, k -> new CraftNode(this, k));
    }
}
