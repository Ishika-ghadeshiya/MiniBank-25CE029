
package service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import model.annotation.Id;

public class Repository<T> {

    private final HashMap<String, T> items = new HashMap<>();

    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException(
                "Entity cannot be null."
            );
        }

        Class<?> currentClass = entity.getClass();

        while (currentClass != null) {

            for (Field field : currentClass.getDeclaredFields()) {

                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);

                        Object value = field.get(entity);

                        if (value == null) {
                            throw new IllegalArgumentException(
                                "Entity ID cannot be null."
                            );
                        }

                        String id = value.toString();

                        if (id.isBlank()) {
                            throw new IllegalArgumentException(
                                "Entity ID cannot be empty."
                            );
                        }

                        items.put(id, entity);
                        return entity;

                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(
                            "Unable to access entity ID.", e
                        );
                    }
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        throw new IllegalArgumentException(
            "No field marked with @Id was found."
        );
    }

    public T findById(String id) {
        return items.get(id);
    }

    public ArrayList<T> findAll() {
        return new ArrayList<>(items.values());
    }

    public boolean delete(String id) {
        return items.remove(id) != null;
    }

    public int size() {
        return items.size();
    }

    @SafeVarargs
    public final void saveAll(T... entities) {
        for (T entity : entities) {
            save(entity);
        }
    }
}
