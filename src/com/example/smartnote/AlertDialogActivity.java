package com.example.smartnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Window;

public class AlertDialogActivity extends Activity{
	Vibrator vibrate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vibrate.vibrate(5000);
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder
	        .setTitle("Alarm From SmartNote")
	        .setMessage(getIntent().getStringExtra("text"))
	        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	                vibrate.cancel();
	                finish();
	            }
	        });
	        
	    AlertDialog alert = builder.create();
	    alert.show();
	}
}
