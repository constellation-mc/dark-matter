package me.melontini.dark_matter.api.minecraft.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

@SuppressWarnings("unused")
public class TextUtil {
    private TextUtil() {
        throw new UnsupportedOperationException();
    }
    public static MutableText translatable(String key) {
        return new TranslatableText(key);
    }

    public static MutableText translatable(String key, Object... args) {
        return new TranslatableText(key, args);
    }

    public static MutableText literal(String text) {
        return new LiteralText(text);
    }
}
