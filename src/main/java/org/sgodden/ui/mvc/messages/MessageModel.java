package org.sgodden.ui.mvc.messages;

import java.util.List;

/**
 * TODO - perhaps we need to do bulk add to prevent so many firings of changed events.
 * TODO - allow message to be keyed by a long so that individual messages can be easily removed.
 * @author goddens
 *
 */
public interface MessageModel {
	
	public void addMessage(Message m);
	
	/**
	 * Returns the messages raw, in the order they were added.
	 * @return
	 */
	public List<Message> getMessages();
	
	/**
	 * Returns the messages in descending time order.
	 * @return
	 */
	public List<Message> getMessagesByTimeDescending();
	
	public List<Message> getMessagesByAttributePath(String attributePath);
	
	/**
	 * Removes all messages. 
	 */
	public void clear();

	/**
	 * Adds a listener to be notified when the model changes.
	 * 
	 * @param listener the listener to be added.
	 */
	public void addMessageModelListener(MessageModelListener listener);
	
	/**
	 * Removes the specified model listener.
	 * 
	 * @param listener the listener to be removed.
	 */
	public void removeMessageModelListener(MessageModelListener listener);

}
