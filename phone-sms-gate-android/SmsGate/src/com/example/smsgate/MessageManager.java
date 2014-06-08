package com.example.smsgate;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

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
		Address responseAddress = null;
		try {
			responseAddress = message.getFrom()[0];
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		if (createdSMS != null && responseAddress != null) {
		SMSSender sender = new SMSSender();
		sender.sendSMSWithResponseAddress(createdSMS, responseAddress);
		}
	}
}
