package com.example.smsgate;

import java.io.IOException;
import java.util.ArrayList;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

import android.util.Log;
import datamodel.SMS;


public class MailParser {
	
	
	public static final String MailWithSMSSubject = "SMS-GATE";
	
	/**
	 * 
	 * Enum representing sms parameters which can be read from
	 * email body
	 *
	 */
	public enum SMSParameter {
		IDENTIFIER(0), TIMESTAMP(1), RECIPIENT(2), NATIONAL(3), BODY(4);
		
		private final int value;
		private SMSParameter(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}

	/**
	 * Number of SMS parameters from email body
	 */
	private static final int emailBodyLinesAmount = 5;
	
	/**
	 * Method which parsed received email and create SMS with its data
	 * @param message
	 * @return
	 */
	public SMS createSMSWithMailMessage(Message message) {
		String messageBody = null;
		String messageSubject = null;
		try {
			messageBody = getBodyText(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			messageSubject = message.getSubject();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		String lines[] = messageBody.split(System.getProperty("line.separator"));

		if (lines.length >= emailBodyLinesAmount && messageSubject.equals(MailWithSMSSubject)) {
			String id = lines[SMSParameter.IDENTIFIER.getValue()];
			String timestamp = lines[SMSParameter.TIMESTAMP.getValue()];
			String recipientNumber = lines[SMSParameter.RECIPIENT.getValue()];
			boolean isNational = Boolean.parseBoolean(lines[SMSParameter.NATIONAL.getValue()]);
			String smsBody = lines[SMSParameter.BODY.getValue()];
			for (int i = SMSParameter.BODY.getValue()+1; i<lines.length; i++) {
				smsBody.concat(lines[i]);
			}
			
			SMS createdSMS = new SMS(id, timestamp, recipientNumber, isNational, smsBody);
			return createdSMS;
		} else {
			return null;
		}
	}
	
	private String getBodyText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
		  String s = (String)p.getContent();
		  return s;
		}
		
		if (p.isMimeType("multipart/alternative")) {
		  // prefer html text over plain text
		  Multipart mp = (Multipart)p.getContent();
		  String text = null;
		  for (int i = 0; i < mp.getCount(); i++) {
		      Part bp = mp.getBodyPart(i);
		      if (bp.isMimeType("text/plain")) {
		          if (text == null)
		              text = getBodyText(bp);
		          continue;
		      } else if (bp.isMimeType("text/html")) {
		          String s = getBodyText(bp);
		          if (s != null)
		              return s;
		      } else {
		          return getBodyText(bp);
		      }
		  }
		  return text;
		} else if (p.isMimeType("multipart/*")) {
		  Multipart mp = (Multipart)p.getContent();
		  for (int i = 0; i < mp.getCount(); i++) {
		      String s = getBodyText(mp.getBodyPart(i));
		      if (s != null)
		          return s;
		  }
		}
		return null;
	}

}
