package com.example.smsgate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	Button bRun, bStop;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bStop = (Button)findViewById(R.id.bStop);
        bRun = (Button)findViewById(R.id.bRun);
        
        if (MailReceiver.isRunning()) {
        	bStop.setEnabled(true);
        	bRun.setEnabled(false);
        }
        else {
        	bStop.setEnabled(false);
        	bRun.setEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	switch (item.getItemId())
    	{
    	case R.id.menu_settings:
    		startActivity(new Intent(this, MainPrefsActivity.class));
    		return true;
    	default:
    		return super.onMenuItemSelected(featureId, item);
    	}
    }
    
    public void onRunClick(View v)
    {
    	startService(new Intent(this, MailReceiver.class));
    	bRun.setEnabled(false);
    	bStop.setEnabled(true);
    }
    
    public void onStopClick(View v)
    {
    	stopService(new Intent(this, MailReceiver.class));
    	bRun.setEnabled(true);
    	bStop.setEnabled(false);
    }
}
