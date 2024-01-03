package me.melontini.dark_matter.impl.crash_handler.uploading;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.melontini.dark_matter.api.base.util.MakeSure;
import me.melontini.dark_matter.api.crash_handler.uploading.McLogs;
import me.melontini.dark_matter.api.crash_handler.uploading.Uploader;
import me.melontini.dark_matter.impl.base.DarkMatterLog;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public final class McLogsImpl implements McLogs {

    public static final McLogsImpl INSTANCE = new McLogsImpl();

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String MCLO_GS_API = "https://api.mclo.gs/1/log";

    public static String uploadToMclo_gs(String log) {
        MakeSure.notEmpty(log, "Empty or null log provided!");
        try {
            HttpResponse<String> response = CLIENT.send(HttpRequest.newBuilder()
                    .uri(URI.create(MCLO_GS_API))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("content=" + URLEncoder.encode(log, StandardCharsets.UTF_8)))
                    .build(), HttpResponse.BodyHandlers.ofString());

            JsonObject jResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            boolean success = jResponse.get("success").getAsBoolean();

            if (success) return jResponse.get("url").getAsString();
            else throw new RuntimeException(jResponse.get("error").getAsString());
        } catch (IOException | InterruptedException e) {
            DarkMatterLog.error("Failed to upload log to mclo.gs!", e);
            return null;
        }
    }

    @Override
    public String upload(Context context) {
        if (!Uploader.enabled()) return null;

        return uploadToMclo_gs(context.log());
    }
}
