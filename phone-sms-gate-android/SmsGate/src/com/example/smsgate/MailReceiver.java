package com.example.smsgate;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import javax.mail.search.FlagTerm;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sun.mail.imap.IMAPFolder;

/**
 * E-mail receiving service.
 */
public class MailReceiver extends Service {

	/** Arbitrary tag for logging purposes. */
	static final String TAG = "MailReceiver";
	
	/** Indicates and controls service's thread state */
	private static boolean running = false;
	
	private String 
		/** IMAP server address */
		serv_addr,
		
		/** Login to mailbox */
		serv_login, 
		
		/** Password to mailbox */
		serv_pass;
	
	private int 
		/** IMAP server port */
		serv_port,
		
		/** Time between mailbox checks if server doesn't support IMAP IDLE */
		serv_delay;
	
	/** Listener which reacts on changes of messages count */
	private MessageCountListener msgListener;
	
	private Folder folder;
	
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
		serv_port = Integer.valueOf(prefs.getString("serv_port", "993"));
		serv_delay = Integer.valueOf(prefs.getString("serv_delay", "5"));
		Log.d(TAG, "MailReceiver service created");
		//ContactsManager.init(getApplicationContext());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "MailReceiver service started");
		setRunning(true);
		
		new Thread() {
			public void run() {
				try {
					Properties props = System.getProperties();
					Session session = Session.getInstance(props);
					Store store = session.getStore("imaps");
						
						Log.i(TAG, "Lacze...");
						store.connect(serv_addr, serv_port, serv_login, serv_pass);
						Log.i(TAG, "Polaczony");
						
						folder = store.getFolder("INBOX");
						if (folder == null || !folder.exists()) {
							Log.e(TAG, "Folder nie istnieje na serwerze! Koncze usluge");
							setRunning(false);
							return;
						}
						
						folder.open(Folder.READ_WRITE); // WRITE to set SEEN flags
						Log.i(TAG, "Folder otwarty, nasluchuje nowych wiadomosci");
						msgListener = new MsgCountListener();
					    folder.addMessageCountListener(msgListener);
						
					    // Manually notify MessageCountListener if any new message was on the mailbox
					    // before server had chance to tell us (e.g. before connection)
						if (folder.getUnreadMessageCount() != 0)
						{
							Message messages[] = folder.search(new FlagTerm(new Flags(Flag.SEEN), false));
							msgListener.messagesAdded(new MessageCountEvent(folder, MessageCountEvent.ADDED, false, messages));
						}
						
						// Check for new messages (periodically or using IDLE)
					    boolean supportsIdle = false;
					    try {
							if (folder instanceof IMAPFolder) {
							    IMAPFolder f = (IMAPFolder)folder;
							    f.idle();
							    supportsIdle = true;
							}
					    } catch (FolderClosedException fex) {
					    	throw fex;
					    } catch (MessagingException mex) {
					    	supportsIdle = false;
					    	Log.d(TAG, "IMAP IDLE not supported by server");
					    }
					    while(isRunning()) {
							if (supportsIdle && folder instanceof IMAPFolder) {
							    IMAPFolder f = (IMAPFolder)folder;
							    f.idle();
							    Log.d(TAG, "IDLE done");
							} else {
							    Thread.sleep(serv_delay*1000);
	
							    // This is to force the IMAP server to send us
							    // EXISTS notifications. 
							    folder.getMessageCount();
							    Log.d(TAG, "Refreshed message count");
							}
					    }
					    
				} catch (NoSuchProviderException e) {
					Log.e(TAG, "Problem z polaczeniem do serwera. Sprawdz ustawienia polaczenia");
				} catch (AuthenticationFailedException e) {
					Log.e(TAG, "Nieudane logowanie. Sprawdz login i haslo");
				} catch (MessagingException e) {
					Log.e(TAG, "Nieznany problem podczas polaczenia: " + e.getMessage());
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
		new Thread() {
			public void run() {
				if (folder != null && folder.isOpen()) {
					try {
						folder.close(false);
					} catch (MessagingException e) {
						Log.e(TAG, "Problem podczas zatrzymywania uslugi: " + e.getMessage());
					}
				}
			}
		}.start();
		setRunning(false);
		Log.d(TAG, "MailReceiver service destroyed");
	}
	
	static synchronized boolean isRunning() {
		return running;
	}
	
	static synchronized void setRunning(boolean state) {
		running = state;
	}
	
}


class MsgCountListener extends MessageCountAdapter
{
	private MessageManager messageManager = new MessageManager();
	
	public void messagesAdded(MessageCountEvent ev) {
	    Message[] msgs = ev.getMessages();
	    Log.i(MailReceiver.TAG,"Nowych wiadomosci: "+msgs.length);
	    messageManager.sendMessagesAsSMS(msgs);
	    
	}
}