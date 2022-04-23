package dst.ass2.ioc.di.impl;

import dst.ass2.ioc.di.*;
import dst.ass2.ioc.di.annotation.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ObjectContainer implements IObjectContainer {
    private final Properties properties;
    private final Map<Class<?>, Object> singletons;

    public ObjectContainer(Properties properties) {
        singletons = new HashMap<>();
        this.properties = properties;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject(Class<T> type) throws InjectionException {
        T object;
        if (!type.isAnnotationPresent(Component.class))
            throw new InvalidDeclarationException(type.getName() + " can't be instantiated." +
                    " An object to be instantiated needs the @Component Class annotation.");
        Scope scope = type.getAnnotation(Component.class).scope();
        if (scope == Scope.SINGLETON && singletons.containsKey(type)) {
            return (T) singletons.get(type);
        }
        object = createObject(type);
        if (scope == Scope.SINGLETON) {
            singletons.put(type, object);
        }

        injectFields(type, object);
        injectProperties(type, object);
        initializeMethods(type, object);
        return object;
    }

    private <T> void injectFields(Class<T> type, T object) {
        List<Field> fields = getAllFields(new LinkedList<>(), type);
        for (Field field : fields) {
            if (field.isAnnotationPresent(Inject.class)) {
                boolean optional = field.getAnnotation(Inject.class).optional();
                Class<?> targetType = field.getAnnotation(Inject.class).targetType();
                if (targetType == Void.class || targetType == void.class) {
                    // Default type is the type of the field
                    targetType = field.getType();
                }
                field.setAccessible(true);
                if (optional) {
                    try {
                        Object dependency = getObject(targetType);
                        field.set(object, dependency);
                        // Ignore all the exceptions
                    } catch (Exception ignored) {
                    }
                } else {
                    try {
                        Object dependency = getObject(targetType);
                        field.set(object, dependency);
                        // Throw the exception of field.set and propagate the exceptions of getObject
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        e.printStackTrace();
                        throw new InvalidDeclarationException("Injection error: Unable to set the field: " + field.getName());
                    }
                }
            }
        }
    }

    private <T> void injectProperties(Class<T> type, T object) {
        List<Field> fields = getAllFields(new LinkedList<>(), type);
        for (Field field : fields) {
            if (field.isAnnotationPresent(Property.class)) {
                String key = field.getAnnotation(Property.class).value();
                if (!properties.containsKey(key))
                    throw new ObjectCreationException("Property: " + key + " not available in the container.");
                String value = properties.getProperty(key);
                Class<?> fieldType = field.getType();
                Object convertedValue;
                if (fieldType.equals(String.class)) {
                    convertedValue = value;
                } else {
                    convertedValue = convertStringToPrimitive(value, fieldType);
                }
                field.setAccessible(true);
                try {
                    field.set(object, convertedValue);
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                    throw new InvalidDeclarationException("Property injection error: Unable to set the field: " + field.getName());
                }
            }
        }
    }

    private Object convertStringToPrimitive(String value, Class<?> type) {
        try {
            if (type.equals(Byte.TYPE) || type.equals(Byte.class)) return Byte.valueOf(value);
            if (type.equals(Short.TYPE) || type.equals(Short.class)) return Short.valueOf(value);
            if (type.equals(Integer.TYPE) || type.equals(Integer.class)) return Integer.valueOf(value);
            if (type.equals(Long.TYPE) || type.equals(Long.class)) return Long.valueOf(value);
            if (type.equals(Float.TYPE) || type.equals(Float.class)) return Float.valueOf(value);
            if (type.equals(Double.TYPE) || type.equals(Double.class)) return Double.valueOf(value);
            if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) return Boolean.valueOf(value);
            if ((type.equals(Character.TYPE) || type.equals(Character.class)) && value.length() == 1) return value.charAt(0);
            else throw new TypeConversionException("The type " + type + " is not primitive" +
                    " and can't be used in @Property annotated fields.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new TypeConversionException("The value of the property can't be converted to " + type + " type.");
        }
    }

    private <T> T createObject(Class<T> type) {
        T object;
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
            object = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new ObjectCreationException("There was an error trying to call the default constructor.");
        }
        return object;
    }

    private <T> void initializeMethods(Class<T> type, T object) {
        List<Method> methods = getAllMethods(new LinkedList<>(), type);
        for (Method method : methods) {
            if (method.isAnnotationPresent(Initialize.class)) {
                int parameters = method.getParameterCount();
                if (parameters != 0)
                    throw new InvalidDeclarationException(method.getName() + " method has " + parameters +
                            " parameters. @Initialize annotated methods must have no parameters.");
                method.setAccessible(true);
                try {
                    method.invoke(object);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                    throw new ObjectCreationException("Initialization error: Unable to execute the method: " + method.getName());
                }
            }
        }
    }

    private static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    private static List<Method> getAllMethods(List<Method> methods, Class<?> type) {
        methods.addAll(Arrays.asList(type.getDeclaredMethods()));

        if (type.getSuperclass() != null) {
            getAllMethods(methods, type.getSuperclass());
        }

        return methods;
    }
}
