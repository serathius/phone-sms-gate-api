package com.example.smsgate;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * E-mail receiving service.
 * 
 *
 */
public class MailReceiver extends Service {
	
	/** Arbitrary tag for logging purposes. */
	static final String TAG = "MailReceiver";
	
	/** Indicates and controls service's thread state */
	boolean running = false;
	
	String serv_addr, serv_login, serv_pass;
	int serv_port;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());	
		serv_addr = prefs.getString("serv_address", "");
		serv_login = prefs.getString("serv_login", "");
		serv_pass = prefs.getString("serv_pass", "");
		serv_port = Integer.valueOf(prefs.getString("serv_port", ""));
		Log.d(TAG, "MailReceiver service created");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "MailReceiver service started");
		running = true;
		
		new Thread() {
			public void run() {
				try {
					Properties props = System.getProperties();
					Session session = Session.getInstance(props);
					Store store = session.getStore("imaps");
						
					while (running) {
						Log.i(TAG, "Lacze...");
						store.connect(serv_addr, serv_port, serv_login, serv_pass);
						
						Folder folder = store.getFolder("INBOX");
						folder.open(Folder.READ_ONLY);
						Log.i(TAG,"Nowych wiadomosci: "+folder.getNewMessageCount());
						store.close();
						Thread.sleep(5000);
					}
				} catch (NoSuchProviderException e) {
					Log.e(TAG, "Problem z polaczeniem do serwera. Sprawdz ustawienia polaczenia");
				} catch (AuthenticationFailedException e) {
					Log.e(TAG, "Nieudane logowanie. Sprawdz login i haslo");
				} catch (MessagingException e) {
					Log.e(TAG, "Nieznany problem podczas polaczenia: ", e);
				} catch (InterruptedException e) {
					Log.d(TAG, "Thread sleep interrupted");
				}
			}
		}.start();
		return super.onStartCommand(intent, flags, startId);
	}
		
	@Override
	public void onDestroy() {
		super.onDestroy();
		running = false;
		Log.d(TAG, "MailReceiver service destroyed");
	}
}
