package me.melontini.dark_matter.recipe_book.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
@ApiStatus.Experimental
public interface PaginatedRecipeBookWidget {
    default void updatePages() {
        throw new IllegalStateException("Interface not implemented");
    }

    default void updatePageSwitchButtons() {
        throw new IllegalStateException("Interface not implemented");
    }

    default int getPage() {
        throw new IllegalStateException("Interface not implemented");
    }

    default void setPage(int page) {
        throw new IllegalStateException("Interface not implemented");
    }

    default int getPageCount() {
        throw new IllegalStateException("Interface not implemented");
    }
}
