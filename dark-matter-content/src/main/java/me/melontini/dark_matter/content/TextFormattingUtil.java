package me.melontini.dark_matter.content;

import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class TextFormattingUtil {
    private TextFormattingUtil() {
        throw new UnsupportedOperationException();
    }
    /**
     * Registers a new color for an existing {@link Formatting} element.
     *
     * <p>
     * This method allows adding a color to an existing {@link Formatting} element at runtime.
     * In particular, if the {@link Formatting} enum is modified after the {@link TextColor} class has been initialized, the existing {@link Formatting} elements may not have their associated colors updated correctly.
     * This method can be used to register new colors for existing {@link Formatting} elements.
     * </p>
     *
     * @param formatting the {@link Formatting} element to add a color to
     * @return the newly created {@link TextColor} instance
     */
    public static @NotNull TextColor addTextColor(@NotNull Formatting formatting) {
        var tc = new TextColor(formatting.getColorValue(), formatting.getName());
        TextColor.FORMATTING_TO_COLOR.put(formatting, tc);
        TextColor.BY_NAME.put(tc.name, tc);
        return tc;
    }

    static {
        TextColor.FORMATTING_TO_COLOR = new HashMap<>(TextColor.FORMATTING_TO_COLOR);
        TextColor.BY_NAME = new HashMap<>(TextColor.BY_NAME);
    }
}
