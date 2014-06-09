package datamodel;

/**
 * Class representing SMS message
 */
public class SMS {

	
	/* Message identifier*/
	private String id;
	
	/* Message timestamp*/
	private String timestamp;
	
	/* Recipient phone number*/
	private String recipient;
	
	/* Indicates if SMS message can contain national letters */
	private boolean national;
	
	/* Body of the message*/
	private String body;
	
	public SMS(String id, String timestamp, String recipient, boolean national,
			String body) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.recipient = recipient;
		this.national = national;
		this.body = body;
	}
	
	public String getBody() {
		return body;
	}
	public String getRecipient() {
		return recipient;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public String getId() {
		return id;
	}
	public boolean isNational() {
		return national;
	}

}
