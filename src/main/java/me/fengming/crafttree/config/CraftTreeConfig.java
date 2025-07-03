package me.fengming.crafttree.config;

import com.google.gson.*;
import me.fengming.crafttree.CraftTree;
import me.fengming.crafttree.capability.ModCapabilities;
import me.fengming.crafttree.graph.CraftNode;
import me.fengming.crafttree.graph.CraftNodeTree;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author FengMing
 */
public class CraftTreeConfig {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final List<CraftNodeTree> trees = new ArrayList<>();
    private static final List<CraftNode> nodes = new ArrayList<>();
    private static final List<Consumer<CraftNode>> listeners = new ArrayList<>();

    public static void load(Path path) {
        try {
            var json = JsonParser.parseReader(Files.newBufferedReader(path.resolve("config.json"), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray array = json.getAsJsonArray("trees");
            for (JsonElement element : array) {
                Path treePath = path.resolve(element.getAsString() + ".json");
                var tree = JsonParser.parseReader(Files.newBufferedReader(treePath, StandardCharsets.UTF_8)).getAsJsonObject();
                trees.add(CraftNodeTree.load(tree));
            }
        } catch (Exception e) {
            CraftTree.LOGGER.error("Error parsing config file", e);
        }


        trees.stream()
                .map(CraftNodeTree::getNodes)
                .flatMap(Collection::stream)
                .forEach(nodes::add);
    }

    public static void trigger(Player player, ItemStack item) {
        var cap = ModCapabilities.getCraftTree(player);
        nodes.stream()
                .filter(e -> !cap.getUnlockedNodes().contains(e))
                .filter(e -> e.match(item))
                .forEach(cap::addUnlockedNode);
    }

    public static List<String> getIds() {
        return trees.stream()
                .map(CraftNodeTree::getNodes)
                .flatMap(e -> e.stream().map(CraftNode::getId))
                .toList();
    }

    public static CraftNode getNode(String id) {
        for (CraftNodeTree tree : trees) {
            CraftNode node = tree.getNode(id);
            if (node != null) return node;
        }
        return null;
    }
}
