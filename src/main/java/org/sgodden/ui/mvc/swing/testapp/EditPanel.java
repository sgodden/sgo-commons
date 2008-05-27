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
package org.sgodden.ui.mvc.swing.testapp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

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
public class EditPanel 
		extends JPanel 
		implements View {

	/**
     * Serial version UID.
     */
    private static final long serialVersionUID = 20080527L;
    private MaintenanceController maintenanceController;
	
	public EditPanel(){
	}
	
	public void setMaintenanceController(MaintenanceController maintenanceController) {
		this.maintenanceController = maintenanceController;
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.ui.mvc.View#initialise(org.sgodden.ui.mvc.Context)
	 */
    public void initialise(Context context) {
        System.out.println("Edit panel: initialise");
        setLayout(new BorderLayout());
        add(new JLabel("Here's the edit panel"), BorderLayout.CENTER);
        add(makeButtons(context), BorderLayout.SOUTH);
    }

    /*
     * (non-Javadoc)
     * @see org.sgodden.ui.mvc.View#activate()
     */
    public void activate() {
    }

    /**
     * Returns a panel containing action buttons.
     * @param context the current context.
     * @return the panel.
     */
    private JPanel makeButtons(Context context) {
        JPanel ret = new JPanel(new FlowLayout());
        
        ret.add(new JButton(new CancelAction(context)));
        
        ret.add(new JButton(new SaveAction(context){
            @Override
            public void actionPerformed(ActionEvent evt) {
                maintenanceController.setFail(false);
                super.actionPerformed(evt);
            }
        }));
        
        JButton failSave = new JButton(new SaveAction(context){
            @Override
            public void actionPerformed(ActionEvent evt) {
                maintenanceController.setFail(true);
                super.actionPerformed(evt);
            }
        });
        failSave.setText("Fail save");
        ret.add(failSave);
        
        return ret;
    }

}
