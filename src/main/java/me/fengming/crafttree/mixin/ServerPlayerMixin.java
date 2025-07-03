package me.fengming.crafttree.mixin;

import com.mojang.authlib.GameProfile;
import me.fengming.crafttree.config.CraftTreeConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author FengMing
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }

    @Unique
    ContainerListener crafttree$containerListener = new net.minecraft.world.inventory.ContainerListener() {
        @Override
        public void slotChanged(AbstractContainerMenu pContainerToSend, int pDataSlotIndex, ItemStack pStack) {
            Slot slot = pContainerToSend.getSlot(pDataSlotIndex);
            if (slot instanceof ResultSlot || slot.container != getInventory()) return;
            CraftTreeConfig.trigger(ServerPlayerMixin.this, pStack);
        }

        @Override
        public void dataChanged(AbstractContainerMenu pContainerMenu, int pDataSlotIndex, int pValue) {
        }
    };

    @Inject(method = "initMenu", at = @At("HEAD"))
    public void injectInitMenu(AbstractContainerMenu pMenu, CallbackInfo ci) {
        pMenu.addSlotListener(crafttree$containerListener);
    }
}
