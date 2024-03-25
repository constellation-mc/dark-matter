package me.melontini.dark_matter.api.data.states;

import net.minecraft.nbt.NbtCompound;

public interface DeserializableState {
    void readNbt(NbtCompound nbt);
}
