package com.example.smsgate;

import javax.mail.Address;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;
import datamodel.SMS;

public class SMSSender {

	public void sendSMSWithResponseAddress(SMS sms, Address responseAddress) {
		//TODO : Add receiving sms status and sending it by email to responseAddress
	
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(sms.getRecipient(), null, sms.getBody(), null, null);
	}
	
}
