package com.example.smartnote;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


public class ViewNote extends ActionBarActivity {
	DataHandler dbh;
	GridView gridview;
	TextView no_notes;
	String[] note_title, note_priority, note_image, text;
	ButtonAdapter adapter;
	EditText editSearch;
	ArrayList<String> tags, note_tag_id;
	Cursor c;
	Boolean set = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_note);
		gridview = (GridView) findViewById(R.id.gridview1);
		no_notes = (TextView) findViewById(R.id.no_notes);
		editSearch = (EditText) findViewById(R.id.edit_search);
		editSearch.setVisibility(View.GONE);
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/gillsans.ttf");
		no_notes.setTypeface(tf);
		no_notes.setTextColor(Color.WHITE);
		dbh = new DataHandler(getBaseContext());
		dbh.open();
		c = dbh.findAllNotes();
		if(c.getCount() > 0){
			no_notes.setVisibility(View.GONE);
		}
		note_title = new String[c.getCount()];
		note_priority = new String[c.getCount()];
		note_image= new String[c.getCount()];
		text = new String[c.getCount()];
		int i=0;
		if(c.moveToFirst()){
			do{
				note_title[i] = c.getString(0);
				note_priority[i] = c.getString(1);
				note_image[i] = c.getString(2);
				text[i] = c.getString(3);
				i++;
			}
			while(c.moveToNext());
		}
		dbh.close();
		adapter=null;
		adapter = new ButtonAdapter(getApplicationContext(), R.layout.view_row_grid, note_title, note_priority, note_image, text);
		adapter.notifyDataSetChanged();
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
				ArrayList<String> result;
				DataHandler handler=new DataHandler(getApplicationContext());
				handler.open();
				result = handler.updateClickedNote(note_title[position], text[position]);
				Intent intent = new Intent(getApplicationContext(), EditNote.class);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("result", result);
				intent.putExtras(bundle);
				handler.close();
				startActivity(intent);
				finish();
			}
			
		});
}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_note, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.add:
			Intent intent = new Intent(getApplicationContext(), AddNote.class);
			startActivity(intent);
			finish();
			break;
		case R.id.search:
			if(editSearch.getVisibility()==View.GONE){
				editSearch.setVisibility(View.VISIBLE);
			}
			else{
				editSearch.setVisibility(View.GONE);
			}
			DataHandler dh = new DataHandler(getApplicationContext());
			dh.open();
			Cursor tag = dh.findAllTags();
			tags = new ArrayList<String>();
			note_tag_id = new ArrayList<String>();
			if(tag.moveToFirst()){
				do{
					tags.add(tag.getString(tag.getColumnIndex("tags")));
					note_tag_id.add(tag.getString(tag.getColumnIndex("note_tag_id")));
				}while(tag.moveToNext());
			}
			dh.close();
			editSearch.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence c, int start, int before, int count) {
					ArrayList<String> id = new ArrayList<String>();
					if(!c.toString().equalsIgnoreCase("")){
						for(String tag : tags){
							if(tag.toLowerCase(Locale.getDefault()).startsWith(c.toString().toLowerCase())){
								set=false;
								DataHandler dh = new DataHandler(getApplicationContext());
								dh.open();
								id = dh.findNoteId(tag);
								note_title = new String[id.size()];
								note_priority = new String[id.size()];
								note_image= new String[id.size()];
								text = new String[id.size()];
								int j=0;
								if(id.size()!=0){
									for(String note_tag : id){
										Cursor note = dh.findSelectedNote(note_tag);
										if(note.moveToFirst()){
											do{
												note_title[j]=note.getString(note.getColumnIndex("note_title"));
												note_priority[j]=note.getString(note.getColumnIndex("note_priority"));
												text[j]=note.getString(note.getColumnIndex("note_text"));
												note_image[j]=note.getString(note.getColumnIndex("image_path"));
												j++;
											}	
											while(note.moveToNext());
										}
									}	
									adapter=null;
									adapter = new ButtonAdapter(getApplicationContext(), R.layout.view_row_grid, note_title, note_priority, note_image, text);
									gridview.setAdapter(adapter);
								}
								dh.close();
							}
						}
						if(set){
							Toast.makeText(getApplicationContext(), "No Tags matching found", Toast.LENGTH_SHORT).show();
							set=false;
						}
					}
					else{
						set=true;
						DataHandler dh = new DataHandler(getApplicationContext());
						dh.open();
						Cursor all = dh.findAllNotes();
						note_title = new String[all.getCount()];
						note_priority = new String[all.getCount()];
						note_image= new String[all.getCount()];
						text = new String[all.getCount()];
						int i=0;
						if(all.moveToFirst()){
							do{
								note_title[i] = all.getString(0);
								note_priority[i] = all.getString(1);
								note_image[i] = all.getString(2);
								text[i] = all.getString(3);
								i++;
							}
							while(all.moveToNext());
						}
						dh.close();
						adapter=null;
						adapter = new ButtonAdapter(getApplicationContext(), R.layout.view_row_grid, note_title, note_priority, note_image, text);
						gridview.setAdapter(adapter);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					
				}
			});
			break;
		case R.id.aboutus:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("About Us").setMessage("Made By:\nVaibhav Jain\nSaurabh Hebbalkar");
			builder.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
			break;
		case R.id.instruction:
			Intent i = new Intent(getApplicationContext(), Instructions.class);
			startActivity(i);
			break;
		}
		return true;
	}
}
