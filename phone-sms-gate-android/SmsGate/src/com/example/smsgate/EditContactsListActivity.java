package com.example.smsgate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

class Contact
{
	String name;
	String phoneNumber;
	
	Contact() { name = ""; phoneNumber = ""; }
	
	Contact(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return name + ", " + phoneNumber;
	}
	
	
}

public class EditContactsListActivity extends Activity {

	static final String TAG = "EditContactsActivity";
	
	Vector<Contact> values;
	ArrayAdapter<Contact> aa;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list_edit);

		ListView lv = (ListView)findViewById(R.id.contactsList);
		values = new Vector<Contact>();
		aa = new ArrayAdapter<Contact>(this, android.R.layout.simple_list_item_1, values);
		lv.setAdapter(aa);
	}
	
	@Override
	protected void onStart() {
		try {
			FileInputStream fis = openFileInput("contacts.dat");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
			String buf1, buf2;
			values.clear();
			while (((buf1 = br.readLine()) != null) && ((buf2 = br.readLine()) != null))
				values.add(new Contact(buf1, buf2));
			
			br.close();
			fis.close();
			
			aa.notifyDataSetChanged();
			
			} catch (IOException e) {
				Log.e(TAG, "Problem z odczytaniem listy kontaktow");
			}
		super.onStart();
	}

	@Override
	protected void onStop() {
		try {
			FileOutputStream fos = openFileOutput("contacts.dat", Context.MODE_PRIVATE);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			
			for (Contact s : values) {
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
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		getMenuInflater().inflate(R.menu.contacts_list_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.contacts_list_add:
	        	LayoutInflater inflater = getLayoutInflater();
	        	View v = inflater.inflate(R.layout.edit_contacts_dlg, null);
	        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        	
	        	builder.setTitle("Dodaj kontakt");
	        	builder.setView(v);
	        	
	        	final EditText name = (EditText) v.findViewById(R.id.eName);
	        	final EditText number = (EditText) v.findViewById(R.id.ePhoneNumber);
	        	
	        	builder.setPositiveButton(R.string.edit_contacts_dlg_ok, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						values.add(new Contact(name.getText().toString(), number.getText().toString()));
						aa.notifyDataSetChanged();
					}
				});
	        	
	        	builder.setNegativeButton(R.string.edit_contacts_dlg_cancel, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						
					}
				});
	        	
	        	AlertDialog dialog = builder.create();
	        	dialog.show();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
