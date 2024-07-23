package me.melontini.dark_matter.test.base.reflect;

import me.melontini.dark_matter.api.base.reflect.Reflect;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflectTest {

  @Test
  public void testFindConstructor() {
    Assertions.assertThat(Reflect.findConstructor(ReflectTestClass.class, 23, 56, new Object[0]))
        .isPresent();
  }

  @Test
  public void testFindMethod() {
    Assertions.assertThat(Reflect.findMethod(ReflectTestClass.class, "privateMethod"))
        .isPresent();
  }

  @Test
  public void testFindField() {
    Assertions.assertThat(Reflect.findField(ReflectTestClass.class, "privateField"))
        .isPresent();
  }

  @Test
  public void testFindMethodInHierarchy() {
    Assertions.assertThat(
            Reflect.findMethodInHierarchy(ReflectTestClass.Subclass.class, "privateMethod"))
        .isPresent();
  }

  @Test
  public void testFindFieldInHierarchy() {
    Assertions.assertThat(
            Reflect.findFieldInHierarchy(ReflectTestClass.Subclass.class, "privateField"))
        .isPresent();
  }

  @Test
  public void testCanAccessSameModule() {
    Assertions.assertThat(Reflect.findMethod(ReflectTestClass.class, "privateMethod"))
        .map(Reflect::setAccessible)
        .isPresent();
  }
}
