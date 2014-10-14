package com.example.smartnote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class ImageChooser {
	Context c;
	Activity a;
	public ImageChooser(Context c, Activity a){
		this.c=c;
		this.a=a;
	}
	public String getImagePath(int requestCode, int resultCode, Intent data){
		String selectedImagePath=null;
        	Uri selectedImageUri = data.getData();
            selectedImagePath = getPath(selectedImageUri);
            return selectedImagePath;
	}
	public String getPath(Uri uri) {
		String res = null;
        if( uri == null ) {
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = a.getContentResolver().query(uri, null, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res =  cursor.getString(column_index);
            cursor.close();
        }
        else{
        	res = uri.getPath();
        }
        return res;
	}
}
