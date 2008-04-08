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
package org.sgodden.ui.mvc.swing;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * A panel with a border layout that puts its single child into the center
 * region so that it takes up all available space.
 * @author sgodden
 */
public class SingleExpandingComponentPanel extends JPanel {
	
	public SingleExpandingComponentPanel(){
		setLayout(new BorderLayout());		
	}
	
	public SingleExpandingComponentPanel(JComponent component){
		this();
		setComponent(component);
	}
	
	public void setComponent(JComponent c){
		if (getComponentCount() > 0){
			removeAll();
		}
		add(c, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

}
