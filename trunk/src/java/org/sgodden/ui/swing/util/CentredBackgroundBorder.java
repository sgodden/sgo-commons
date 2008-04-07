package org.sgodden.ui.swing.util;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;

import org.sgodden.ui.swing.util.image.ImageFill;
 
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

