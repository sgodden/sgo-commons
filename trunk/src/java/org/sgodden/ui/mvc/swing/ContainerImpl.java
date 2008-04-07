package org.sgodden.ui.mvc.swing;

import javax.swing.JComponent;
import javax.swing.JDialog;

import org.sgodden.ui.mvc.Container;
import org.sgodden.ui.mvc.View;

/**
 * A swing-based implementation of the container interface.
 * 
 * @author goddens
 *
 */
public class ContainerImpl
		extends SingleExpandingComponentPanel
		implements Container {

	private JDialog currentDialog;
	
	public void display(final View view) {
		removeAll();
		add((JComponent)view);
		validate();
		repaint();
	}
	
	public void displayModalDialog(final View view) {
		JDialog dialog;
		if (view instanceof JDialog) {
			dialog = (JDialog) view;
		}
		else {
			dialog = new DialogViewAdapter(view);
		}
		
		currentDialog = dialog;
		
		dialog.setModal(true);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
	}
	
	private static class DialogViewAdapter extends JDialog {
		
		private DialogViewAdapter(View view) {
			add((JComponent) view);
			pack();
		}
		
	}

	public void closeDialog() {
		if (currentDialog == null) {
			throw new IllegalStateException("There is no dialog currently showing");
		}
		currentDialog.dispose();
	}

}