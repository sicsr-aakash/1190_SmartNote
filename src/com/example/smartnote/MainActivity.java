package com.example.smartnote;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
Button add_note, view_note;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/gillsans.ttf");
		add_note = (Button) findViewById(R.id.add_note);
		view_note = (Button) findViewById(R.id.view_note);
		add_note.setTypeface(tf);
		view_note.setTypeface(tf);
		
		add_note.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), AddNote.class);
				startActivity(intent);
			}
		});
		view_note.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getApplicationContext(), ViewNote.class);
				startActivity(intent);
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
