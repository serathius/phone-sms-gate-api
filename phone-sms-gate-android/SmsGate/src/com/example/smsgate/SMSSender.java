package com.example.smsgate;

import android.telephony.SmsManager;
import datamodel.SMS;

public class SMSSender {

	public void sendSMS(SMS sms) {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(sms.getRecipient(), null, sms.getBody(), null, null);
	}
	
}
