package me.melontini.dark_matter.impl.base;

import me.melontini.dark_matter.api.base.util.PrependingLogger;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.ApiStatus;

import static me.melontini.dark_matter.api.base.util.Utilities.STACK_WALKER;

@ApiStatus.Internal
public class DarkMatterLog {
    private static final PrependingLogger BACKING = new PrependingLogger(LogManager.getLogger("Dark Matter"), PrependingLogger.NAME_METHOD_MIX_WRAPPED);

    public static void error(String msg) {
        BACKING.error(msg);
    }

    public static void error(String msg, Throwable t) {
        BACKING.error(msg, t);
    }

    public static void error(Object msg) {
        BACKING.error(msg);
    }

    public static void error(String msg, Object... args) {
        BACKING.error(msg, args);
    }

    public static void warn(String msg) {
        BACKING.warn(msg);
    }

    public static void warn(String msg, Throwable t) {
        BACKING.warn(msg, t);
    }

    public static void warn(Object msg) {
        BACKING.warn(msg);
    }

    public static void warn(String msg, Object... args) {
        BACKING.warn(msg, args);
    }

    public static void info(String msg) {
        BACKING.info(msg);
    }

    public static void info(String msg, Throwable t) {
        BACKING.info(msg, t);
    }

    public static void info(Object msg) {
        BACKING.info(msg);
    }

    public static void info(String msg, Object... args) {
        BACKING.info(msg, args);
    }

    public static void traceCalls() {
        STACK_WALKER.walk(s -> {
            s.forEach(stackFrame -> {
                StringBuilder b = new StringBuilder();
                b.append("at: ").append(stackFrame.getLineNumber());
                b.append("   ");
                b.append(stackFrame.getClassName()).append("#").append(stackFrame.getMethodName()).append(stackFrame.getDescriptor());
                if (stackFrame.isNativeMethod()) b.append(" native method");
                BACKING.getBacking().error(b.toString());
            });
            return null;
        });
    }
}
