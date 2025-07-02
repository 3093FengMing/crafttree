package me.fengming.crafttree;

import com.mojang.logging.LogUtils;
import me.fengming.crafttree.capability.CraftTreeCapability;
import me.fengming.crafttree.capability.CraftTreeCapabilityProvider;
import me.fengming.crafttree.capability.ModCapabilities;
import me.fengming.crafttree.client.CraftTipOverlay;
import me.fengming.crafttree.config.CraftTreeConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

/**
 * @author FengMing
 */
@Mod(CraftTree.MODID)
public class CraftTree {
    public static final String MODID = "craft_tree";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public CraftTree(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ITEMS.register(modEventBus);

        CraftTreeConfig.load(FMLPaths.CONFIGDIR.get().resolve("craft_tree"));
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("thirst", CraftTipOverlay.HUD_CRAFT_TIP);
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
        public static void onAttachCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(MODID, "concrete_count"), new CraftTreeCapabilityProvider());
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
