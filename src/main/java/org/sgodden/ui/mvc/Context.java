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
package org.sgodden.ui.mvc;

import java.util.Map;
import java.util.Set;

import javax.swing.Action;

import org.apache.commons.beanutils.PropertyUtils;
import org.sgodden.ui.mvc.messages.DefaultMessageModel;
import org.sgodden.ui.mvc.messages.MessageModel;

/**
 * Provides access to context at various levels, and provides the hooks to handle
 * resolutions that occur within views.
 * <p/>
 * FIXME - needs to implement event, conversation, session and application level contexts.(?)
 * <p/>
 * TODO - better documentation and api method naming.
 * <p/>
 * FIXME - view-specific concerns should be separated out.
 * 
 * @author goddens
 *
 */
public class Context 
		implements ResolutionHandler {
	
	private static ThreadLocal<Context> currentContext = new ThreadLocal<Context>();
	
	private NamedObjectResolver conversationContext;
    private MessageModel messageModel = new DefaultMessageModel();
    private ResolutionHandler resolutionHandler;
    private Set<String> availableResolutions;

    private Context() {}
	
	/**
	 * Returns the context for the current conversation.
	 * <p/>
	 * <u>Note for Swing applications</u>
	 * <p/>
	 * In Swing applications, references to the current conversation context must
	 * not be obtained on the EDT, since all applications share that thread for event processing.
	 * <p/>
	 * Instead, obtain a reference to the context when you are created.  When responding
	 * to an event on the EDT, you should normally obtain something from the context.
	 * When that is done, the context will set itself as the thread context, and new
	 * components obtaining a reference to the context will get the correct one.
	 * <p/>
	 * TODO - make that explanation understandable :(
	 * 
	 * @return the context for the current thread.
	 */
	public static Context getCurrentContext() {
		if (currentContext.get() == null) {
			currentContext.set(new Context());
		}
		return currentContext.get();
	}
	
	/**
	 * Evaluates the passed expression, and returns the return value.
	 * 
	 * @param expression a bean-path expression, such as <code>myBean.myValue</code>. 
	 * @return the return value.
	 */
	public Object evaluate(String expression) {

        // we may have been called on the EDT, in which case we need to re-set this instance as the thread local context
        currentContext.set(this);

        Object ret;
		
		String objectName;
		String propertyPath;
		
		if (expression.indexOf('.') < 0) {
			objectName = expression;
			propertyPath = null;
		}
		else {
			objectName = expression.substring(0, expression.indexOf('.'));
			propertyPath = expression.substring(expression.indexOf('.') + 1);
		}
		
		Object o = conversationContext.getNamedObject(objectName);
		
		if (o == null) {
			throw new IllegalArgumentException("Unable to resolve object with name: " + objectName);
		}

		if (propertyPath != null) {
			try {
				ret = PropertyUtils.getNestedProperty(o, propertyPath);
			} catch (Exception e) {
				throw new Error("Error evaluating expression: " + expression, e);
			} 
		}
		else {
			ret = o;
		}
		
		return ret;
	}

	/**
	 * Allows a view to fire a particular named resolution.
	 * <p/>
	 * For instance, in a simple flow where you have a list panel and then an edit
	 * panel, the list panel might fire a resolution called 'SELECT' when the user
	 * double-clicked on a row.  The flow configuration would presumably be
	 * configured to transfer to the EDIT view when this occurs.
	 * <p/>
	 * Remember to name your flow resolutions according to what the user has done, and not
	 * what you think should happen as a consequence, because what happens is configured 
	 * in the flow, and can be changed.
	 * <p/>
	 * So for instance, it would be wrong in the previous example for the list to
	 * fire a resolution of EDIT, because someone might configure this view in 
	 * a different flow so that it just, say, brings up a window showing
	 * what was selected.  The event that occurred was row selection, so just call
	 * the resolution 'SELECT'.
	 */
    public void handleResolution(String resolution) {
        resolutionHandler.handleResolution(resolution);
    }

    /**
     * Sets the object which will be responsible for resolving named beans within
     * the conversation (flow) currently in place.
     * 
     * @param conversationNamedObjectResolver
     */
    public void setConversationNamedObjectResolver(NamedObjectResolver conversationNamedObjectResolver) {
		conversationContext = conversationNamedObjectResolver;
	}

    /**
     * Returns the message model, to be used for recording messages.
     * @return
     */
    public MessageModel getMessageModel() {
        return messageModel;
    }

    /**
     * Sets the handler which will determine what to do when a resolution occurs.
     * <p/>
     * FIXME - name of this method is wrong?
     * @param resolutionHandler
     */
    public void setControllerResolutionHandler(ResolutionHandler resolutionHandler) {
        this.resolutionHandler = resolutionHandler;
    }

    /**
     * Returns a map of actions representing the available resolutions from
     * the current step in the flow.
     * <p/>
     * This allows generic views to construct their available options from what
     * has been configured in the flow, so that may function without modification
     * in any flow.
     * 
     * @return the map of available actions, keyed by destination flow step name.
     */
    public Set<String> getAvailableResolutions() {
		return availableResolutions;
	}

	/**
	 * Sets the resolutions available from the current flow step.
	 * @param availableResolutions the map of available resolutions.
	 */
	public void setAvailableResolutions(Set<String> availableResolutions) {
		this.availableResolutions = availableResolutions;
	}
    

}
