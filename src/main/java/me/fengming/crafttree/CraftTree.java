package me.fengming.crafttree;

import com.mojang.logging.LogUtils;
import me.fengming.crafttree.capability.CraftTreeCapability;
import me.fengming.crafttree.capability.CraftTreeCapabilityProvider;
import me.fengming.crafttree.capability.ModCapabilities;
import me.fengming.crafttree.client.CraftTipOverlay;
import me.fengming.crafttree.command.CraftNodeArgument;
import me.fengming.crafttree.command.CraftTreeCommand;
import me.fengming.crafttree.config.CraftTreeConfig;
import me.fengming.crafttree.network.ModMessages;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

/**
 * @author FengMing
 */
@Mod(CraftTree.MODID)
public class CraftTree {
    public static final String MODID = "craft_tree";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, MODID);
    public static final RegistryObject<SingletonArgumentInfo<CraftNodeArgument>> CRAFT_NODE_ARGUMENT = COMMAND_ARGUMENT_TYPES.register("craft_node", () -> ArgumentTypeInfos.registerByClass(CraftNodeArgument.class, SingletonArgumentInfo.contextFree(CraftNodeArgument::create)));


    public CraftTree(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ITEMS.register(modEventBus);
        COMMAND_ARGUMENT_TYPES.register(modEventBus);

        CraftTreeConfig.load(FMLPaths.CONFIGDIR.get().resolve(MODID));
        modEventBus.addListener(CraftTree::commonSetup);
    }

    public static void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register();
        });
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModClientEvents {
        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("craft_tip", CraftTipOverlay.HUD_CRAFT_TIP);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModCommonEvents {
        @SubscribeEvent
        public static void registerCaps(RegisterCapabilitiesEvent event) {
            event.register(CraftTreeCapability.class);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeCommonEvents {
        @SubscribeEvent
        public static void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer sp) {
                ModCapabilities.syncWithPlayer(sp);
            }
        }

        @SubscribeEvent
        public static void registerCommand(RegisterCommandsEvent event) {
            CraftTreeCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void onAttachCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
            event.addCapability(ResourceLocation.tryBuild(MODID, "craft_tree"), new CraftTreeCapabilityProvider());
        }

        @SubscribeEvent
        public static void onPlayerCloned(PlayerEvent.Clone event) {
            // Inheritance capability
            event.getOriginal().reviveCaps();
            var oldCap = event.getOriginal().getCapability(ModCapabilities.CRAFT_TREE);
            var newCap = event.getEntity().getCapability(ModCapabilities.CRAFT_TREE);
            if (oldCap.isPresent() && newCap.isPresent()) {
                newCap.ifPresent((newCap1) -> oldCap.ifPresent((oldCap1) -> newCap1.deserializeNBT(oldCap1.serializeNBT())));
            }
            event.getOriginal().invalidateCaps();
        }
    }
}
