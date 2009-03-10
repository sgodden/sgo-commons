package org.sgodden.ui.mvc;

/**
 * Denotes the capability of a view to be asked to close.
 * @author Lloyd Colling
 *
 */
public interface CloseableView extends View {

    /**
     * Asks the view to close, with a false return value
     * indicating that the view cannot be closed.
     * @return
     */
    public boolean closeView();
}
