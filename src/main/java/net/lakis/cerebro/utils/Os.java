package net.lakis.cerebro.utils;

import java.lang.reflect.Field;

public final class Os {
	private static final String OS_NAME = System.getProperty("os.name");;
	private static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows");
	private static final boolean IS_WINDOWS_CE = OS_NAME.startsWith("Windows CE");

	private static final boolean IS_LINUX = OS_NAME.startsWith("Linux");
	private static final boolean IS_ANDROID = IS_LINUX && "dalvik".equalsIgnoreCase(System.getProperty("java.vm.name"));
	private static final boolean IS_SOLARIS = OS_NAME.startsWith("Solaris") || OS_NAME.startsWith("SunOS");
	private static final boolean IS_FREEBSD = OS_NAME.startsWith("FreeBSD");
	private static final boolean IS_OPENBSD = OS_NAME.startsWith("OpenBSD");

	private static final boolean IS_MAC = OS_NAME.startsWith("Mac") || OS_NAME.startsWith("Darwin");
	private static final boolean IS_AIX = OS_NAME.startsWith("AIX");
	private static final boolean IS_GNU = OS_NAME.equalsIgnoreCase("gnu");
	private static final boolean IS_KFREEBSD = OS_NAME.equalsIgnoreCase("gnu/kfreebsd");
	private static final boolean IS_NETBSD = OS_NAME.equalsIgnoreCase("netbsd");

	public static final String getOsName() {
		return OS_NAME;
	}

	public static final boolean isMac() {
		return IS_MAC;
	}

	public static final boolean isAndroid() {
		return IS_ANDROID;
	}

	public static final boolean isLinux() {
		return IS_LINUX;
	}

	public static final boolean isAIX() {
		return IS_AIX;
	}

	public static final boolean isWindowsCE() {
		return IS_WINDOWS_CE;
	}

	/** Returns true for any windows variant. */
	public static final boolean isWindows() {
		return IS_WINDOWS;
	}

	public static final boolean isSolaris() {
		return IS_SOLARIS;
	}

	public static final boolean isFreeBSD() {
		return IS_FREEBSD;
	}

	public static final boolean isOpenBSD() {
		return IS_OPENBSD;
	}

	public static final boolean isNetBSD() {
		return IS_NETBSD;
	}

	public static final boolean isGNU() {
		return IS_GNU;
	}

	public static final boolean iskFreeBSD() {
		return IS_KFREEBSD;
	}

	public static void addNativeLibDirectory(String path) {
		try {
			Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");

			usrPathsField.setAccessible(true);

			// get array of paths
			final String[] paths = (String[]) usrPathsField.get(null);
			final String[] newPaths = new String[paths.length + 1];

			for (int i = 0; i < paths.length; i++) {
				if (paths[i].equals(path)) // pathToAdd already exists, exit function
					return;
				newPaths[i] = paths[i];
			}

			newPaths[newPaths.length - 1] = path;
			usrPathsField.set(null, newPaths);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		}
	}

}
