/*
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */
package org.sgodden.ui.swing.util.image;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

/**
 * Displays a single <code>BufferedImage</code>, scaled to fit the
 * <code>paintFill</code> rectangle. 
 * <pre>
 *  BufferedImage image = ImageIO.read(new File("background.jpg"));
 *  final ImageFill imageFill = new ImageFill(image);
 *  JPanel p = new JPanel() {
 *      public c void paintComponent(Graphics g) {
 *	    imageFill.paintFill(this, g);
 *	}
 *  };
 * </pre>
 * Note that animated gifs aren't supported as there's no image observer.
 */
public class ImageFill extends Fill {
	private final static int IMAGE_CACHE_SIZE = 8;

	private BufferedImage image;

	private BufferedImage[] imageCache = new BufferedImage[IMAGE_CACHE_SIZE];

	private int imageCacheIndex = 0;

	/**
	 * Creates an <code>ImageFill</code> that draws <i>image</i>
	 * scaled to fit the <code>paintFill</code> rectangle 
	 * parameters.
	 * 
	 * @see #getImage
	 * @see #paintFill
	 */
	public ImageFill(BufferedImage image) {
		this.image = image;
	}

	/**
	 * Creates an "empty" ImageFill.  Before the ImageFill can be 
	 * drawn with the <code>paintFill</code> method, the 
	 * <code>image</code> property must be set.
	 * 
	 * @see #setImage
	 * @see #paintFill
	 */
	public ImageFill() {
		this.image = null;
	}

	/**
	 * Returns the image that the <code>paintFill</code> method draws.
	 * 
	 * @return the value of the <code>image</code> property
	 * @see #setImage
	 * @see #paintFill
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Set the image that the <code>paintFill</code> method draws.
	 * 
	 * @param image the new value of the <code>image</code> property
	 * @see #getImage
	 * @see #paintFill
	 */
	public void setImage(BufferedImage image) {
		this.image = image;
		for (int i = 0; i < imageCache.length; i++) {
			imageCache[i] = null;
		}
	}

	/** 
	 * Returns the actual width of the <code>BufferedImage</code> 
	 * rendered by the <code>paintFill</code> method.  If the image
	 * property hasn't been set, -1 is returned.
	 * 
	 * @return the value of <code>getImage().getWidth()</code> or -1 if
	 *     getImage() returns null
	 * @see #getHeight
	 * @see #setImage
	 */
	public int getWidth() {
		BufferedImage image = getImage();
		return (image == null) ? -1 : image.getWidth();
	}

	/** 
	 * Returns the actual height of the <code>BufferedImage</code> 
	 * rendered by the <code>paintFill</code> method.  If the image
	 * property hasn't been set, -1 is returned.
	 * 
	 * @return the value of <code>getImage().getHeight()</code> or -1 if
	 *     getImage() returns null
	 * @see #getWidth
	 * @see #setImage
	 */
	public int getHeight() {
		BufferedImage image = getImage();
		return (image == null) ? -1 : image.getHeight();
	}

	/** 
	 * Create a copy of image scaled to width,height w,h and 
	 * add it to the null element of the imageCache array.  If 
	 * the imageCache array is full, then we replace the "least
	 * recently used element", at imageCacheIndex.
	 */
	private BufferedImage createScaledImage(Component c, int w, int h) {
		GraphicsConfiguration gc = c.getGraphicsConfiguration();
		BufferedImage newImage = gc.createCompatibleImage(w, h,
				Transparency.TRANSLUCENT);

		boolean cacheOverflow = true;
		for (int i = 0; i < imageCache.length; i++) {
			Image image = imageCache[i];
			if (image == null) {
				imageCache[i] = newImage;
				cacheOverflow = false;
				break;
			}
		}
		if (cacheOverflow) {
			imageCache[imageCacheIndex] = newImage;
			imageCacheIndex = (imageCacheIndex + 1) % imageCache.length;
		}

		Graphics g = newImage.getGraphics();
		int width = image.getWidth();
		int height = image.getHeight();
		g.drawImage(image, 0, 0, w, h, 0, 0, width, height, null);
		g.dispose();

		return newImage;
	}

	/**
	 * Returns either the image itself or a cached scaled copy.
	 */
	private BufferedImage getFillImage(Component c, int w, int h) {
		if ((w == getWidth()) && (h == getHeight())) {
			return image;
		}
		for (int i = 0; i < imageCache.length; i++) {
			BufferedImage cimage = imageCache[i];
			if (cimage == null) {
				break;
			}
			if ((cimage.getWidth(c) == w) && (cimage.getHeight(c) == h)) {
				return cimage;
			}
		}
		return createScaledImage(c, w, h);
	}

	/** 
	 * Draw the image at <i>r.x,r.y</i>, scaled to <i>r.width</i> 
	 * and <i>r.height</i>.
	 */
	public void paintFill(Component c, Graphics g, Rectangle r) {
		if ((r.width > 0) && (r.height > 0)) {
			BufferedImage fillImage = getFillImage(c, r.width, r.height);
			g.drawImage(fillImage, r.x, r.y, c);

		}
	}
}
