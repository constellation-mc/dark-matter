package me.melontini.dark_matter.impl.crash_handler.uploading;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.melontini.dark_matter.api.crash_handler.uploading.Mixpanel;
import me.melontini.dark_matter.api.crash_handler.uploading.Uploader;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

@ApiStatus.Internal
public class MixpanelAPI implements Mixpanel {

    private static final String BASE_URL = "https://api.mixpanel.com";
    private static final String EU_URL = "https://api-eu.mixpanel.com";
    private static final HttpClient CLIENT = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).connectTimeout(Duration.ofMillis(2000)).proxy(ProxySelector.getDefault()).build();
    private final Holder holder;

    public MixpanelAPI(boolean eu, String projectToken) {
        this.holder = new Holder(projectToken, eu ? EU_URL : BASE_URL);
    }

    public void trackEvent(String eventName, JsonObject props) {
        JsonObject object = new JsonObject();
        object.addProperty("event", eventName);

        if (props == null) {
            props = new JsonObject();
        }

        if (!props.has("token")) props.addProperty("token", this.holder.token());
        if (!props.has("time")) props.addProperty("time", System.currentTimeMillis());
        if (!props.has("$insert_id")) props.addProperty("$insert_id", UUID.randomUUID().toString());
        if (!props.has("distinct_id")) props.addProperty("distinct_id", CRASH_UUID.toString());

        object.add("properties", props);

        call(object);
    }

    private void call(JsonObject... objects) {
        if (!Uploader.enabled()) return;

        JsonArray array = new JsonArray();
        for (JsonObject object : objects) {
            array.add(object);
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.holder.endpoint() + "/track?ip=0"))
                    .header("accept", "text/plain")
                    .header("content-type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(array.toString()))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) throw new RuntimeException("Status Code: " + response.statusCode() + " Body: " + response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Void upload(Context context) {
        if (!Uploader.enabled()) return null;

        trackEvent(context.event(), context.props());
        return null;
    }

    private record Holder(String token, String endpoint) {

    }
}
