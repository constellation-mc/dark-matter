package me.melontini.dark_matter.api.mixin;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import me.melontini.dark_matter.impl.mixin.AsmImpl;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.InstructionAdapter;
import org.objectweb.asm.tree.AnnotationNode;

@UtilityClass
public class AsmUtil {

  public static Map<String, Object> mapAnnotationNode(AnnotationNode node) {
    return AsmImpl.mapAnnotationNode(node);
  }

  public static <T> T getAnnotationValue(AnnotationNode node, String name, T defaultValue) {
    return AsmImpl.getAnnotationValue(node, name, defaultValue);
  }

  public static Object mapObjectFromAnnotation(Object value) {
    return AsmImpl.mapObjectFromAnnotation(value, true, false);
  }

  public static Object mapObjectFromAnnotation(
      Object value, boolean loadEnums, boolean loadClasses) {
    return AsmImpl.mapObjectFromAnnotation(value, loadEnums, loadClasses);
  }

  public static List<Map<String, Object>> emptyAnnotationList() {
    return AsmImpl.emptyAnnotationList();
  }

  public static void insAdapter(MethodVisitor mv, @NonNull Consumer<InstructionAdapter> consumer) {
    consumer.accept(new InstructionAdapter(mv));
  }

  public static void insAdapter(
      ClassVisitor cv,
      int access,
      String name,
      String descriptor,
      Consumer<InstructionAdapter> consumer) {
    insAdapter(cv, access, name, descriptor, null, null, consumer);
  }

  public static void insAdapter(
      ClassVisitor cv,
      int access,
      String name,
      String descriptor,
      String signature,
      Consumer<InstructionAdapter> consumer) {
    insAdapter(cv, access, name, descriptor, signature, null, consumer);
  }

  public static void insAdapter(
      @NonNull ClassVisitor cv,
      int access,
      String name,
      String descriptor,
      @Nullable String signature,
      @Nullable String[] exceptions,
      @NonNull Consumer<InstructionAdapter> consumer) {
    consumer.accept(
        new InstructionAdapter(cv.visitMethod(access, name, descriptor, signature, exceptions)));
  }
}
