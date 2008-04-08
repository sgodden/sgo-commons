/* =================================================================
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#
# ================================================================= */
package org.sgodden.ui.swing.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;

import org.sgodden.ui.swing.util.image.ImageFill;

/**
 * A border which centres and stretches an image in the background.
 * @author sgodden
 */
public class CentredBackgroundBorder implements Border {
    
    private ImageFill imageFill;
 
    public CentredBackgroundBorder(BufferedImage image) {
        this.imageFill = new ImageFill(image);
    }
 
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        //int x0 = x + (width-image.getWidth())/2;
        //int y0 = y + (height-image.getHeight())/2;
        //g. drawImage(imageFill.get, x0, y0, null);
        
        imageFill.paintFill(c, g, x, y, width, height);
    }
 
    public Insets getBorderInsets(Component c) {
        return new Insets(0,0,0,0);
    }
 
    public boolean isBorderOpaque() {
        return true;
    }
}

