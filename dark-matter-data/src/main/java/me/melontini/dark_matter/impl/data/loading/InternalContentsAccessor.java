package me.melontini.dark_matter.impl.data.loading;

import me.melontini.dark_matter.api.data.loading.DataPackContentsAccessor;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;

import java.util.List;

public interface InternalContentsAccessor extends DataPackContentsAccessor {
    void dark_matter$setReloaders(List<IdentifiableResourceReloadListener> reloaders);
}
