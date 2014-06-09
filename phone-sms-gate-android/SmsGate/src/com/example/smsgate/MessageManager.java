package com.example.smsgate;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import android.util.Log;
import datamodel.SMS;
/**
 * 
 * Class responsible for handling and sending e-mail
 * messages retrieved from mailbox
 *
 */
public class MessageManager {
	
	private MailParser mailParser;
	
	public MessageManager() {
		mailParser = new MailParser();
	}
	
	/**
	 * Method which take list of received e-mail and processes it
	 * @param messages
	 */
	public void sendMessagesAsSMS(Message[] messages) {
		mailParser = new MailParser();
		for(Message msg : messages) {
			prepareAndSendMessageAsSMS(msg);
		}
	}
	
	/**
	 * Method which prepares SMS basing on received email and sends it
	 * @param message
	 */
	private void prepareAndSendMessageAsSMS(Message message) {
		SMS createdSMS = mailParser.createSMSWithMailMessage(message);

		if (ContactsManager.getInstance().checkPhoneNumber(createdSMS.getRecipient())) {
	
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
}
