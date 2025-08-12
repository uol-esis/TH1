package de.uol.pgdoener.th1.application.mapper;

import de.uol.pgdoener.th1.application.dto.StructureDto;
import de.uol.pgdoener.th1.infastructure.persistence.entity.Structure;
import org.junit.platform.commons.support.ReflectionSupport;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class TestHelper {

    /**
     * Lists all classes in the package of the Structure class that are assignable to Structure.
     * This excludes the Structure class itself.
     *
     * @return a list of classes that extend Structure
     */
    static List<Class<?>> listEntityStructureClasses() {
        return ReflectionSupport.findAllClassesInPackage(
                Structure.class.getPackageName(),
                Structure.class::isAssignableFrom,
                className -> !className.equals(Structure.class.getName())
        );
    }

    /**
     * Lists all classes in the package of the StructureDto class that are assignable to Structure.
     * This excludes the StructureDto class itself.
     *
     * @return a list of classes that extend StructureDto
     */
    static List<Class<?>> listDtoStructureClasses() {
        return ReflectionSupport.findAllClassesInPackage(
                StructureDto.class.getPackageName(),
                StructureDto.class::isAssignableFrom,
                className -> !className.equals(StructureDto.class.getName())
        );
    }

    /**
     * Populates all fields of the given object with test data.
     * This method uses reflection to iterate through all fields of the object,
     * and sets them to a generated value based on their type.
     *
     * @param obj the object whose fields should be populated
     * @throws Exception if an error occurs during reflection or instantiation
     */
    static void populateFields(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            Object value;

            if (Collection.class.isAssignableFrom(fieldType)) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType) {
                    Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (typeArgs.length == 1 && typeArgs[0] instanceof Class<?> itemType) {
                        Object item = generateValue(itemType);
                        List<Object> list = new ArrayList<>();
                        list.add(item);
                        value = list;
                    } else {
                        value = List.of();
                    }
                } else {
                    value = List.of();
                }
            } else {
                value = generateValue(fieldType);
            }

            if (value != null) {
                field.set(obj, value);
            }
        }
    }


    private static Object generateValue(Class<?> type) {
        if (type.equals(String.class)) return "test";
        if (type.equals(int.class) || type.equals(Integer.class)) return 1;
        if (type.equals(boolean.class) || type.equals(Boolean.class)) return true;
        if (type.equals(long.class) || type.equals(Long.class)) return 1L;
        if (type.equals(double.class) || type.equals(Double.class)) return 1.0;

        if (type.isEnum()) return type.getEnumConstants()[0];

        if (type.isArray()) {
            Class<?> componentType = type.getComponentType();
            Object array = Array.newInstance(componentType, 1);
            Array.set(array, 0, generateValue(componentType));
            return array;
        }

        try {
            Constructor<?> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object nestedObj = constructor.newInstance();
            populateFields(nestedObj);
            return nestedObj;
        } catch (Exception e) {
            return null;
        }
    }

}
