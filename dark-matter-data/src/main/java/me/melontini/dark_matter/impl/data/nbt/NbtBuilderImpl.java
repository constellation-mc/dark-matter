package me.melontini.dark_matter.impl.data.nbt;

import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import me.melontini.dark_matter.api.data.nbt.NbtBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class NbtBuilderImpl implements NbtBuilder {

  private final NbtCompound nbt;

  public NbtBuilderImpl(NbtCompound nbt) {
    if (nbt == null) nbt = new NbtCompound();
    this.nbt = nbt;
  }

  public NbtBuilderImpl() {
    this.nbt = new NbtCompound();
  }

  public NbtBuilder put(String key, @NonNull NbtElement element) {
    nbt.put(key, element);
    return this;
  }

  @Override
  public NbtBuilder put(String key, @NonNull NbtBuilder builder) {
    nbt.put(key, builder.build());
    return null;
  }

  public NbtBuilder putByte(String key, byte value) {
    nbt.putByte(key, value);
    return this;
  }

  public NbtBuilder putShort(String key, short value) {
    nbt.putShort(key, value);
    return this;
  }

  public NbtBuilder putInt(String key, int value) {
    nbt.putInt(key, value);
    return this;
  }

  public NbtBuilder putLong(String key, long value) {
    nbt.putLong(key, value);
    return this;
  }

  public NbtBuilder putUuid(String key, @NonNull UUID uuid) {
    nbt.putUuid(key, uuid);
    return this;
  }

  public NbtBuilder putFloat(String key, float value) {
    nbt.putFloat(key, value);
    return this;
  }

  public NbtBuilder putDouble(String key, double value) {
    nbt.putDouble(key, value);
    return this;
  }

  public NbtBuilder putString(String key, @NonNull String string) {
    nbt.putString(key, string);
    return this;
  }

  public NbtBuilder putByteArray(String key, byte @NonNull [] bytes) {
    nbt.putByteArray(key, bytes);
    return this;
  }

  public NbtBuilder putByteArray(String key, @NonNull List<Byte> bytes) {
    nbt.putByteArray(key, bytes);
    return this;
  }

  public NbtBuilder putIntArray(String key, int @NonNull [] ints) {
    nbt.putIntArray(key, ints);
    return this;
  }

  public NbtBuilder putIntArray(String key, @NonNull List<Integer> ints) {
    nbt.putIntArray(key, ints);
    return this;
  }

  public NbtBuilder putLongArray(String key, long @NonNull [] longs) {
    nbt.putLongArray(key, longs);
    return this;
  }

  public NbtBuilder putLongArray(String key, @NonNull List<Long> longs) {
    nbt.putLongArray(key, longs);
    return this;
  }

  public NbtBuilder putBoolean(String key, boolean value) {
    nbt.putBoolean(key, value);
    return this;
  }

  @Override
  public String toString() {
    return "NbtBuilder{" + "nbt=" + nbt + '}';
  }

  public NbtCompound build() {
    return nbt;
  }
}
