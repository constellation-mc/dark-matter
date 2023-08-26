package me.melontini.dark_matter.api.base.util;

import org.apache.logging.log4j.Logger;

import java.util.function.Function;

import static me.melontini.dark_matter.api.base.util.Utilities.getCallerName;

@SuppressWarnings("unused")
public class PrependingLogger {
    public static final Function<Logger, String> LOGGER_NAME = logger -> !Utilities.IS_DEV ? "(" + logger.getName() + ") " : "";
    public static final Function<Logger, String> METHOD_CALLER = logger -> "[" + getCallerName(3) + "] ";
    public static final Function<Logger, String> METHOD_CALLER_WRAPPED = logger -> "[" + getCallerName(4) + "] ";
    public static final Function<Logger, String> NAME_METHOD_MIX = logger -> {
        String[] caller = Utilities.getCallerName(3).split("\\.");
        return "(" + logger.getName() + " / " + caller[caller.length - 1] + ") ";
    };
    public static final Function<Logger, String> NAME_METHOD_MIX_WRAPPED = logger -> {
        String[] caller = Utilities.getCallerName(4).split("\\.");
        return "(" + logger.getName() + " / " + caller[caller.length - 1] + ") ";
    };

    public static final Function<Logger, String> CALLING_CLASS = logger -> {
        Class<?> caller = Utilities.getCallerClass(3);
        return "[" + caller.getName() + "] ";
    };
    public static final Function<Logger, String> CALLING_CLASS_WRAPPED = logger -> {
        Class<?> caller = Utilities.getCallerClass(4);
        return "[" + caller.getName() + "] ";
    };
    public static final Function<Logger, String> NAME_CLASS_MIX = logger -> {
        Class<?> cls = Utilities.getCallerClass(3);
        String[] split = cls.getName().split("\\.");
        String caller = split[split.length - 1];
        return "(" + logger.getName() + " / " + caller + ") ";
    };

    public static final Function<Logger, String> NAME_CLASS_MIX_WRAPPED = logger -> {
        Class<?> cls = Utilities.getCallerClass(4);
        String[] split = cls.getName().split("\\.");
        String caller = split[split.length - 1];
        return "(" + logger.getName() + " / " + caller + ") ";
    };
    private static final Function<Logger, String> DEFAULT = logger -> "";
    private final Logger backing;
    protected Function<Logger, String> prefixGetter = DEFAULT;

    public PrependingLogger(Logger backing, Function<Logger, String> prefixGetter) {
        this.backing = backing;
        this.prefixGetter = prefixGetter;
    }

    public PrependingLogger(Logger backing) {
        this.backing = backing;
    }

    public void fatal(String msg) {
        backing.fatal(prefixGetter.apply(backing) + msg);
    }

    public void fatal(String msg, Throwable t) {
        backing.fatal(prefixGetter.apply(backing) + msg, t);
    }

    public void fatal(Object msg) {
        backing.fatal(prefixGetter.apply(backing) + "{}", msg);
    }

    public void fatal(String msg, Object... args) {
        backing.fatal(prefixGetter.apply(backing) + msg, args);
    }

    public void error(String msg) {
        backing.error(prefixGetter.apply(backing) + msg);
    }

    public void error(String msg, Throwable t) {
        backing.error(prefixGetter.apply(backing) + msg, t);
    }

    public void error(Object msg) {
        backing.error(prefixGetter.apply(backing) + "{}", msg);
    }

    public void error(String msg, Object... args) {
        backing.error(prefixGetter.apply(backing) + msg, args);
    }

    public void warn(String msg) {
        backing.warn(prefixGetter.apply(backing) + msg);
    }

    public void warn(String msg, Throwable t) {
        backing.warn(prefixGetter.apply(backing) + msg, t);
    }

    public void warn(Object msg) {
        backing.warn(prefixGetter.apply(backing) + "{}", msg);
    }

    public void warn(String msg, Object... args) {
        backing.warn(prefixGetter.apply(backing) + msg, args);
    }

    public void info(String msg) {
        backing.info(prefixGetter.apply(backing) + msg);
    }

    public void info(String msg, Throwable t) {
        backing.info(prefixGetter.apply(backing) + msg, t);
    }

    public void info(Object msg) {
        backing.info(prefixGetter.apply(backing) + "{}", msg);
    }

    public void info(String msg, Object... args) {
        backing.info(prefixGetter.apply(backing) + msg, args);
    }

    public void debug(String msg) {
        backing.debug(prefixGetter.apply(backing) + msg);
    }

    public void debug(String msg, Throwable t) {
        backing.debug(prefixGetter.apply(backing) + msg, t);
    }

    public void debug(Object msg) {
        backing.debug(prefixGetter.apply(backing) + "{}", msg);
    }

    public void debug(String msg, Object... args) {
        backing.debug(prefixGetter.apply(backing) + msg, args);
    }

    public void trace(String msg) {
        backing.trace(prefixGetter.apply(backing) + msg);
    }

    public void trace(String msg, Throwable t) {
        backing.trace(prefixGetter.apply(backing) + msg, t);
    }

    public void trace(Object msg) {
        backing.trace(prefixGetter.apply(backing) + "{}", msg);
    }

    public void trace(String msg, Object... args) {
        backing.trace(prefixGetter.apply(backing) + msg, args);
    }

    public Logger getBacking() {
        return backing;
    }
}
