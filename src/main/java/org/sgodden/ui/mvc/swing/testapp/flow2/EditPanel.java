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
package org.sgodden.ui.mvc.swing.testapp.flow2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
@SuppressWarnings("serial")
public class EditPanel 
		extends JPanel 
		implements View {

    public EditPanel(){
	}

    /*
     * (non-Javadoc)
     * @see org.sgodden.ui.mvc.View#initialise(org.sgodden.ui.mvc.Context)
     */
    public void initialise(Context context) {

        System.out.println(context.evaluate("inputFlow2"));

        setLayout(new BorderLayout());
        add(new JLabel("A pretend edit panel for an order line"), BorderLayout.CENTER);
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
    private JPanel makeButtons(final Context context) {
        JPanel ret = new JPanel(new FlowLayout());

        JButton cancel = new JButton("Save");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                context.handleResolution("SAVE");
            }
        });
        ret.add(cancel);

        return ret;
    }

    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

}
