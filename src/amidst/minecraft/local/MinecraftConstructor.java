package amidst.minecraft.local;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import amidst.logging.Log;

public class MinecraftConstructor {
	private Map<String, Class<?>> primitivesMap;
	private MinecraftClass parent;
	private String minecraftName;
	private String[] parameterNames;
	private Class<?>[] parameterClasses;
	private Constructor<?> constructor;

	public MinecraftConstructor(Map<String, Class<?>> primitivesMap,
			MinecraftClass parent, String minecraftName,
			String... parameterNames) {
		this.primitivesMap = primitivesMap;
		this.parent = parent;
		this.minecraftName = minecraftName;
		this.parameterNames = parameterNames;
	}

	public String getMinecraftName() {
		return minecraftName;
	}

	public MinecraftObject call(Object... parameters) {
		return new MinecraftObject(parent, newInstance(parameters));
	}

	private Object newInstance(Object... parameters) {
		try {
			return constructor.newInstance(parameters);
		} catch (IllegalArgumentException e) { // TODO : Add error text
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void initialize(Minecraft minecraft, MinecraftClass minecraftClass) {
		Class<?> clazz = minecraftClass.getClazz();
		try {
			parameterClasses = MinecraftFeatureUtils.getParameterClasses(
					minecraft, parameterNames, primitivesMap);
			constructor = MinecraftFeatureUtils.getConstructor(clazz,
					parameterClasses);
		} catch (SecurityException e) {
			Log.crash(
					e,
					"SecurityException on ("
							+ minecraftClass.getMinecraftName() + " / "
							+ minecraftClass.getByteName() + ") contructor ("
							+ minecraftName + ")");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			Log.crash(
					e,
					"Unable to find class constructor ("
							+ minecraftClass.getMinecraftName() + " / "
							+ minecraftClass.getByteName() + ") ("
							+ minecraftName + ")");
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "[Constructor " + minecraftName + " of class "
				+ parent.getMinecraftName() + "]";
	}
}