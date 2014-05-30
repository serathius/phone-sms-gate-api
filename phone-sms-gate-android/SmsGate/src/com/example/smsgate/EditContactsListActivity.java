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
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EditContactsListActivity extends Activity {

	static final String TAG = "EditContactsActivity";
	
	Vector<String> values;
	ArrayAdapter<String> aa;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_list_edit);

		ListView lv = (ListView)findViewById(R.id.contactsList);
		values = new Vector<String>();
		aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, values);
		lv.setAdapter(aa);
	}
	
	@Override
	protected void onStart() {
		try {
			FileInputStream fis = openFileInput("contacts.dat");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			
			String buf;
			values.clear();
			while ((buf = br.readLine()) != null)
				values.add(buf);
			
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
			
			for (String s : values) {
				bw.write(s);
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
	        	values.add("fourth");
	        	aa.notifyDataSetChanged();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
