package me.melontini.dark_matter.recipe_book.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.ApiStatus;

@Environment(EnvType.CLIENT)
@ApiStatus.Experimental
public interface PaginatedRecipeBookWidget {
    default void dm$updatePages() {
        throw new IllegalStateException("Interface not implemented");
    }

    default void dm$updatePageSwitchButtons() {
        throw new IllegalStateException("Interface not implemented");
    }

    default int dm$getPage() {
        throw new IllegalStateException("Interface not implemented");
    }

    default void dm$setPage(int page) {
        throw new IllegalStateException("Interface not implemented");
    }

    default int dm$getPageCount() {
        throw new IllegalStateException("Interface not implemented");
    }

    @Deprecated
    default void updatePages() {
        dm$updatePages();
    }

    @Deprecated
    default void updatePageSwitchButtons() {
        dm$updatePageSwitchButtons();
    }

    @Deprecated
    default int getPage() {
        return dm$getPage();
    }

    @Deprecated
    default void setPage(int page) {
        dm$setPage(page);
    }

    @Deprecated
    default int getPageCount() {
        return dm$getPageCount();
    }
}
