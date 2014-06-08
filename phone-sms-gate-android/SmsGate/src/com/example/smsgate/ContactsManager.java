package com.example.smsgate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;

/**
 * Simple class representing one contact - pair consisted
 *  of name and phone number.
 */
class Contact
{
	/** Name of contact */
	String name;
	
	/** Phone number of contact */
	String phoneNumber;
	
	/** Default constructor, sets both fields to empty string */
	Contact() { name = ""; phoneNumber = ""; }
	
	/**
	 * Constructor of Contact objects
	 * @param name Name of contact
	 * @param phoneNumber Phone number of contact
	 */
	Contact(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return name + ", " + phoneNumber;
	}
	
}

public class ContactsManager {

	private final static String TAG = "ContactsManager";
	private static Context context;
    private static ContactsManager cm;
  
    /** List of user defined contacts */ 
	Vector<Contact> myContacts;
	
	/** List of android contacts */
	Vector <Contact> androidContacts;
    
    public static synchronized ContactsManager getInstance(Context context) {
    	ContactsManager.context = context.getApplicationContext();
    	if(cm == null){
            cm = new ContactsManager();
        }
        return cm;
    }
    
    private ContactsManager() {
    	myContacts = new Vector<Contact>();
    	androidContacts = new Vector<Contact>();
    	readMyContacts();
    	readAndroidContacts();
    }
    
    private void readMyContacts() {
		try {
			FileInputStream fis = context.openFileInput("contacts.dat");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
			String buf1, buf2;
			myContacts.clear();
			while (((buf1 = br.readLine()) != null) && ((buf2 = br.readLine()) != null))
				myContacts.add(new Contact(buf1, buf2));
			
			br.close();
			fis.close();
			
			} catch (IOException e) {
				Log.e(TAG, "Problem z odczytaniem listy kontaktow");
			}
    }
    
    public void updateMyContacts() {
		try {
			FileOutputStream fos = context.openFileOutput("contacts.dat", Context.MODE_PRIVATE);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			for (Contact s : myContacts) {
				bw.write(s.name);
				bw.newLine();
				bw.write(s.phoneNumber);
				bw.newLine();
			}
					
			bw.close();
			fos.close();
			
			} catch (IOException e) {
				Log.e(TAG, "Problem z zapisaniem listy kontaktow");
			}
    }
	
    private void readAndroidContacts() {
    	ContentResolver cr = context.getContentResolver();
    	Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
    	
    	if (cur.getCount() > 0) {
    	    while (cur.moveToNext()) {
    	        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
    	        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
    	        
    	        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
    	             Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
    	                                     null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
    	                                     new String[]{id}, null);
    	             
    	             while (pCur.moveToNext()) {
    	                  int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
    	                  String phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    	                  switch (phoneType) {
    	                        case Phone.TYPE_MOBILE:
    	                            androidContacts.add(new Contact(name + " M", parseNumber(phoneNumber)));
    	                            break;
    	                        case Phone.TYPE_HOME:
    	                        	androidContacts.add(new Contact(name + " H", parseNumber(phoneNumber)));
    	                            break;
    	                        case Phone.TYPE_WORK:
    	                        	androidContacts.add(new Contact(name + " W", parseNumber(phoneNumber)));
    	                            break;
    	                        case Phone.TYPE_OTHER:
    	                        	androidContacts.add(new Contact(name + " O", parseNumber(phoneNumber)));
    	                            break;                                  
    	                        default:
    	                            break;
    	                  }
    	              } 
    	              pCur.close();
    	        }
    	    }
    	}
    }
    
    private String parseNumber(String number) {
    	String parsedNumber = new String();
    	
    	for (char c : number.toCharArray())
    		if ((c == '+') || ((c >= '0') &&(c <='9')))
    			parsedNumber += c;
    	
    	return parsedNumber;
    }
    
    public boolean checkPhoneNumber(String number) {
    	Vector<Contact> source;
    	
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    	if (prefs.getBoolean("check_contact_list", true))
    		source = myContacts; // check for number in myContacts
    	else
    		source = androidContacts; // check for number in androidContacts
    	
    	for (Contact c: source)
    	if (c.phoneNumber.equals(number))
    		return true;
    	
    	return false;
    }
    
}
