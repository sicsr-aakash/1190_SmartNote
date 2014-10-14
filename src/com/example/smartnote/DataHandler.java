package com.example.smartnote;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHandler {
	//Setting all table names, column names and table creation query.
	public static final String TITLE = "note_title";
	public static final String PRIORITY = "note_priority";
	public static final String TEXT = "note_text";
	public static final String TAGS = "note_tags";
	public static final String TABLE_NAME_ADD_NOTE = "add_note";
	public static final String TABLE_NAME_TAGS = "tags";
	public static final String DATABASE_NAME = "SmartNote";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_CREATE_ADD_NOTE = "create table "+TABLE_NAME_ADD_NOTE+" ( note_id integer primary key, "+TITLE+" text, "+PRIORITY+" text, "+TEXT+" text, image_path text);";
	public static final String TABLE_CREATE_TAGS = "create table "+TABLE_NAME_TAGS+" ( tag_id integer primary key, note_tag_id integer, tags text, FOREIGN KEY(note_tag_id) REFERENCES "+TABLE_NAME_ADD_NOTE+"(note_id));";
	
	DataBaseHelper dbHelper;
	Context ctx;
	SQLiteDatabase db;
	public DataHandler(Context ctx){
		this.ctx=ctx;
		dbHelper = new DataBaseHelper(ctx);
	}
	
	private class DataBaseHelper extends SQLiteOpenHelper{
		
		public DataBaseHelper(Context ctx){
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(TABLE_CREATE_ADD_NOTE);
				db.execSQL(TABLE_CREATE_TAGS);
			}
			catch(SQLException e){
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//Drop table if they already exists when version is increased and recreate.
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_ADD_NOTE);
			db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME_TAGS);
			onCreate(db);
		}
		
	}
	
	//This method is used to populate add_note table and tags table. relationship between add_note and tags is 1:M
	public long insertData(String title, String priority, String text, String image_path, String[] tags){
		ContentValues content_add_note = new ContentValues();
		content_add_note.put(TITLE, title);
		content_add_note.put(PRIORITY, priority);
		content_add_note.put(TEXT, text);
		content_add_note.put("image_path", image_path);
		//inserting in parent row so that we can enter the value of foreign key column in tags table.
		long row_add_note = db.insertOrThrow(TABLE_NAME_ADD_NOTE, null, content_add_note);
		ContentValues content_tag = new ContentValues();
		for(String tag : tags){
			content_tag.put("tags", tag);
			content_tag.put("note_tag_id", (int)row_add_note);
			db.insertOrThrow(TABLE_NAME_TAGS, null, content_tag);
		}
		//To check if values are completely added.
		return row_add_note;
	}
	
	public DataHandler open(){
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public Cursor findAllNotes(){
		return db.query(TABLE_NAME_ADD_NOTE, new String[] {TITLE, PRIORITY, "image_path", TEXT}, null, null, null, null, null);
	}
	
	public ArrayList<String> updateClickedNote(String title, String body){
		ArrayList<String> result;
		String position="0";
		Cursor add_c = db.query(TABLE_NAME_ADD_NOTE, null, TITLE + " =? and " + TEXT + " =?", new String[]{title, body}, null, null, null);
		result=new ArrayList<String>();
		if(add_c.moveToFirst()){
			position = add_c.getString(add_c.getColumnIndex("note_id"));
			result.add(position);
			result.add(add_c.getString(add_c.getColumnIndex(TITLE)));
			result.add(add_c.getString(add_c.getColumnIndex(PRIORITY)));
			result.add(add_c.getString(add_c.getColumnIndex(TEXT)));
			result.add(add_c.getString(add_c.getColumnIndex("image_path")));
		}
		Cursor tag_c = db.query(TABLE_NAME_TAGS, new String[]{"tags"}, "note_tag_id=?", new String[]{position}, null, null, null);
		if(tag_c.moveToFirst()){
			do{
				result.add(tag_c.getString(tag_c.getColumnIndex("tags")));
			}while(tag_c.moveToNext());
		}
		return result;
	}
	
	public int updateNote(String id, String title, String priority, String text, String image_path, String[] tags){
		ContentValues newContent_add_note = new ContentValues();
		newContent_add_note.put(TITLE, title);
		newContent_add_note.put(PRIORITY, priority);
		newContent_add_note.put(TEXT, text);
		newContent_add_note.put("image_path", image_path);
		db.update(TABLE_NAME_ADD_NOTE, newContent_add_note, "note_id=?", new String[]{id});
		ContentValues newContent_tag = new ContentValues();
		int result=0;
		for(String tag : tags){
			newContent_tag.put("tags", tag);
			result= db.update(TABLE_NAME_TAGS, newContent_tag, "note_tag_id=?", new String[]{id});			
		}
		return result;
	}
	
	public int deleteNote(String id){
		int success_tag = db.delete(TABLE_NAME_TAGS, "note_tag_id = ?", new String[]{id});
		if(success_tag>0){
			return db.delete(TABLE_NAME_ADD_NOTE, "note_id = ?", new String[]{id});
		}
		return 0;
	}
	public Cursor findAllTags(){
		return db.query(TABLE_NAME_TAGS, new String[]{"note_tag_id", "tags"}, null, null, null, null, null);
	}
	
	public Cursor findSelectedNote(String note_tag_id){
		return db.query(TABLE_NAME_ADD_NOTE, null, "note_id=?", new String[]{note_tag_id}, null, null, null);
	}
	
	public ArrayList<String> findNoteId(String tag){
		Cursor c = db.query(TABLE_NAME_TAGS, new String[]{"note_tag_id"}, "tags=?", new String[]{tag}, null, null, null);
		ArrayList<String> result = new ArrayList<String>();
		if(c.moveToFirst()){
			do{
				if(!result.contains(c.getString(c.getColumnIndex("note_tag_id")))){
					result.add(c.getString(c.getColumnIndex("note_tag_id")));
				}
			}while(c.moveToNext());
			return result;
		}
		else
			return null;
	}
}
