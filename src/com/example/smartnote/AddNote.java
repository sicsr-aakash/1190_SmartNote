package com.example.smartnote;


import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class AddNote extends ActionBarActivity {
Button add_note, select_image;
EditText title, body, tags;
String note_priority="",note_title="",note_body="",note_tags="", selectedImagePath=null, voiceResolver="notset";
Spinner priority_options;
ImageButton voice1, voice2, voice3,voice4;

private final int REQ_CODE_SPEECH_INPUT = 100;


DataHandler dbh;
AlarmSetter alarm;
//SharedPreferences pref;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_note);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/gillsans.ttf");
	//	pref=getApplicationContext().getSharedPreferences("AlarmSaver", 0);s
		add_note = (Button)findViewById(R.id.add_note_submit);
		select_image = (Button) findViewById(R.id.select_image);
		title = (EditText)findViewById(R.id.title_input);
		body = (EditText)findViewById(R.id.note_body);
		tags = (EditText)findViewById(R.id.tags_input);
		
		voice1 = (ImageButton)findViewById(R.id.voice_1);
		voice2 = (ImageButton)findViewById(R.id.voice_2);
		voice3 = (ImageButton)findViewById(R.id.voice_3);
		voice4 = (ImageButton)findViewById(R.id.voice_4);
		
		
		priority_options = (Spinner) findViewById(R.id.priority_options);
		alarm=new AlarmSetter();
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.priority_options,R.layout.spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		priority_options.setAdapter(adapter);
		add_note.setTypeface(tf);
		select_image.setTypeface(tf);
		
		select_image.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				//intent.setType("image/*");
				//intent.setAction(Intent.ACTION_GET_CONTENT);
				//startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
				startActivityForResult(intent, 1);
			}
		});
		
		
		voice1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				voiceResolver="title";
				promptSpeechInput();
				
			}
		});
		
		
		voice2.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							voiceResolver="priority";
							promptSpeechInput();
							
						}
		});
					
			voice3.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					voiceResolver="body";
					promptSpeechInput();
				}
			});
			
			voice4.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					voiceResolver="tags";
					promptSpeechInput();
					
				}
			});
		
		add_note.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//get all values entered in the add note layout.
				note_title = title.getText().toString();
				note_body = body.getText().toString();
				note_tags = tags.getText().toString();
				//Split tags over comma as they are comma separated.
				String[] array = note_tags.split(",");
				//trim all values after splitting.
				String[] tag_parts = new String[array.length];
				for(int i = 0; i<array.length;i++){
					tag_parts[i] = array[i].trim();
				}
				note_priority = priority_options.getSelectedItem().toString();
				if(note_title.equals("")){
					Toast.makeText(getApplicationContext(), "Title cannot be left blank.", Toast.LENGTH_SHORT).show();
				}
				else if(note_body.equals("")){
					Toast.makeText(getApplicationContext(), "Note Body cannot be left blank.", Toast.LENGTH_SHORT).show();
				}
				else if(note_tags.equals("")){
					Toast.makeText(getApplicationContext(), "Tags cannot be left blank.", Toast.LENGTH_SHORT).show();
				}
				else if(note_priority.equalsIgnoreCase("Enter your priority")){
					Toast.makeText(getApplicationContext(), "Please choose priority of Note.", Toast.LENGTH_SHORT).show();
				}
				else {
					dbh = new DataHandler(getBaseContext());
					dbh.open();
					if(selectedImagePath == null){
						selectedImagePath = "smartnote.png";
					}
					long rowid = dbh.insertData(note_title.trim(), note_priority, note_body.trim(), selectedImagePath, tag_parts);
					if(rowid != -1){
						Toast.makeText(getBaseContext(), "Note Saved", Toast.LENGTH_LONG).show();
					}
					else{
						Toast.makeText(getBaseContext(), "Note not saved due to some issue", Toast.LENGTH_LONG).show();
					}
					dbh.close();
					AlarmChecker ac = new AlarmChecker(AddNote.this, getApplicationContext(), body);
					if(!ac.setAlarm()){
						Intent intent = new Intent(getApplicationContext(), ViewNote.class);
						startActivity(intent);
						finish();
					}
					}
			}
		});
	}	
	
	private void promptSpeechInput() {
		//Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
 	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == 1){
			if(resultCode == Activity.RESULT_OK && data!=null){
				ImageChooser ic = new ImageChooser(getApplicationContext(), AddNote.this);
				selectedImagePath = ic.getImagePath(requestCode, resultCode, data);
				if(!selectedImagePath.equalsIgnoreCase(null)){
					Toast.makeText(getApplicationContext(), "Image choosen", Toast.LENGTH_SHORT).show();
					String[] AllPaths = selectedImagePath.split("/");
					String filename = AllPaths[(AllPaths.length-1)];
					select_image.setText(filename);
				}
			}
			else if(data==null){
				Toast.makeText(getApplicationContext(), "Either the action was cancelled by user or could not get the data.", Toast.LENGTH_SHORT).show();
			}
			else if(resultCode == Activity.RESULT_CANCELED)
			{
				Toast.makeText(getApplicationContext(), "No Image Choosen. Action cancelled by user.", Toast.LENGTH_SHORT).show();
			}
		}
		else if(requestCode == REQ_CODE_SPEECH_INPUT){
			if (resultCode == RESULT_OK && null != data) {
				ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				if(voiceResolver.equalsIgnoreCase("title")){
					//ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					title.setText(title.getText() + " " +result.get(0));
				}
				else if(voiceResolver.equalsIgnoreCase("body")){
	                //ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	                body.setText(body.getText() + " " +result.get(0));
				}
				else if(voiceResolver.equalsIgnoreCase("tags")){
	                //ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	                if(tags.getText().toString().equals("")){
	                	tags.setText(result.get(0));
	                }
	                else{
	                	tags.setText(tags.getText() + ", " +result.get(0));
	                }
				}
				else if(voiceResolver.equalsIgnoreCase("priority")){
					if((result.get(0).equalsIgnoreCase("hi")) || (result.get(0).equalsIgnoreCase("high"))){
						priority_options.setSelection(1);
					}
					else if(result.get(0).equalsIgnoreCase("medium")){
						priority_options.setSelection(2);
					}
					else if((result.get(0).equalsIgnoreCase("lo")) || (result.get(0).equalsIgnoreCase("yo")) || (result.get(0).equalsIgnoreCase("low"))){
						priority_options.setSelection(3);
					}
				}
            }
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_note, menu);
		return true;
	}
}