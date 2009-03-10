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
@SuppressWarnings("serial")
public class ContainerImpl extends SingleExpandingComponentPanel implements
        Container {

    private JDialog currentDialog;
    private View currentView;
    private View currentDialogView;

    public void display(final View view) {
        removeAll();
        add((JComponent) view);
        currentView = view;
        validate();
        repaint();
    }

    public void displayModalDialog(final View view) {
        JDialog dialog;
        if (view instanceof JDialog) {
            dialog = (JDialog) view;
        } else {
            dialog = new DialogViewAdapter(view);
        }

        currentDialog = dialog;
        currentDialogView = view;

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
            throw new IllegalStateException(
                    "There is no dialog currently showing");
        }
        currentDialog.dispose();
        currentDialogView = null;
    }

    public View getCurrentView() {
        if (currentDialogView != null)
            return currentDialogView;
        return currentView;
    }

}