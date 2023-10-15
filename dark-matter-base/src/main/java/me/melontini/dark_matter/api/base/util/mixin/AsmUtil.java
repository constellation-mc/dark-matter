package me.melontini.dark_matter.api.base.util.mixin;

import me.melontini.dark_matter.impl.base.util.mixin.AsmImpl;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.List;
import java.util.Map;

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

    public static Object mapObjectFromAnnotation(Object value, boolean loadEnums, boolean loadClasses) {
        return AsmImpl.mapObjectFromAnnotation(value,  loadEnums, loadClasses);
    }

    public static List<Map<String, Object>> emptyAnnotationList() {
        return AsmImpl.emptyAnnotationList();
    }
}
