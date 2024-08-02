package me.melontini.dark_matter.test.base.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import me.melontini.dark_matter.api.base.reflect.UnsafeUtils;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.ClassNode;

public class UnsafeUtilsTest {

  @Test
  public void testTrustedLookup() throws NoSuchFieldException, IllegalAccessException {
    HashMap<?, ?> map = new HashMap<>(78, 0.457f);
    var lookup = UnsafeUtils.lookupIn(HashMap.class);
    Assertions.assertThat(lookup.findVarHandle(HashMap.class, "loadFactor", float.class))
        .isNotNull()
        .extracting(varHandle -> varHandle.get(map), Assertions.as(InstanceOfAssertFactories.FLOAT))
        .isNotNull()
        .isEqualTo(0.457f);
  }

  @Test
  public void testGetPutReference() throws NoSuchFieldException {
    Field field = BigDecimal.class.getDeclaredField("stringCache");

    BigDecimal map = BigDecimal.valueOf(300);
    map.toString();

    Assertions.assertThat(UnsafeUtils.<String>getReference(field, map)).isEqualTo("300");
    UnsafeUtils.putReference(field, map, "999");
    Assertions.assertThat(map.toString()).isEqualTo("999");
  }

  @Test
  public void testAllocateInstance() throws InstantiationException {
    Assertions.assertThat(UnsafeUtils.allocateInstance(TestClassWithObject.class))
        .isNotNull()
        .isInstanceOf(TestClassWithObject.class)
        .hasFieldOrPropertyWithValue(
            "strings", null); // Should be null as constructor was not called.
  }

  @Test
  void testDefineClass() {
    String cls = UnsafeUtilsTest.class.getName().replace('.', '/') + "Dummy";

    ClassNode node = new ClassNode();
    node.visit(Opcodes.V17, Opcodes.ACC_PUBLIC,
            cls,
            null, Type.getInternalName(Object.class), null);

    InstructionAdapter adapter = new InstructionAdapter(node.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null));
    adapter.load(0, Type.getType(Object.class));
    adapter.invokespecial("java/lang/Object", "<init>", "()V", false);
    adapter.anew(Type.getType(UnsupportedOperationException.class));
    adapter.dup();
    adapter.aconst("This test constructor must throw an error!");
    adapter.invokespecial(Type.getInternalName(UnsupportedOperationException.class), "<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String.class)), false);
    adapter.athrow();

    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    node.accept(writer);
    Class<?> defined = UnsafeUtils.defineClass(this.getClass().getClassLoader(), cls.replace('/', '.'), writer.toByteArray(), this.getClass().getProtectionDomain());

    Assertions.assertThatThrownBy(() -> defined.getConstructors()[0].newInstance())
            .isInstanceOf(InvocationTargetException.class)
            .extracting(Throwable::getCause, Assertions.as(InstanceOfAssertFactories.THROWABLE))
            .isInstanceOf(UnsupportedOperationException.class)
            .hasMessage("This test constructor must throw an error!");
  }

  public static class TestClassWithObject {
    public final List<String> strings = List.of("34", "12", "67");
  }
}
