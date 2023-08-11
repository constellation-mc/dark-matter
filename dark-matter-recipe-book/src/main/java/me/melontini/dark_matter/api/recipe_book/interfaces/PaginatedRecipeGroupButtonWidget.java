package me.melontini.dark_matter.api.recipe_book.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface PaginatedRecipeGroupButtonWidget {
    default int dm$getPage() {
        throw new IllegalStateException("Interface not implemented");
    }

    default void dm$setPage(int page) {
        throw new IllegalStateException("Interface not implemented");
    }

    @Deprecated
    default int getPage() {
        throw new IllegalStateException("Interface not implemented");
    }

    @Deprecated
    default void setPage(int page) {
        throw new IllegalStateException("Interface not implemented");
    }
}
