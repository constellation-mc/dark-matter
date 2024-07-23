package me.melontini.dark_matter.api.minecraft.debug;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.minecraft.debug.ValueTrackerImpl;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
@ApiStatus.Experimental
@SuppressWarnings("unused")
public final class ValueTracker {

  /**
   * This method is used to track anything using a supplier.
   * <p>
   * It's recommended to return strings or objects that implement the toString() method.
   * <p>
   * The supplier cannot be null, but can return null.
   * @param trackerName unique id/name for the tracker.
   */
  public static void addTracker(String trackerName, Supplier<?> supplier) {
    ValueTrackerImpl.addTracker(trackerName, supplier);
  }

  /**
   * This method is used to track anything using a supplier, but you can specify a duration.
   * <p>
   * Once the time is up, the tracker will be removed.
   * <p>
   * The supplier cannot be null, but can return null.
   * @param trackerName unique id/name for the tracker.
   */
  public static void addTracker(String trackerName, Supplier<?> supplier, Duration duration) {
    ValueTrackerImpl.addTracker(trackerName, supplier, duration);
  }

  /**
   * This method is used to add a tracker for an object field.
   * @param trackerName unique id/name for the tracker.
   */
  public static void addTracker(String trackerName, Field f, Object o) {
    ValueTrackerImpl.addFieldTracker(trackerName, f, o);
  }

  /**
   * This method is used to add a tracker for an object field, but you can specify a duration.
   * <p>
   * Once the time is up, the tracker will be removed.
   * @param trackerName unique id/name for the tracker.
   */
  public static void addTracker(String trackerName, Field f, Object o, Duration duration) {
    ValueTrackerImpl.addFieldTracker(trackerName, f, o, duration);
  }

  /**
   * This method is used to add a tracker for a static field.
   * @param trackerName unique id/name for the tracker.
   */
  public static void addTracker(String trackerName, Field f) {
    ValueTrackerImpl.addStaticFieldTracker(trackerName, f);
  }

  /**
   * This method is used to add a tracker for a static field, but you can specify a duration.
   * <p>
   * Once the time is up, the tracker will be removed.
   * @param trackerName unique id/name for the tracker.
   */
  public static void addTracker(String trackerName, Field f, Duration duration) {
    ValueTrackerImpl.addStaticFieldTracker(trackerName, f, duration);
  }

  public static void removeTracker(String trackerName) {
    ValueTrackerImpl.removeTracker(trackerName);
  }
}
