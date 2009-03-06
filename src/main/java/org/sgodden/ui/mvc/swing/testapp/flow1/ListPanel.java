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
package org.sgodden.ui.mvc.swing.testapp.flow1;

import org.sgodden.ui.mvc.swing.testapp.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.sgodden.ui.mvc.Context;
import org.sgodden.ui.mvc.View;

/**
 * Provides a dummy list panel for the test app.
 * 
 * @author sgodden
 *
 */
public class ListPanel 
		extends JPanel 
		implements View {
	
	/**
     * Serial version UID.
     */
    private static final long serialVersionUID = 20080527L;

    public ListPanel(){
	}

    /*
     * (non-Javadoc)
     * @see org.sgodden.ui.mvc.View#initialise(org.sgodden.ui.mvc.Context)
     */
    public void initialise(Context context) {
        setLayout(new BorderLayout());
        add(new JLabel("Some pretend list of objects goes here"), BorderLayout.CENTER);
        add(makeButtons(context), BorderLayout.SOUTH);
    }

    /*
     * (non-Javadoc)
     * @see org.sgodden.ui.mvc.View#activate()
     */
    public void activate() {
    }

    /**
     * Makes a panel containing action buttons.
     * @return the panel.
     */
    private JPanel makeButtons(Context context) {
        JPanel ret = new JPanel(new FlowLayout());
        ret.add(new JButton(new EditAction(context))); 
        return ret;
    }

    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

}
