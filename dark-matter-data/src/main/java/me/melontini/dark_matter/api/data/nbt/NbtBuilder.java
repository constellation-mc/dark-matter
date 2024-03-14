package me.melontini.dark_matter.api.data.nbt;

import me.melontini.dark_matter.impl.data.nbt.NbtBuilderImpl;
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
public interface NbtBuilder {


    @Contract(value = " -> new", pure = true)
    static @NotNull NbtBuilder create() {
        return new NbtBuilderImpl();
    }

    @Contract("_ -> new")
    static @NotNull NbtBuilder create(@Nullable NbtCompound nbt) {
        return new NbtBuilderImpl(nbt);
    }

    NbtBuilder put(String key, NbtElement element);

    NbtBuilder putByte(String key, byte value);

    NbtBuilder putShort(String key, short value);

    NbtBuilder putInt(String key, int value);

    NbtBuilder putLong(String key, long value);

    NbtBuilder putUuid(String key, UUID value);

    NbtBuilder putFloat(String key, float value);

    NbtBuilder putDouble(String key, double value);

    NbtBuilder putString(String key, String value);

    NbtBuilder putByteArray(String key, byte[] value);

    NbtBuilder putByteArray(String key, List<Byte> value);

    NbtBuilder putIntArray(String key, int[] value);

    NbtBuilder putIntArray(String key, List<Integer> value);

    NbtBuilder putLongArray(String key, long[] value);

    NbtBuilder putLongArray(String key, List<Long> value);

    NbtBuilder putBoolean(String key, boolean value);

    NbtCompound build();
}
