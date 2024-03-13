package me.melontini.dark_matter.impl.data.nbt;

import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.data.nbt.NbtBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.List;
import java.util.UUID;

public class NbtBuilderImpl implements NbtBuilder {

    private final NbtCompound nbt;

    public NbtBuilderImpl(NbtCompound nbt) {
        if (nbt == null) nbt = new NbtCompound();
        this.nbt = nbt;
    }

    public NbtBuilderImpl() {
        this.nbt = new NbtCompound();
    }

    public NbtBuilder put(String key, NbtElement element) {
        MakeSure.notNull(element, "Tried to put null NbtElement into NBT");
        nbt.put(key, element);
        return this;
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

    public NbtBuilder putUuid(String key, UUID value) {
        MakeSure.notNull(value, "Tried to put null UUID into NBT");
        nbt.putUuid(key, value);
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

    public NbtBuilder putString(String key, String value) {
        MakeSure.notNull(value, "Tried to put null String into NBT");
        nbt.putString(key, value);
        return this;
    }

    public NbtBuilder putByteArray(String key, byte[] value) {
        nbt.putByteArray(key, value);
        return this;
    }

    public NbtBuilder putByteArray(String key, List<Byte> value) {
        nbt.putByteArray(key, value);
        return this;
    }

    public NbtBuilder putIntArray(String key, int[] value) {
        nbt.putIntArray(key, value);
        return this;
    }

    public NbtBuilder putIntArray(String key, List<Integer> value) {
        nbt.putIntArray(key, value);
        return this;
    }

    public NbtBuilder putLongArray(String key, long[] value) {
        nbt.putLongArray(key, value);
        return this;
    }

    public NbtBuilder putLongArray(String key, List<Long> value) {
        nbt.putLongArray(key, value);
        return this;
    }

    public NbtBuilder putBoolean(String key, boolean value) {
        nbt.putBoolean(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "NbtBuilder{" +
                "nbt=" + nbt +
                '}';
    }

    public NbtCompound build() {
        return nbt;
    }
}
