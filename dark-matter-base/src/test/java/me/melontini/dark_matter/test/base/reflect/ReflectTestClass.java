package me.melontini.dark_matter.test.base.reflect;

import org.jetbrains.annotations.Nullable;

public class ReflectTestClass {

  private final int privateField = 0xdeadbeef;

  private ReflectTestClass(int value, @Nullable Integer wrapper, Object... varargs) {}

  private static void privateMethod() {}

  public static class Subclass extends ReflectTestClass {

    private Subclass() {
      super(0, null);
    }
  }
}
