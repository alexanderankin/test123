import java.lang.reflect.Field;

public class Parent {
	protected void ensureDefaults() {
		Field[] declaredFields = getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			Object fieldValue = getDefaultValueForType(field.getType());
			try {
				System.out.println("defaulting field - name: " + field.getName() + " | this: " + this);
				field.set(this, fieldValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Object getDefaultValueForType(Class<?> type) {
		Object defaultValue = null;
		if (type.isAssignableFrom(String.class)) {
			defaultValue = "default";
		} else if (type.isAssignableFrom(int.class)) {
			defaultValue = -100;
		}
		return defaultValue;
	}
}
