package me.melontini.dark_matter.api.data.states;

import lombok.experimental.UtilityClass;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
@ApiStatus.Experimental
public final class PersistentStateHelper {

    public static <T extends PersistentState> T getOrCreate(ServerWorld world, Function<NbtCompound, T> readFunction, Supplier<T> supplier, String id) {
        return world.getPersistentStateManager().getOrCreate(new PersistentState.Type<>(supplier, readFunction, null), id);
    }

    public static <T extends PersistentState & DeserializableState> T getOrCreate(ServerWorld world, Supplier<T> supplier, String id) {
        return getOrCreate(world, nbt -> {
            T state = supplier.get();
            state.readNbt(nbt);
            return state;
        }, supplier, id);
    }

    public static boolean isStateLoaded(ServerWorld world, String id) {
        return world.getPersistentStateManager().loadedStates.containsKey(id);
    }

    public static <T extends PersistentState> void consumeIfLoaded(ServerWorld world, String id, BiFunction<ServerWorld, String, T> getFunc, Consumer<T> action) {
        if (isStateLoaded(world, id)) {
            action.accept(getFunc.apply(world, id));
        }
    }

    public static <T extends PersistentState, R> Optional<R> processIfLoaded(ServerWorld world, String id, BiFunction<ServerWorld, String, T> getFunc, Function<T, R> action) {
        if (isStateLoaded(world, id)) {
            return Optional.ofNullable(action.apply(getFunc.apply(world, id)));
        }
        return Optional.empty();
    }
}
