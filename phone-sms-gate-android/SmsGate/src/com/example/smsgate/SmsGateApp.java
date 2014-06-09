package com.example.smsgate;

import android.app.Application;

public class SmsGateApp extends Application{

	@Override
	public void onCreate() {
		super.onCreate();
		ContactsManager.init(getApplicationContext());
		ContactsManager.getInstance();
	}

}

