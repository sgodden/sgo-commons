package org.sgodden.ui.swing.util;

import javax.swing.ImageIcon;

public class IconUtils {

	public static ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = IconUtils.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

}
