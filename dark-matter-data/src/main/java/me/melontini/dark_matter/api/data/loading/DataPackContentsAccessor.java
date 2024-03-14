package me.melontini.dark_matter.api.data.loading;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.util.Identifier;

public interface DataPackContentsAccessor {

    <T extends IdentifiableResourceReloadListener> T dm$getReloader(Identifier identifier);
}
