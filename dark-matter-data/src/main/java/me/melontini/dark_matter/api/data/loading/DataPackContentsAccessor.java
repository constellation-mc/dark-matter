package me.melontini.dark_matter.api.data.loading;

import net.minecraft.resource.ResourceReloader;

public interface DataPackContentsAccessor {

    <T extends ResourceReloader> T dm$getReloader(ReloaderType<T> type);
}
