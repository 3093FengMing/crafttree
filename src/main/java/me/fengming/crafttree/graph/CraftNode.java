package me.fengming.crafttree.graph;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import me.fengming.crafttree.CraftTree;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author FengMing
 */
public class CraftNode {
    private final CraftNodeTree tree;
    private final String id;

    private CraftNode parent = null;
    private List<CraftNode> children = new ArrayList<>();
    private Component name;
    private Component description;
    private ItemStack icon;

    public CraftNode(CraftNodeTree tree, String id) {
        this.tree = tree;
        this.id = id;
    }

    public static CraftNode load(CraftNodeTree tree, JsonObject jsonObject) {
        JsonElement id = jsonObject.get("id");
        if (id == null) return null;
        CraftNode node = new CraftNode(tree, id.getAsString());
        JsonElement parentId = jsonObject.get("parent");
        if (parentId == null) {
            node.setParent(null);
        } else {
            node.setParent(tree.getOrCreate(parentId.getAsString()));
        }
        JsonElement childrenId = jsonObject.get("children");
        if (childrenId == null) {
            node.setChildren(List.of());
        } else {
            for (JsonElement element : childrenId.getAsJsonArray()) {
                node.addChild(tree.getOrCreate(element.getAsString()));
            }
        }
        JsonElement name = jsonObject.get("name");
        if (name == null) {
            node.setName(Component.empty());
        } else {
            node.setName(Component.Serializer.fromJson(name));
        }
        JsonElement description = jsonObject.get("description");
        if (description == null) {
            node.setDescription(Component.empty());
        } else {
            node.setDescription(Component.Serializer.fromJson(description));
        }
        JsonElement icon = jsonObject.get("icon");
        if (icon == null) {
            node.setIcon(ItemStack.EMPTY);
        } else {
            node.setIcon(ItemStack.CODEC.parse(JsonOps.INSTANCE, icon).getOrThrow(false, CraftTree.LOGGER::error));
        }
        return node;
    }

    public String getId() {
        return this.id;
    }

    public void setName(Component name) {
        this.name = name;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setDescription(Component description) {
        this.description = description;
    }

    public void setParent(CraftNode parent) {
        this.parent = parent;
    }

    public void setChildren(List<CraftNode> children) {
        this.children = children;
    }

    public void addChild(CraftNode child) {
        this.children.add(child);
    }

    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CraftNode craftNode = (CraftNode) object;
        return Objects.equals(id, craftNode.id) && Objects.equals(parent, craftNode.parent) && Objects.equals(children, craftNode.children) && Objects.equals(name, craftNode.name) && Objects.equals(description, craftNode.description) && Objects.equals(icon, craftNode.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), parent, children, name, description, icon);
    }
}
