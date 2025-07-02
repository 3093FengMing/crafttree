package me.fengming.crafttree;

import com.mojang.logging.LogUtils;
import me.fengming.crafttree.config.CraftTreeConfig;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
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
}
