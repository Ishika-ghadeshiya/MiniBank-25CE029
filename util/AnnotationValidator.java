
package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import model.annotation.Positive;
import model.annotation.MaxLength;

public class AnnotationValidator {

    public static String[] validate(Object obj) {

        if (obj == null) {
            return new String[] {"Object cannot be null."};
        }

        ArrayList<String> errors = new ArrayList<>();

        Class<?> currentClass = obj.getClass();

        while (currentClass != null
                && currentClass != Object.class) {

            for (Field field : currentClass.getDeclaredFields()) {

                boolean hasPositive =
                    field.isAnnotationPresent(Positive.class);

                boolean hasMaxLength =
                    field.isAnnotationPresent(MaxLength.class);

                if (!hasPositive && !hasMaxLength) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);

                    if (hasPositive) {

                        Positive annotation =
                            field.getAnnotation(Positive.class);

                        if (!(value instanceof Number)
                                || ((Number) value).doubleValue() <= 0) {

                            errors.add(
                                field.getName() + ": "
                                + annotation.message()
                            );
                        }
                    }

                    if (hasMaxLength) {

                        MaxLength annotation =
                            field.getAnnotation(MaxLength.class);

                        if (value instanceof String text
                                && text.length() > annotation.value()) {

                            errors.add(
                                field.getName()
                                + ": maximum length is "
                                + annotation.value()
                            );
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add(
                        "Cannot read field: " + field.getName()
                    );
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
