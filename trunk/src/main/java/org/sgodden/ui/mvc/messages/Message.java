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

import java.util.Date;

/**
 * A message that should be communicated to the user.
 * <p/>
 * The natural ordering sequnce of this class is messageTime descending.
 * The rationale behind that is that if you want the messages in ascending
 * messageTime sequence, you can just read them in arrival sequence in
 * whatever collection you are storing them.
 * 
 * @author goddens
 */
public class Message
        implements Comparable<Message> {

    private String messageKey;
    private String fallbackText;
    private String attributePath;
    private MessageSeverity messageType;
    private Date messageTime;
    // TODO - do we need to hold a reference to the object against which the message is being issued?
    /**
     * Constructs a new message, with message type {@link MessageType.INFO}.
     * 
     * @param messageKey the key of the message in the application properties file.
     * @param fallbackText text to be displayed in case the message key is not found in the properties file.
     * @param attributePath the path to the object attribute to which this message relates, or <code>null</code> if
     * the message is global.
     */
    public Message(String messageKey, String fallbackText, String attributePath) {
        super();
        this.messageKey = messageKey;
        this.fallbackText = fallbackText;
        this.attributePath = attributePath;
        this.messageTime = new Date();
    }

    /**
     * Constructs a new message.
     * 
     * @param messageType message type;
     * @param messageKey the key of the message in the application properties file.
     * @param fallbackText text to be displayed in case the message key is not found in the properties file.
     * @param attributePath the path to the object attribute to which this message relates, or <code>null</code> if
     * the message is global.
     */
    public Message(MessageSeverity messageType, String messageKey, String fallbackText, String attributePath) {
        this(messageKey, fallbackText, attributePath);
        this.messageType = messageType;
    }

    /**
     * Returns the attribute path to which this message relates.
     * @return the attribute path.
     */
    public String getAttributePath() {
        return attributePath;
    }

    /**
     * Sets the attribute path to which this message relates.
     * @param attributePath the attribute path.
     */
    public void setAttributePath(String attributePath) {
        this.attributePath = attributePath;
    }

    /**
     * Returns the text to be displayed if if the message key is not
     * found in the resource bundle.
     * @return the fallback text.
     */
    public String getFallbackText() {
        return fallbackText;
    }

    /**
     * Sets the text to be displayed if if the message key is not
     * found in the resource bundle.
     * @param fallbackText the fallback text.
     */
    public void setFallbackText(String fallbackText) {
        this.fallbackText = fallbackText;
    }

    /**
     * Returns the resource bundle key of the message to display.
     * @return the message key.
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Sets the resource bundle key of the message to display.
     * @param messageKey the message key.
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Returns the message type.
     * @return the message type.
     */
    public MessageSeverity getMessageType() {
        return messageType;
    }

    /**
     * Sets the message type.
     * @param messageType the message type.
     */
    public void setMessageType(MessageSeverity messageType) {
        this.messageType = messageType;
    }

    /**
     * Returns the time at which this message was created.
     * @return the message time.
     */
    public Date getMessageTime() {
        return messageTime;
    }

    /**
     * Sets the time at which this message was created.
     * @param messageTime the message time.
     */
    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    /**
     * Returns the message text.
     * @return the message text.
     */
    public String getText() {
        return fallbackText; // FIXME - the proper resource text should be retrieved
    }

    /**
     * Sequences messages according to the message time.
     * @param arg0 the other message.
     * @return the relevant comparison result.
     */
    public int compareTo(Message arg0) {
        if (this.messageTime.getTime() >
                arg0.getMessageTime().getTime()) {
            return -1;
        } else {
            return 1;
        }
    }
}
