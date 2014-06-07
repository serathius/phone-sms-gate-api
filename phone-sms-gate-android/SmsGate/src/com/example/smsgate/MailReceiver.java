package com.example.smsgate;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * E-mail receiving service.
 * 
 *
 */
public class MailReceiver extends Service {
	
	/** Arbitrary tag for logging purposes. */
	static final String TAG = "MailReceiver";
	
	boolean running = false;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
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
						store.connect("212.77.101.140",993,"login","pass"); //imap.wp.pl, login and pass to change
						
						Folder folder = store.getFolder("INBOX");
						folder.open(Folder.READ_ONLY);
						Log.i(TAG,"Nowych wiadomosci: "+folder.getNewMessageCount());
						store.close();
						Thread.sleep(5000);
					}
				} catch (NoSuchProviderException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
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
