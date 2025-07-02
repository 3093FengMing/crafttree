package me.fengming.crafttree.config;

import com.google.gson.*;
import me.fengming.crafttree.CraftTree;
import me.fengming.crafttree.graph.CraftNodeTree;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author FengMing
 */
public class CraftTreeConfig {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private static final List<CraftNodeTree> trees = new ArrayList<>();

    public static void load(Path path) {
        try {
            var json = JsonParser.parseReader(Files.newBufferedReader(path.resolve("config.json"), StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray array = json.getAsJsonArray("trees");
            for (JsonElement element : array) {
                Path treePath = path.resolve(element.getAsString());
                var tree = JsonParser.parseReader(Files.newBufferedReader(treePath, StandardCharsets.UTF_8)).getAsJsonObject();
                trees.add(CraftNodeTree.load(tree));
            }
        } catch (Exception e) {
            CraftTree.LOGGER.error("Error parsing config file", e);
        }
    }
}
