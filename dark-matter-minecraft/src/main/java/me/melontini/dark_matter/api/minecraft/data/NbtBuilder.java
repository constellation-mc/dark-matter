package me.melontini.dark_matter.api.minecraft.data;

import me.melontini.dark_matter.api.base.util.MakeSure;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * A quick builder to make NBTs in one line.
 *
 * <p>This class allows you to easily create and modify NBT compounds.
 * You can create a new {@link NbtBuilder} instance by calling the
 * {@link #create()} method, and then use its various put methods
 * to add elements to the NBT compound. You can also use the
 * {@link #create(NbtCompound)} method to start with an existing
 * NBT compound and modify it.
 *
 * <p>Once you are done adding elements to the NBT compound, you can
 * call the {@link #build()} method to obtain the resulting
 * {@link NbtCompound} instance.
 *
 * <p>Here's an example of how to use this class:
 *
 * <pre>
 * {@code
 * NbtCompound myNbt = NbtBuilder.create()
 *     .putInt("myInt", 42)
 *     .putString("myString", "Hello, world!")
 *     .build();
 * }
 * </pre>
 */
@SuppressWarnings("unused")
public class NbtBuilder {
    private final NbtCompound nbt;

    private NbtBuilder(NbtCompound nbt) {
        if (nbt == null) nbt = new NbtCompound();
        this.nbt = nbt;
    }

    private NbtBuilder() {
        this.nbt = new NbtCompound();
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull NbtBuilder create() {
        return new NbtBuilder();
    }

    @Contract("_ -> new")
    public static @NotNull NbtBuilder create(@Nullable NbtCompound nbt) {
        return new NbtBuilder(nbt);
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
