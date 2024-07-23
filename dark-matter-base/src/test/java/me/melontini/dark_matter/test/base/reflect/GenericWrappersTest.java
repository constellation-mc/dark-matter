package me.melontini.dark_matter.test.base.reflect;

import me.melontini.dark_matter.api.base.reflect.wrappers.GenericConstructor;
import me.melontini.dark_matter.api.base.reflect.wrappers.GenericField;
import me.melontini.dark_matter.api.base.reflect.wrappers.GenericMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GenericWrappersTest {

    @Test
    @Order(0)
    public void testCreateGenericConstructor() {
        GenericConstructor<ReflectTestClass> constructor = GenericConstructor.of(ReflectTestClass.class, int.class, Integer.class, Object[].class);
        constructor.accessible(true).construct(23, 45, new Object[0]);
    }

    @Test
    @Order(1)
    public void testCreateGenericMethod() {
        GenericConstructor<ReflectTestClass> constructor = GenericConstructor.of(ReflectTestClass.class, int.class, Integer.class, Object[].class);
        var inst = constructor.accessible(true).construct(23, 45, new Object[0]);

        GenericMethod<ReflectTestClass, Void> method = GenericMethod.of(ReflectTestClass.class, "privateMethod");
        method.accessible(true).invoke(inst);
    }

    @Test
    @Order(1)
    public void testCreateGenericField() {
        GenericConstructor<ReflectTestClass> constructor = GenericConstructor.of(ReflectTestClass.class, int.class, Integer.class, Object[].class);
        var inst = constructor.accessible(true).construct(23, 45, new Object[0]);

        GenericField<ReflectTestClass, Integer> field = GenericField.of(ReflectTestClass.class, "privateField");
        Assertions.assertThat(field.accessible(true).get(inst))
                .isEqualTo(0xdeadbeef);
    }
}
