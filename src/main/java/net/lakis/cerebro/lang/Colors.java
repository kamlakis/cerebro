package net.lakis.cerebro.lang;

import java.awt.Color;

public class Colors {

	public static final String colorToHex(Color c) {
		return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());

	}

	public static final Color hexStringToColor(String hex) {
		int r = Hex.getInt(hex, 0, 2);
		int g = Hex.getInt(hex, 2, 2);
		int b = Hex.getInt(hex, 4, 2);
		return new Color(r, g, b);
	}
}
