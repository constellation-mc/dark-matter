package me.melontini.dark_matter.impl.data.loading;

import java.util.List;
import me.melontini.dark_matter.api.data.loading.DataPackContentsAccessor;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;

public interface InternalContentsAccessor extends DataPackContentsAccessor {
  void dark_matter$setReloaders(List<IdentifiableResourceReloadListener> reloaders);
}
