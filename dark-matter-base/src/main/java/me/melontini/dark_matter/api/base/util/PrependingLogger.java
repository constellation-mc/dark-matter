package me.melontini.dark_matter.api.base.util;

import java.util.function.Function;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PrependingLogger {

  public static final Function<Logger, String> LOGGER_NAME = PrependingLogger::getNameOrEmpty;

  public static final Function<Logger, String> METHOD_CALLER = logger -> {
    String[] caller = Utilities.getCallerName(3).map(PrependingLogger::split).orElseThrow();
    return "[" + caller[caller.length - 1] + "] ";
  };
  public static final Function<Logger, String> METHOD_CALLER_WRAPPED = logger -> {
    String[] caller = Utilities.getCallerName(4).map(PrependingLogger::split).orElseThrow();
    return "[" + caller[caller.length - 1] + "] ";
  };

  public static final Function<Logger, String> NAME_METHOD_MIX = logger -> {
    String[] caller = Utilities.getCallerName(3).map(PrependingLogger::split).orElseThrow();
    return getNameOrEmpty(logger) + "[" + caller[caller.length - 1] + "] ";
  };
  public static final Function<Logger, String> NAME_METHOD_MIX_WRAPPED = logger -> {
    String[] caller = Utilities.getCallerName(4).map(PrependingLogger::split).orElseThrow();
    return getNameOrEmpty(logger) + "[" + caller[caller.length - 1] + "] ";
  };

  public static final Function<Logger, String> CALLING_CLASS = logger -> {
    String[] cls = Utilities.getCallerClass(3)
        .map(Class::getName)
        .map(PrependingLogger::split)
        .orElseThrow();
    return "[" + cls[cls.length - 1] + "] ";
  };
  public static final Function<Logger, String> CALLING_CLASS_WRAPPED = logger -> {
    String[] cls = Utilities.getCallerClass(4)
        .map(Class::getName)
        .map(PrependingLogger::split)
        .orElseThrow();
    return "[" + cls[cls.length - 1] + "] ";
  };

  public static final Function<Logger, String> NAME_CLASS_MIX = logger -> {
    String[] cls = Utilities.getCallerClass(3)
        .map(Class::getName)
        .map(PrependingLogger::split)
        .orElseThrow();
    return getNameOrEmpty(logger) + "[" + cls[cls.length - 1] + "] ";
  };
  public static final Function<Logger, String> NAME_CLASS_MIX_WRAPPED = logger -> {
    String[] cls = Utilities.getCallerClass(4)
        .map(Class::getName)
        .map(PrependingLogger::split)
        .orElseThrow();
    return getNameOrEmpty(logger) + "[" + cls[cls.length - 1] + "] ";
  };

  private static String[] split(String s) {
    return s.split("\\.");
  }

  @Getter
  private final Logger backing;

  protected volatile Function<Logger, String> prefixGetter = logger -> "";

  public PrependingLogger(Logger backing, Function<Logger, String> prefixGetter) {
    this.backing = backing;
    this.prefixGetter = prefixGetter;
  }

  public PrependingLogger(Logger backing) {
    this.backing = backing;
  }

  public static @NotNull PrependingLogger get() {
    String[] cls = Utilities.getCallerClass(2)
        .map(Class::getName)
        .map(PrependingLogger::split)
        .orElseThrow();
    return new PrependingLogger(LogManager.getLogger(cls[cls.length - 1]), LOGGER_NAME);
  }

  @Contract("_ -> new")
  public static @NotNull PrependingLogger get(String name) {
    return new PrependingLogger(LogManager.getLogger(name), LOGGER_NAME);
  }

  @Contract("_, _ -> new")
  public static @NotNull PrependingLogger get(String name, Function<Logger, String> prefixGetter) {
    return new PrependingLogger(LogManager.getLogger(name), prefixGetter);
  }

  @Contract("_ -> new")
  public static @NotNull PrependingLogger withClass(String name) {
    return new PrependingLogger(LogManager.getLogger(name), NAME_CLASS_MIX);
  }

  @Contract("_ -> new")
  public static @NotNull PrependingLogger withMethod(String name) {
    return new PrependingLogger(LogManager.getLogger(name), NAME_METHOD_MIX);
  }

  private static @NotNull String getNameOrEmpty(Logger logger) {
    return !Utilities.isDev() ? "(" + logger.getName() + ") " : "";
  }

  public void fatal(String msg) {
    backing.fatal(prefixGetter.apply(backing) + msg);
  }

  public void fatal(String msg, Throwable t) {
    backing.fatal(prefixGetter.apply(backing) + msg, t);
  }

  public void fatal(Throwable msg) {
    backing.fatal(prefixGetter.apply(backing), msg);
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

  public void error(Throwable msg) {
    backing.error(prefixGetter.apply(backing), msg);
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

  public void warn(Throwable msg) {
    backing.warn(prefixGetter.apply(backing), msg);
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

  public void info(Throwable msg) {
    backing.info(prefixGetter.apply(backing), msg);
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

  public void debug(Throwable msg) {
    backing.debug(prefixGetter.apply(backing), msg);
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

  public void trace(Throwable msg) {
    backing.trace(prefixGetter.apply(backing), msg);
  }

  public void trace(Object msg) {
    backing.trace(prefixGetter.apply(backing) + "{}", msg);
  }

  public void trace(String msg, Object... args) {
    backing.trace(prefixGetter.apply(backing) + msg, args);
  }
}
