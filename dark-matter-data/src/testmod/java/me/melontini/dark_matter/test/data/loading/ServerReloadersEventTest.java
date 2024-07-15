package me.melontini.dark_matter.test.data.loading;

import me.melontini.dark_matter.api.data.loading.ReloaderType;
import me.melontini.dark_matter.api.data.loading.ServerReloadersEvent;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ServerReloadersEventTest implements ModInitializer {

    public static final ReloaderType<TestReloader> TYPE = ReloaderType.create(new Identifier("dark-matter-data", "test-reloader"));

    @Override
    public void onInitialize() {
        ServerReloadersEvent.EVENT.register(context -> context.register(new TestReloader(context)));
    }

    public static class TestReloader implements SimpleSynchronousResourceReloadListener {

        private final ServerReloadersEvent.Context context;

        public TestReloader(ServerReloadersEvent.Context context) {
            this.context = context;
        }

        @Override
        public Identifier getFabricId() {
            return TYPE.identifier();
        }

        @Override
        public void reload(ResourceManager manager) {
            DarkMatterLog.info("Hi!!!!");
            DarkMatterLog.info(context.registryManager().get(RegistryKeys.DIMENSION_TYPE).get(new Identifier("overworld")));
            DarkMatterLog.info(context.reloader(TYPE).getFabricId());
        }
    }
}
