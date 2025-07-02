package me.fengming.crafttree.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author FengMing
 */
public class CraftTreeCapabilityProvider implements NonNullSupplier<CraftTreeCapability>, ICapabilitySerializable<CompoundTag> {
    private final CraftTreeCapability capability;

    public CraftTreeCapabilityProvider() {
        this.capability = new CraftTreeCapability();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.CRAFT_TREE ? LazyOptional.of(this).cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.capability.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.capability.deserializeNBT(nbt);
    }

    @Override
    public @NotNull CraftTreeCapability get() {
        return this.capability;
    }
}
