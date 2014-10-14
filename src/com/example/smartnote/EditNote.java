package com.example.smartnote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class EditNote extends ActionBarActivity {
	Spinner priority_options;
	EditText title, text, tags;
	Button submit, choose_image;
	File fname=null;
	String note_id, note_title, note_body, note_tags, note_priority, selectedImagePath=null, voiceResolver="notset";
	Boolean noteResolver=true;
	DataHandler dbh;
	ArrayList<String> result;
	ImageButton voice1, voice2, voice3, voice4;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		title = (EditText) findViewById(R.id.edit_title_input);
		text = (EditText) findViewById(R.id.edit_note_body);
		tags =  (EditText) findViewById(R.id.edit_tag_text);
		choose_image = (Button) findViewById(R.id.choose_image);
		submit = (Button)findViewById(R.id.edit_note_submit);
		priority_options = (Spinner) findViewById(R.id.priority_options);
		
		voice1 = (ImageButton)findViewById(R.id.edit_voice_1);
		voice2 = (ImageButton)findViewById(R.id.edit_voice_2);
		voice3 = (ImageButton)findViewById(R.id.edit_voice_3);
		voice4 = (ImageButton)findViewById(R.id.edit_voice_4);
		
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.priority_options,R.layout.spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		priority_options.setAdapter(adapter);
		result=getIntent().getExtras().getStringArrayList("result");
		title.setText(result.get(1));
		if(result.get(2).equalsIgnoreCase("high")){
			priority_options.setSelection(1);
		}
		else if(result.get(2).equalsIgnoreCase("medium")){
			priority_options.setSelection(2);
		}
		else if(result.get(2).equalsIgnoreCase("low")){
			priority_options.setSelection(3);
		}
		text.setText(result.get(3));
		selectedImagePath = result.get(4);
		int i=0;
		for(String tag: result){
			if(i>4){
				String prev = tags.getText().toString();
				if(prev.equals("")){
					tags.setText(tag);
				}
				else{
				tags.setText(prev + ", " +tag);
				}
			}
			else{
				i++;
			}
		}
		
		
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
							voiceResolver = "priority";
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

			
		choose_image.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				//intent.setType("image/*");
				//intent.setAction(Intent.ACTION_GET_CONTENT);
				//startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
				startActivityForResult(intent, 1);
			}
		});
		
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				note_id=result.get(0);
				note_title = title.getText().toString();
				note_body = text.getText().toString();
				note_tags = tags.getText().toString();
				String[] array = note_tags.split(",");
				String[] tag_parts = new String[array.length];
				for(int i = 0; i<array.length;i++){
					tag_parts[i] = array[i].trim();
					//System.out.println(tag_parts[i]);
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
				else{
				dbh = new DataHandler(getBaseContext());
				dbh.open();
				int result=dbh.updateNote(note_id, note_title.trim(), note_priority, note_body.trim(), selectedImagePath, tag_parts);
				if(result>0){
					Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
				}
				dbh.close();
				AlarmChecker ac = new AlarmChecker(EditNote.this, getApplicationContext(), text);
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
				ImageChooser ic = new ImageChooser(getApplicationContext(), EditNote.this);
				selectedImagePath = ic.getImagePath(requestCode, resultCode, data);
				if(!selectedImagePath.equalsIgnoreCase(null)){
					Toast.makeText(getApplicationContext(), "Image choosen", Toast.LENGTH_SHORT).show();
					String[] AllPaths = selectedImagePath.split("/");
					String filename = AllPaths[(AllPaths.length-1)];
					choose_image.setText(filename);
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
				
				if(voiceResolver.equalsIgnoreCase("title")){
					ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
					title.setText(title.getText() + " " +result.get(0));
				}
				else if(voiceResolver.equalsIgnoreCase("body")){
	                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	                text.setText(text.getText() + " " +result.get(0));
				}
				else if(voiceResolver.equalsIgnoreCase("tags")){
	                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
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
		getMenuInflater().inflate(R.menu.edit_note, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menu) {
		switch(menu.getItemId()){
		case R.id.delete:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Delete Confirmation").setMessage("Are you sure you want to delete this item?");
			alert.setPositiveButton("Yes", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface inter, int i) {
					String id = (result.get(0));
					dbh = new DataHandler(getApplicationContext());
					dbh.open();
					if(dbh.deleteNote(id) > 0){
						Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(getApplicationContext(), ViewNote.class);
						startActivity(intent);
						finish();
					}
					else{
						Toast.makeText(getApplicationContext(), "Perhaps something went wrong", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(getApplicationContext(), ViewNote.class);
						startActivity(intent);
						finish();
					}
					dbh.close();
				}
			})
			.setNegativeButton("No", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface inter, int i) {
					inter.cancel();
				}
			});
			AlertDialog dialog = alert.create();
			dialog.show();
			break;
		case R.id.save:
			String filename = result.get(1)+".csv";
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				File root = new File(Environment.getExternalStorageDirectory().getPath()+ "/SmartNote");
				root.mkdirs();
				fname = new File(root, filename);
				if(fname.exists()){
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Note Exists").setMessage("The specified note has already been saved. Would you like to sync the changes?");
					builder.setPositiveButton("Sync", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface di, int arg1) {
							fname.delete();
							noteResolver = true;
							saveFile(noteResolver);
						}
					});
					builder.setNegativeButton("No", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface d, int arg1) {
							d.cancel();
							noteResolver=false;
							saveFile(noteResolver);
						}
					});
					AlertDialog dialogbuilder = builder.create();
					dialogbuilder.show();
				}
				else{
					saveFile(true);
				}
			}
			else{
				Toast.makeText(getApplicationContext(), "No External Storage Found.", Toast.LENGTH_SHORT).show();
			}
			break;
		}
		return true;
	}
	public void saveFile(Boolean noteResolver){
		
		if(noteResolver){
		try{
			FileOutputStream fos=null;
			fos = new FileOutputStream(fname);
			String data = title.getText().toString()+", "+priority_options.getSelectedItem().toString()+", "+text.getText().toString()+", "+tags.getText().toString()+", "+selectedImagePath;
			fos.write(data.getBytes());
			fos.close();
			Toast.makeText(getApplicationContext(), "Note saved successfully!", Toast.LENGTH_SHORT).show();
		}
		catch(FileNotFoundException e){
			Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}	
		catch(IOException e){
			Toast.makeText(getApplicationContext(), "Oops! Something went wrong while saving to the file.", Toast.LENGTH_SHORT).show();
		}
	}
}
}