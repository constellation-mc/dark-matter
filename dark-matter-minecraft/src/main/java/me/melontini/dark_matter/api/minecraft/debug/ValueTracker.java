package me.melontini.dark_matter.api.minecraft.debug;

import me.melontini.dark_matter.impl.minecraft.debug.ValueTrackerImpl;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.function.Supplier;

@ApiStatus.Experimental
@SuppressWarnings("unused")
public final class ValueTracker {

    private ValueTracker() {
        throw new UnsupportedOperationException();
    }


    /**
     * This method is used to track anything using a supplier.
     * <p>
     * It's recommended to return strings or objects that implement the toString() method.
     * @param s unique id/name for the tracker.
     */
    public static void addTracker(String s, Supplier<?> supplier) {
        ValueTrackerImpl.addTracker(s, supplier);
    }

    /**
     * This method is used to add a tracker for an object field.
     * @param s unique id/name for the tracker.
     */
    public static void addTracker(String s, Field f, Supplier<Object> objectSupplier) {
        ValueTrackerImpl.addFieldTracker(s, f, objectSupplier);
    }

    /**
     * This method is used to add a tracker for a static field.
     * @param s unique id/name for the tracker.
     */
    public static void addTracker(String s, Field f) {
        ValueTrackerImpl.addStaticFieldTracker(s, f);
    }

    public static void removeTracker(String s) {
        ValueTrackerImpl.removeTracker(s);
    }

}
