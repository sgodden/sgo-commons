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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sgodden.ui.mvc.Context;
import org.sgodden.ui.mvc.View;
import org.sgodden.ui.mvc.swing.testapp.CancelAction;
import org.sgodden.ui.mvc.swing.testapp.SaveAction;

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

    private static final transient Log log = LogFactory.getLog(EditPanel.class);
    
    private MaintenanceController controller;
    
    private JLabel label;

    private Context context;
	
	public EditPanel(){
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.ui.mvc.View#initialise(org.sgodden.ui.mvc.Context)
	 */
    public void initialise(Context context) {
        this.context = context;
        controller = (MaintenanceController)
                context.evaluate("controller");

        setLayout(new BorderLayout());
        label = new JLabel("");
        add(label, BorderLayout.CENTER);
        add(makeButtons(context), BorderLayout.SOUTH);
    }

    /*
     * (non-Javadoc)
     * @see org.sgodden.ui.mvc.View#activate()
     */
    public void activate() {
        if (controller.getFail()) {
            label.setText("Here's the edit view - the last validation failed");
        }
        else {
            label.setText("Here's the edit panel");
        }
        log.info("Checking flow variable 'outputFlow1': "
                + context.evaluate("outputFlow1"));
    }

    /**
     * Returns a panel containing action buttons.
     * @param context the current context.
     * @return the panel.
     */
    private JPanel makeButtons(final Context context) {
        JPanel ret = new JPanel(new FlowLayout());
        
        ret.add(new JButton(new CancelAction(context)));
        
        ret.add(new JButton(new SaveAction(context){
            @Override
            public void actionPerformed(ActionEvent evt) {
                controller.setFail(false);
                super.actionPerformed(evt);
            }
        }));
        
        JButton failSave = new JButton(new SaveAction(context){
            @Override
            public void actionPerformed(ActionEvent evt) {
                controller.setFail(true);
                super.actionPerformed(evt);
            }
        });
        failSave.setText("Fail save");
        ret.add(failSave);

        JButton subflow = new JButton(new AbstractAction(){
            public void actionPerformed(ActionEvent e) {
                context.setFlowVariable("outputFlow1", "Blah");
                context.handleResolution("SUBFLOW");
            }
        });
        subflow.setText("Sub flow");
        ret.add(subflow);
        
        return ret;
    }

    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

}