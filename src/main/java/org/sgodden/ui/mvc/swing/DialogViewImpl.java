package org.sgodden.ui.mvc.swing;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.sgodden.ui.mvc.DialogListener;
import org.sgodden.ui.mvc.DialogView;
import org.sgodden.ui.mvc.View;

public class DialogViewImpl 
	extends JDialog 
	implements DialogView {
	
	private List<DialogListener> listeners = new ArrayList<DialogListener>();
	private View innerView;
	
	public DialogViewImpl(View innerView){
		super();
		this.innerView = innerView;
		add((Component)innerView);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent evt) {
				fireDialogClosed();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.ui.mvc.DialogView#addDialogListener(org.sgodden.ui.mvc.DialogListener)
	 */
	public void addDialogListener(DialogListener l) {
		listeners.add(l);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.ui.mvc.DialogView#removeDialogListener(org.sgodden.ui.mvc.DialogListener)
	 */
	public void removeDialogListener(DialogListener l) {
		listeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * @see org.sgodden.ui.mvc.View#activate()
	 */
	public void activate() {
		innerView.activate();
	}
	
	private void fireDialogClosed(){
		for (DialogListener l : listeners) {
			l.dialogClosed(this);
		}
	}

}