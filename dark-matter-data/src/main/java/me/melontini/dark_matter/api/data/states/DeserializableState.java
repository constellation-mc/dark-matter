package me.melontini.dark_matter.api.data.states;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public interface DeserializableState {
  void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup);
}
