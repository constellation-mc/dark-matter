package me.melontini.dark_matter.api.recipe_book.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
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

  default void dm$incrementPage() {
    this.dm$setPage(this.dm$getPage() + 1);
  }

  default void dm$decrementPage() {
    this.dm$setPage(this.dm$getPage() - 1);
  }

  default int dm$getPageCount() {
    throw new IllegalStateException("Interface not implemented");
  }
}
