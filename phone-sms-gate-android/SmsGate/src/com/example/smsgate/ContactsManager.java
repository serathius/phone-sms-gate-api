package com.example.smsgate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

import android.content.Context;
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
	
    private void readAndroidContacts()
    {
    	
    }
}
