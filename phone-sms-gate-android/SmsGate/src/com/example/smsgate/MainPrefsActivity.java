package com.example.smsgate;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class MainPrefsActivity extends Activity{

	static class SettingsFragment extends PreferenceFragment {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.main_prefs_screen);
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
		
		super.onCreate(savedInstanceState);
	}
	
	

}
