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

import java.util.List;
import org.sgodden.ui.mvc.Model;

/**
 * A simple message model interface for recording and retrieving of messages
 * which can be related to objects or attributes of those objects.
 * <p/>
 * <u>To do:</u>
 * <ul>
 * <li>TODO - perhaps we need to do bulk add to prevent so many firings of changed events</li>
 * <li>TODO - allow message to be keyed by a long so that individual messages can be easily removed.</li>
 * </ul>
 * @author goddens
 *
 */
public interface MessageModel 
        extends Model {

    /**
     * Adds a message to the model.
     * @param m the message to add.
     */
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

    /**
     * Returns all messages linked to a particular attribute path.
     * @param attributePath the attribute path (for instance 'order.deliveryDate').
     * @return the list of messages related to the attribute path.
     */
    public List<Message> getMessagesByAttributePath(String attributePath);

    /**
     * Removes all messages. 
     */
    public void clear();
}
