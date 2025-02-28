package me.melontini.dark_matter.api.crash_handler.uploading;

import com.google.gson.JsonObject;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import me.melontini.dark_matter.api.crash_handler.Prop;
import me.melontini.dark_matter.api.crash_handler.Props;
import me.melontini.dark_matter.impl.crash_handler.uploading.MixpanelAPI;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Mixpanel extends Uploader<Void, Mixpanel.Context> {

  static Mixpanel get(String token, boolean eu) {
    return new MixpanelAPI(eu, token);
  }

  /**
   * Uploads the event to Mixpanel.
   * @param context The name of the event and attached props.
   * @return nothing.
   */
  @Override
  CompletableFuture<Void> upload(Context context);

  /**
   * Attaches properties to a JsonObject.
   *
   * @param object The JsonObject to attach properties to.
   * @param props  The properties to attach to the JSONObject.
   * @return The mutated JsonObject.
   */
  @Contract("_, _ -> param1")
  default JsonObject attachProps(JsonObject object, Prop @NotNull ... props) {
    for (Prop prop : props) {
      object.addProperty(getPropName(prop), prop.get());
    }
    return object;
  }

  @Override
  default String getPropName(Prop prop) {
    if (prop == Props.OS) {
      return "$os";
    }
    return prop.name().toLowerCase(Locale.ROOT);
  }

  record Context(String event, JsonObject props) {}
}
