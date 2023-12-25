package me.melontini.dark_matter.impl.analytics;

import me.melontini.dark_matter.api.analytics.Analytics;
import me.melontini.dark_matter.api.base.util.BadCrypt;
import me.melontini.dark_matter.impl.base.DarkMatterLog;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.UUID;

public class AnalyticsImpl implements Analytics {

    private final UUID uuid;

    public AnalyticsImpl(ModContainer mod) {
        this.uuid = Analytics.uniqueId() ? this.init(mod) : this.off(mod);
    }

    private UUID off(ModContainer mod) {
        try {
            byte[] name = (mod.getMetadata().getId() + FabricLoader.getInstance().getGameDir().toString()).getBytes();
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            Path idPath = FabricLoader.getInstance().getGameDir().resolve(".dark-matter/analytics").resolve(BadCrypt.digestToHexString(name, d).substring(0, 35) + ".id");

            Files.deleteIfExists(idPath);
        } catch (Exception e) {
            DarkMatterLog.error("Failed to init analytics for mod " + mod.getMetadata().getId(), e);
        }
        return nullID;
    }

    private UUID init(ModContainer mod) {
        try {
            byte[] name = (mod.getMetadata().getId() + FabricLoader.getInstance().getGameDir().toString()).getBytes();
            MessageDigest d = MessageDigest.getInstance("SHA-256");
            Path idPath = FabricLoader.getInstance().getGameDir().resolve(".dark-matter/analytics").resolve(BadCrypt.digestToHexString(name, d).substring(0, 35) + ".id");

            UUID uuid = UUID.randomUUID();
            if (!Files.exists(idPath)) {
                Files.createDirectories(idPath.getParent());

                Files.writeString(idPath, BadCrypt.Base64Based.encryptToStr(uuid.toString(), name, d));
                return uuid;
            }

            //This is completely unnecessary, but it's pretty funny.
            String encryptedUUID = Files.readString(idPath);
            uuid = UUID.fromString(BadCrypt.Base64Based.decryptToStr(encryptedUUID, name, d));
            return uuid;
        } catch (Exception e) {
            DarkMatterLog.error("Failed to init analytics for mod " + mod.getMetadata().getId(), e);
            return nullID;
        }
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }
}
