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

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.sgodden.ui.mvc.DialogListener;
import org.sgodden.ui.mvc.DialogView;
import org.sgodden.ui.mvc.View;

/**
 * A swing-based implementation of the dialog view interface.
 * @author sgodden
 */
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