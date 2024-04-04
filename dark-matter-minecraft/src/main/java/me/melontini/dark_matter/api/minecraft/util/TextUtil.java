package me.melontini.dark_matter.api.minecraft.util;

import lombok.experimental.UtilityClass;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
@SuppressWarnings("unused")
public class TextUtil {

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull MutableText translatable(String key) {
        return Text.translatable(key);
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull MutableText translatable(String key, Object... args) {
        return Text.translatable(key, args);
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull MutableText literal(String text) {
        return Text.literal(text);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull MutableText empty() {
        return Text.empty();
    }
}
