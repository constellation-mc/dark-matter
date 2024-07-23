package me.melontini.dark_matter.test.base.reflect;

import me.melontini.dark_matter.api.base.reflect.UnsafeUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public class UnsafeUtilsTest {

    @Test
    public void testTrustedLookup() throws NoSuchFieldException, IllegalAccessException {
        HashMap<?, ?> map = new HashMap<>(78, 0.457f);
        var lookup = UnsafeUtils.lookupIn(HashMap.class);
        Assertions.assertThat(lookup.findVarHandle(HashMap.class, "loadFactor", float.class))
                .isNotNull()
                .extracting(varHandle -> varHandle.get(map), Assertions.as(InstanceOfAssertFactories.FLOAT))
                .isNotNull().isEqualTo(0.457f);
    }

    @Test
    public void testGetPutReference() throws NoSuchFieldException {
        Field field = BigDecimal.class.getDeclaredField("stringCache");

        BigDecimal map = BigDecimal.valueOf(300);
        map.toString();

        Assertions.assertThat(UnsafeUtils.<String>getReference(field, map))
                .isEqualTo("300");
        UnsafeUtils.putReference(field, map, "999");
        Assertions.assertThat(map.toString())
                .isEqualTo("999");
    }

    @Test
    public void testAllocateInstance() throws InstantiationException {
        Assertions.assertThat(UnsafeUtils.allocateInstance(TestClassWithObject.class))
                .isNotNull().isInstanceOf(TestClassWithObject.class)
                .hasFieldOrPropertyWithValue("strings", null); //Should be null as constructor was not called.
    }

    public static class TestClassWithObject {
        public final List<String> strings = List.of("34", "12", "67");
    }
}
