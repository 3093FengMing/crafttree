package me.fengming.crafttree.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.fengming.crafttree.capability.ModCapabilities;
import me.fengming.crafttree.config.CraftTreeConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * @author FengMing
 */
public class CraftTreeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var targets = Commands.argument("target", EntityArgument.entity());
        targets.then(Commands.literal("query")
                        .executes(ctx -> query(EntityArgument.getEntity(ctx, "target"))))
                .then(Commands.literal("addUnlock")
                        .then(Commands.argument("id", CraftNodeArgument.create())
                                .executes(ctx -> addUnlock(
                                        EntityArgument.getEntity(ctx, "target"),
                                        CraftNodeArgument.getQuestId(ctx, "id")
                                ))
                        )
                )
                .then(Commands.literal("removeUnlock")
                        .then(Commands.argument("id", CraftNodeArgument.create())
                                .executes(ctx -> removeUnlock(
                                        EntityArgument.getEntity(ctx, "target"),
                                        CraftNodeArgument.getQuestId(ctx, "id")
                                ))
                        )
                )
                .then(Commands.literal("addTrack")
                        .then(Commands.argument("id", CraftNodeArgument.create())
                                .executes(ctx -> addTrack(
                                        EntityArgument.getEntity(ctx, "target"),
                                        CraftNodeArgument.getQuestId(ctx, "id")
                                ))
                        )
                )
                .then(Commands.literal("removeTrack")
                        .then(Commands.argument("id", CraftNodeArgument.create())
                                .executes(ctx -> removeTrack(
                                        EntityArgument.getEntity(ctx, "target"),
                                        CraftNodeArgument.getQuestId(ctx, "id")
                                ))
                        )
                );

        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("crafttree")
                .requires(stack -> stack.hasPermission(2))
                .then(targets);

        dispatcher.register(command);
    }

    private static int query(Entity entity) {
        if (entity instanceof Player player) {
            var cap = ModCapabilities.getCraftTree(player);
            player.sendSystemMessage(Component.translatable("command.craft_tree.add_unlock.query.unlocked", cap.getUnlockedNodes().toString()));
            player.sendSystemMessage(Component.translatable("command.craft_tree.add_unlock.query.tracking", cap.getTrackingNodes().toString()));
            return 0;
        }
        return 1;
    }

    private static int addUnlock(Entity entity, String nodeId) {
        if (entity instanceof Player player) {
            var cap = ModCapabilities.getCraftTree(player);
            var node = CraftTreeConfig.getNode(nodeId);
            boolean bl = cap.addUnlockedNode(node);
            player.sendSystemMessage(Component.translatable("command.craft_tree.add_unlock." + (bl ? "success" : "failed"), entity.getDisplayName(), nodeId));
            ModCapabilities.syncWithPlayer(player);
            return 0;
        }
        return 1;
    }

    private static int removeUnlock(Entity entity, String nodeId) {
        if (entity instanceof Player player) {
            var cap = ModCapabilities.getCraftTree(player);
            var node = CraftTreeConfig.getNode(nodeId);
            boolean bl = cap.removeUnlockedNode(node);
            player.sendSystemMessage(Component.translatable("command.craft_tree.remove_unlock." + (bl ? "success" : "failed"), entity.getDisplayName(), nodeId));
            ModCapabilities.syncWithPlayer(player);
            return 0;
        }
        return 1;
    }

    private static int addTrack(Entity entity, String nodeId) {
        if (entity instanceof Player player) {
            var cap = ModCapabilities.getCraftTree(player);
            var node = CraftTreeConfig.getNode(nodeId);
            cap.addTrackingNode(node);
            ModCapabilities.syncWithPlayer(player);
            return 0;
        }
        return 1;
    }


    private static int removeTrack(Entity entity, String nodeId) {
        if (entity instanceof Player player) {
            var cap = ModCapabilities.getCraftTree(player);
            var node = CraftTreeConfig.getNode(nodeId);
            cap.removeTrackingNode(node);
            ModCapabilities.syncWithPlayer(player);
            return 0;
        }
        return 1;
    }
}
