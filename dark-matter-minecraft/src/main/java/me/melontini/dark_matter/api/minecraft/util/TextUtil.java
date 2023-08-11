package me.melontini.dark_matter.api.minecraft.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

@SuppressWarnings("unused")
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