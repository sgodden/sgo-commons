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
	private MessageType messageType;
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
	public Message(MessageType messageType, String messageKey, String fallbackText, String attributePath) {
		this(messageKey, fallbackText, attributePath);
		this.messageType = messageType;
	}

	public String getAttributePath() {
		return attributePath;
	}

	public void setAttributePath(String attributePath) {
		this.attributePath = attributePath;
	}

	public String getFallbackText() {
		return fallbackText;
	}

	public void setFallbackText(String fallbackText) {
		this.fallbackText = fallbackText;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public Date getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(Date messageTime) {
		this.messageTime = messageTime;
	}
	
	public String getText(){
		return fallbackText; // FIXME - the proper resource text should be retrieved
	}

	public int compareTo(Message arg0) {
		if (this.messageTime.getTime() > 
			arg0.getMessageTime().getTime()){
			return -1;
		} else {
			return 1;
		}
	}
	
}
