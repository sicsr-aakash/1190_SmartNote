package com.example.smartnote;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;
import android.widget.Toast;

public class AlarmChecker {
	Activity activity;
	Context c;
	EditText body;
	SharedPreferences pref;
	int day, month, year, hour, minutes;
	Calendar  cal;
	public AlarmChecker(Activity activity, Context c, EditText body){
		this.activity = activity;
		this.c = c;
		this.body = body;
		pref = c.getSharedPreferences("AlarmSaver", 0);
		cal = Calendar.getInstance();
	}
	
	public boolean setAlarm(){
		year = cal.get(Calendar.YEAR);
		month = cal.get(Calendar.MONTH);
		day = cal.get(Calendar.DAY_OF_MONTH);
		hour = cal.get(Calendar.HOUR_OF_DAY);
		minutes = cal.get(Calendar.MINUTE);
		try
		{
		if((body.getText().toString().matches(".*@time[ ][0-9]{2}:[0-9]{2}.*")) && !(body.getText().toString().matches(".*@date[ ][0-9]{2}[-./]{1}[0-9]{2}[-./]{1}[0-9]{2}.*"))){
			String[] parts = body.getText().toString().split("@time", 2);
			String[] time;
			if(parts.length>1){
				time = parts[1].split(":", 2);
				hour = Integer.parseInt(time[0].substring((time[0].length()-2), time[0].length()));
				minutes = Integer.parseInt(time[1].substring(0, 2));
				if(checkRange(hour, minutes, day, (month+1), year)){
					createDialog(1,1,0,0,0);
					return true;
				}
				else{
					Toast.makeText(c, "Invalid Date/Time. Alarm not Set", Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}
		else if(body.getText().toString().matches(".*@time[ ][0-9]{2}:[0-9]{2}.*@date[ ][0-9]{2}[-./]{1}[0-9]{2}[-./]{1}[0-9]{2}.*")){
			String[] parts = body.getText().toString().split("@time", 2);
			if(parts.length > 1){
				String[] time = parts[1].split(":", 2);
				hour = Integer.parseInt(time[0].substring((time[0].length()-2), (time[0].length())));
				minutes = Integer.parseInt(time[1].substring(0, 2));
				String[] date_temp = time[1].split("@date", 2);
				String[] date= date_temp[1].split("[./-]", 3);
				day = Integer.parseInt(date[0].substring((date[0].length()-2), date[0].length()));
				month = Integer.parseInt(date[1])-1;
				year = Integer.parseInt(date[2].substring(0, 2));
				if(checkRange(hour, minutes, day, (month+1), year)){
					createDialog(1,1,1,1,1);
					return true;
				}
				else {
					Toast.makeText(c, "Invalid Date/Time. Alarm not Set", Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}
		else if(body.getText().toString().matches(".*@date[ ][0-9]{2}[-./]{1}[0-9]{2}[-./]{1}[0-9]{2}.*@time[ ][0-9]{2}:[0-9]{2}.*")){
			String parts[] = body.getText().toString().split("@time",2);
			if(parts.length > 1){
				String[] date_temp = parts[0].split("@date", 2);
				String[] date = date_temp[1].split("[./-]", 3);
				day = Integer.parseInt(date[0].substring((date[0].length()-2), date[0].length()));
				month = Integer.parseInt(date[1])-1;
				year = Integer.parseInt(date[2].substring(0, 2));
				String[] time = parts[1].split(":",2);
				hour = Integer.parseInt(time[0].substring((time[0].length()-2), (time[0].length())));
				minutes = Integer.parseInt(time[1].substring(0, 2));
				if(checkRange(hour, minutes, day, (month+1), year)){
					createDialog(1,1,1,1,1);
					return true;
				}
				else{
					Toast.makeText(c, "Invalid Date/Time. Alarm not Set.", Toast.LENGTH_LONG).show();
					return false;
				}
			}
		}
		else if((body.getText().toString().matches(".*@time.*")) || (body.getText().toString().matches(".*@date.*"))){
			Toast.makeText(c, "Alarm Detected but your time/date format seems to be wrong. Alarm not Set.", Toast.LENGTH_LONG).show();
			return false;
		}
		}
		catch(NumberFormatException e){
			Toast.makeText(c, "Alarm Detected but your time/date format seems to be wrong. Alarm not Set.", Toast.LENGTH_LONG).show();
		}
		return false;
	}
	
	public boolean checkRange(int hour, int minutes, int day, int month, int year){
		if((hour>=0 && hour<=24) && (minutes >=0 && minutes <= 60)){
			GregorianCalendar cal = new GregorianCalendar();
			if(cal.isLeapYear(year)){
				if(month == 2){
					if(day>0 && day<=29){
						return true;
					}
					else
						return false;
				}
				else if((month==1 || month==3 || month==5 || month==7 || month==8 || month==10 || month==12)){
					if(day >=1 && day<=31){
						return true;
					}
					else
						return false;
				}
				else if((month==4 || month==6 || month==9 || month==11)){
					if(day >=1 && day<=30){
						return true;
					}
					else
						return false;
				}
				else
					return false;
			}
			else{
				if(month == 2){
					if(day>0 && day<=28){
						return true;
					}
					else
						return false;
				}
				else if((month==1 || month==3 || month==5 || month==7 || month==8 || month==10 || month==12)){
					if(day >=1 && day<=31){
						return true;
					}
					else
						return false;
				}
				else if((month==4 || month==6 || month==9 || month==11)){
					if(day >=1 && day<=30){
						return true;
					}
					else
						return false;
				}
				else
					return false;
			}
		}
		else
			return false;
	}
	
	public void createDialog(int hours, int minute, int days, int months, int years){
		if(hours==1 && minute==1 && days==0 && months==0 && years==0){
			year = cal.get(Calendar.YEAR);
		}
		else{
			year = year+2000;
		}
		AlertDialog.Builder builder =new AlertDialog.Builder(activity);
		builder.setTitle("Set Alarm").setMessage("Do you want to set an alarm?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cal.set(year, month, day, hour, minutes, 0);
				AlarmManager am=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
			    Intent intent = new Intent(c, AlarmSetter.class);
			    intent.putExtra("text", body.getText().toString());
			    int code = pref.getInt("id", 0);
			    Editor editor = pref.edit();
			    editor.putInt("id", (code+1));
			    editor.commit();
			    PendingIntent pi = PendingIntent.getBroadcast(c, code, intent, 0);
			    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
			   	Toast.makeText(c, "Alarm Set", Toast.LENGTH_SHORT).show();
			    finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {	
				dialog.cancel();
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public void finish(){
		activity.finish();
	}
}
