package by.training.zaretskaya.converters;

import by.training.zaretskaya.annotations.ConvertibleToMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapConverter {

    public static Map<String, Map<String, Object>> toMap(Object object) throws IllegalAccessException {
        Class<?> objectClass = object.getClass();
        if (objectClass.getAnnotation(ConvertibleToMap.class) == null) {
            throw new IllegalArgumentException("Class hasn't got annotation.");
        }
        Field[] fields = objectClass.getDeclaredFields();
        Map<String, Object> mapFields = new HashMap<>();
        for (Field field : fields) {
            Object valueField;
            Class<?> type = field.getType();
            if (type.isPrimitive() || type.getAnnotation(ConvertibleToMap.class) != null) {
                checkAccess(field);
                if (!type.isPrimitive()) {
                    if (field.getType().isAssignableFrom(objectClass)) {
                        throw new IllegalArgumentException("Infinite Recursion");
                    }
                    valueField = toMap(field.get(object));
                } else {
                    valueField = field.get(object);
                }
                mapFields.put(field.getName(), valueField);

            }
        }
        Map<String, Map<String, Object>> map = new HashMap<>();
        map.put(object.getClass().getName(), mapFields);
        return map;
    }

    public static Object fromMap(Map<String, Map<String, Object>> map)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchFieldException {
        Set<Map.Entry<String, Map<String, Object>>> entries = map.entrySet();
        String nameClass = null;
        Map<String, Object> mapFields = null;
        for (Map.Entry<String, Map<String, Object>> entry : entries) {
            nameClass = entry.getKey();
            mapFields = entry.getValue();
        }
        Class<?> classInstance = Class.forName(nameClass);
        Object instance = classInstance.newInstance();
        Set<Map.Entry<String, Object>> fields = mapFields.entrySet();
        for (Map.Entry<String, Object> field : fields) {
            String nameField = field.getKey();
            Field fieldInstance = classInstance.getDeclaredField(nameField);
            checkAccess(fieldInstance);
            Object valueField = field.getValue();
            if (valueField instanceof Map) {
                valueField = fromMap((Map<String, Map<String, Object>>) valueField);
            }
            fieldInstance.set(instance, valueField);
        }
        return instance;
    }

    private static void checkAccess(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
