package com.example.smartnote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ButtonAdapter extends BaseAdapter{
	private Context mContext;
	private String[] values, priority, image, body;
	private int resourceId;
	
	public ButtonAdapter(Context c, int resourceId, String[] values, String[] priority, String[] image, String[] body){
		mContext=c;
		this.resourceId=resourceId;
		this.values = values;
		this.priority = priority;
		this.image = image;
		this.body = body;
	}
	
	public int getCount(){
		return values.length;
	}
	
	public Object getItem(int position){
		return null;
	}
	
	public long getItemId(int position){
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Typeface tf = Typeface.createFromAsset(mContext.getAssets(),"fonts/gillsans.ttf");
		TextView txt = null, txt2=null;
		ImageView image_view;
		View row = convertView;
		if(row==null){
			LayoutInflater inflater = LayoutInflater.from(mContext);
			row=inflater.inflate(resourceId, parent, false);
			txt = (TextView)row.findViewById(R.id.row_text_view);
			txt2 = (TextView)row.findViewById(R.id.row_text_view2);
			image_view = (ImageView) row.findViewById(R.id.row_image);
			txt.setText(values[position]);
			txt2.setText(body[position]);
			if(image[position].equals("smartnote.png")){
				image_view.setImageResource(R.drawable.smart_logo_corp);
			}
			else{
				Bitmap bmp= BitmapFactory.decodeFile(image[position]);
				image_view.setImageBitmap(bmp);
			}
			if(priority[position].equalsIgnoreCase("high")){
				txt.setBackgroundColor(Color.rgb(230, 25, 44));
				txt2.setBackgroundColor(Color.rgb(230, 25, 44));
				image_view.setBackgroundColor(Color.rgb(230, 25, 44));
			}
			else if(priority[position].equalsIgnoreCase("medium")){
				txt.setBackgroundColor(Color.rgb(235, 137, 33));
				txt2.setBackgroundColor(Color.rgb(235, 137, 33));
				image_view.setBackgroundColor(Color.rgb(235, 137, 33));
			}
			else if(priority[position].equalsIgnoreCase("low")){
				txt.setBackgroundColor(Color.rgb(85, 212, 63));
				txt2.setBackgroundColor(Color.rgb(85, 212, 63));
				image_view.setBackgroundColor(Color.rgb(85, 212, 63));
			}
			txt.setTypeface(tf);
			row.setTag(txt);
			row.setTag(image);
			row.setTag(txt2);
			notifyDataSetChanged();
		}
		else{
			row.getTag();
		}
		return row;
	}
}

