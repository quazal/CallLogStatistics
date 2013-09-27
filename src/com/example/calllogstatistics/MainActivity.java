package com.example.calllogstatistics;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ArrayList<String> conNames;
	private ArrayList<String> conNumbers;
	private ArrayList<Integer> conTime;
	private ArrayList<Integer> conCallIn;
	private ArrayList<Integer> conCallOut;
	private ArrayList<Integer> conTextIn;
	private ArrayList<Integer> conTextOut;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Master List
		conNames = new ArrayList<String>();
		conNumbers = new ArrayList<String>();
		conTime = new ArrayList<Integer>();
		conCallIn = new ArrayList<Integer>();
		conCallOut = new ArrayList<Integer>();
		conTextIn = new ArrayList<Integer>();
		conTextOut = new ArrayList<Integer>();
		String tempName = "";
		//Various totals
		int index = 0;
		int totalCallIn = 0;
		int totalCallOut = 0;
		int totalTalkTime = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		int totalSMSin = 0;
		int totalSMSout = 0;
		//Textview manipulators
		TextView textViewCallIn = (TextView) findViewById(R.id.TInCall);
		TextView textViewCallOut = (TextView) findViewById(R.id.TOutCall);
		TextView textViewCallTot = (TextView) findViewById(R.id.TCall);
		TextView textViewTime = (TextView) findViewById(R.id.TTalkTime);
		TextView textViewTextIN = (TextView) findViewById(R.id.textIN);
		TextView textViewTextOUT = (TextView) findViewById(R.id.textOUT);
		TextView textViewTextTOT = (TextView) findViewById(R.id.textTOT);
		TextView textViewTopCallIN = (TextView) findViewById(R.id.topCallerIN);
		TextView textViewTopCallNumIN = (TextView) findViewById(R.id.topCallerNumIN);
		TextView textViewTopCallOUT = (TextView) findViewById(R.id.topCallerOUT);
		TextView textViewTopCallNumOUT = (TextView) findViewById(R.id.topCallerNumOUT);
		TextView textViewTopTextIN = (TextView) findViewById(R.id.topTextIN);
		TextView textViewTopTextINnum = (TextView) findViewById(R.id.topTextINnum);
		TextView textViewTopTextOut = (TextView) findViewById(R.id.topTextOUT);
		TextView textViewTopTextOutNum = (TextView) findViewById(R.id.topTextOUTnum);
		

		//Pull Calls
		Cursor curLog = getAllLogs.getAllCallLogs(getContentResolver());

		//Pull SMS
		Cursor curSmsLog = getAllLogs.getAllSMSLogs(getContentResolver()); 
		
		//Populate Arraylists Call log portion
		for (curLog.moveToFirst(); !curLog.isAfterLast(); curLog.moveToNext()) {
			tempName = "";
			//Grab the "Name" of the entry
			if (curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME)) == null){
				tempName = curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
				if (tempName.length() == 11)
					tempName = tempName.substring(1, tempName.length());
				if (tempName.length() == 12)
					tempName = tempName.substring(2, tempName.length());
					
			}
			else
				tempName = curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
			//Check number against current list, find index or add entry at end.
			if (conNumbers.contains(curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.NUMBER))))
				//find existing entry
				index = conNumbers.indexOf(curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.NUMBER)));
			else {
				//If not found, make a new entry and grab index
				conNames.add(tempName);
				conNumbers.add(curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.NUMBER)));
				conTime.add(0);
				conCallIn.add(0);
				conCallOut.add(0);
				conTextIn.add(0);
				conTextOut.add(0);
				index = conNumbers.indexOf(curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.NUMBER)));
			}
			//Set the rest of the fields
			//Add the call duration to the total time.
			conTime.set(index, conTime.get(index) + 
					Integer.parseInt(curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.DURATION))));
			//Check Call type
			String callType = curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.TYPE));
			if (callType.equals("1"))
				conCallIn.set(index, conCallIn.get(index) + 1);
			else
				conCallOut.set(index, conCallOut.get(index) + 1);
			
			//Go to next entry
		}
		
		//Process SMS log and add values to existing data
		for (curSmsLog.moveToFirst(); !curSmsLog.isAfterLast(); curSmsLog.moveToNext()) {
			tempName = "";
			//Grab first entry and format it properly
			tempName = curSmsLog.getString(curSmsLog.getColumnIndex("address"));
			//Cut any leading + or 1's out of the number
			if (tempName.length() == 11)
				tempName = tempName.substring(1, tempName.length());
			if (tempName.length() == 12)
				tempName = tempName.substring(2, tempName.length());
			//check against current list
			if (conNames.contains(tempName))
				index = conNumbers.indexOf(tempName);
			else {
				//If not found, make a new entry and grab index
				conNames.add(tempName);
				conNumbers.add(tempName);
				conTime.add(0);
				conCallIn.add(0);
				conCallOut.add(0);
				conTextIn.add(0);
				conTextOut.add(0);
				index = conNumbers.indexOf(tempName);
			}
			//check type of message(sent/rec) .
			String smsType = curSmsLog.getString(curSmsLog.getColumnIndex("type"));
			if(smsType.equals("1"))
				conTextIn.set(index, conTextIn.get(index) + 1);
			else
				conTextOut.set(index, conTextOut.get(index) + 1);
			//Next Record
		}
		
		//Grab totals
		for(int i=0; i < conNumbers.size(); i++) {
			totalCallIn += conCallIn.get(i);
			totalCallOut += conCallOut.get(i);
			totalTalkTime += conTime.get(i);
			totalSMSin += conTextIn.get(i);
			totalSMSout += conTextOut.get(i);
		}
		seconds = totalTalkTime % 60;
		minutes = (totalTalkTime / 60) % 60;
		hours = (totalTalkTime / 60) / 60;

		//Find top people.
		int tempIndexTI = 0;
		int tempIndexTO = 0;
		int tempIndexCI = 0;
		int tempIndexCO = 0;
		for(int i=1; i < conNumbers.size(); i++) {
			if(conTextIn.get(i) > conTextIn.get(tempIndexTI))
				tempIndexTI = i;
			if(conTextOut.get(i) > conTextOut.get(tempIndexTO))
				tempIndexTO = i;
			if(conCallIn.get(i) > conCallIn.get(tempIndexCI))
				tempIndexCI = i;
			if(conCallOut.get(i) > conCallOut.get(tempIndexCO))
				tempIndexCO = i;
		}
		
		
		
		
		//Output the data to screen
		textViewCallIn.setText(Integer.toString(totalCallIn));
		textViewCallOut.setText(Integer.toString(totalCallOut));
		textViewCallTot.setText(Integer.toString(totalCallIn + totalCallOut));
		textViewTime.setText(Integer.toString(hours) + ":" + Integer.toString(minutes) + ":" + Integer.toString(seconds));
		textViewTextIN.setText(Integer.toString(totalSMSin));
		textViewTextOUT.setText(Integer.toString(totalSMSout));
		textViewTextTOT.setText(Integer.toString(totalSMSin + totalSMSout));
		
		textViewTopCallIN.setText(conNames.get(tempIndexCI));
		textViewTopCallNumIN.setText(Integer.toString(conCallIn.get(tempIndexCI)));
		textViewTopCallOUT.setText(conNames.get(tempIndexCO));
		textViewTopCallNumOUT.setText(Integer.toString(conCallOut.get(tempIndexCO)));
		
		textViewTopTextIN.setText(conNames.get(tempIndexTI));
		textViewTopTextINnum.setText(Integer.toString(conTextIn.get(tempIndexTI)));
		textViewTopTextOut.setText(conNames.get(tempIndexTO));
		textViewTopTextOutNum.setText(Integer.toString(conTextOut.get(tempIndexTO)));
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	


	
}
