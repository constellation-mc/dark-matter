package me.melontini.dark_matter.minecraft.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class TextUtil {
    private TextUtil() {
        throw new UnsupportedOperationException();
    }
    public static MutableText translatable(String key) {
        return Text.translatable(key);
    }

    public static MutableText translatable(String key, Object... args) {
        return Text.translatable(key, args);
    }

    public static MutableText literal(String text) {
        return Text.literal(text);
    }
}
