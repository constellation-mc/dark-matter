package me.melontini.dark_matter.impl.crash_handler.uploading;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import me.melontini.dark_matter.api.crash_handler.uploading.McLogs;
import me.melontini.dark_matter.api.crash_handler.uploading.Uploader;

public enum McLogsImpl implements McLogs {
  INSTANCE;

  private static final HttpClient CLIENT = HttpClient.newHttpClient();
  private static final String MCLO_GS_API = "https://api.mclo.gs/1/log";

  public static CompletableFuture<String> uploadToMclo_gs(String log) {
    if (log == null || log.isEmpty())
      return CompletableFuture.failedFuture(
          new IllegalArgumentException("Empty or null log provided!"));

    try {
      HttpResponse<String> response = CLIENT.send(
          HttpRequest.newBuilder()
              .uri(URI.create(MCLO_GS_API))
              .header("Content-Type", "application/x-www-form-urlencoded")
              .POST(HttpRequest.BodyPublishers.ofString(
                  "content=" + URLEncoder.encode(log, StandardCharsets.UTF_8)))
              .build(),
          HttpResponse.BodyHandlers.ofString());

      JsonObject jResponse = JsonParser.parseString(response.body()).getAsJsonObject();
      boolean success = jResponse.get("success").getAsBoolean();

      if (success) {
        return CompletableFuture.completedFuture(jResponse.get("url").getAsString());
      } else {
        return CompletableFuture.failedFuture(
            new RuntimeException(jResponse.get("error").getAsString()));
      }
    } catch (IOException | InterruptedException e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  @Override
  public CompletableFuture<String> upload(Context context) {
    if (!Uploader.enabled())
      return CompletableFuture.failedFuture(Uploader.uploadDisabledException());

    return uploadToMclo_gs(context.log());
  }
}
