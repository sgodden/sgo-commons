package org.sgodden.ui.mvc.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultMessageModel 
		implements MessageModel {

	List<Message> messages = new ArrayList<Message>();
	List<MessageModelListener> listeners = new ArrayList<MessageModelListener>();
	Map<String, List<Message>> messagesByAttributePath = new HashMap<String, List<Message>>();
	
	public List<Message> getMessages() {
		return messages;
	}
	
	public void addMessage(Message m){
		messages.add(m);
		
		List<Message> messages;
		if (messagesByAttributePath.containsKey(m.getAttributePath())) {
			messages = messagesByAttributePath.get(m.getAttributePath());
		} else {
			messages = new ArrayList<Message>();
			messagesByAttributePath.put(m.getAttributePath(), messages);
		}
		messages.add(m);
		
		fireModelChanged();
	}
	
	public List<Message> getMessagesByAttributePath(String attributePath){
		return messagesByAttributePath.get(attributePath);
	}

	public void addMessageModelListener(MessageModelListener listener) {
		listeners.add(listener);
	}

	public void removeMessageModelListener(MessageModelListener listener) {
		listeners.remove(listener);
	}
	
	private void fireModelChanged(){
		for (MessageModelListener listener : listeners){
			listener.modelChanged(this);
		}
	}

	public List<Message> getMessagesByTimeDescending() {
		List<Message> ret = new ArrayList<Message>();
		ret.addAll(messages);
		Collections.sort(ret);
		return ret;
	}

	public void clear() {
		messages.clear();
		messagesByAttributePath.clear();
		fireModelChanged();
	}
	
}
