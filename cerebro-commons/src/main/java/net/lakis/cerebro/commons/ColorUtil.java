package net.lakis.cerebro.commons;

import java.awt.Color;

public class ColorUtil {

	public static final String colorToHex(Color c) {
		return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());

	}

	public static final Color hexStringToColor(String hex) {
		int r = HexUtils.getInt(hex, 0, 2);
		int g = HexUtils.getInt(hex, 2, 2);
		int b = HexUtils.getInt(hex, 4, 2);
		return new Color(r, g, b);
	}
}
