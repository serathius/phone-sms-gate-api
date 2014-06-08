package datamodel;

public class SMS {
	
	private String id;
	private String timestamp;
	private String recipient;
	private boolean national;
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
