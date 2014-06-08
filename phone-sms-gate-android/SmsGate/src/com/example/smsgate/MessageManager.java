package com.example.smsgate;

import javax.mail.Message;

import android.util.Log;
import datamodel.SMS;

public class MessageManager {
	
	private MailParser mailParser;
	
	public MessageManager() {
		mailParser = new MailParser();
	}

	public void sendMessagesAsSMS(Message[] messages) {
		mailParser = new MailParser();
		for(Message msg : messages) {
			prepareAndSendMessageAsSMS(msg);
		}
	}
	
	private void prepareAndSendMessageAsSMS(Message message) {
		SMS createdSMS = mailParser.createSMSWithMailMessage(message);
		if (createdSMS != null) {
		SMSSender sender = new SMSSender();
		sender.sendSMS(createdSMS);
		}
	}
}
