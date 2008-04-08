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
package org.sgodden.ui.mvc.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sgodden.ui.mvc.ModelListener;

/**
 * Default implementation of {@link MessageModel}.
 * @author sgodden
 */
public class DefaultMessageModel 
		implements MessageModel {

	List<Message> messages = new ArrayList<Message>();
	List<ModelListener> listeners = new ArrayList<ModelListener>();
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

	public void addModelListener(ModelListener listener) {
		listeners.add(listener);
	}

	public void removeModelListener(ModelListener listener) {
		listeners.remove(listener);
	}
	
	private void fireModelChanged(){
		for (ModelListener listener : listeners){
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
