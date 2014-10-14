package com.example.smartnote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmSetter extends BroadcastReceiver {

	@Override
	public void onReceive(Context c, Intent i) {
		Intent intent = new Intent(c, AlertDialogActivity.class);
		intent.putExtra("text", i.getStringExtra("text"));
		Toast.makeText(c, i.getStringExtra("text"), Toast.LENGTH_SHORT).show();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity(intent);
	}
}
