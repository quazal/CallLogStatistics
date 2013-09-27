package com.example.calllogstatistics;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;


public class getAllLogs {
	
	//Pull Call Logs
	public static Cursor getAllCallLogs(ContentResolver cr) {
		// reading all data in descending order according to DATE
		String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
		Uri callUri = Uri.parse("content://call_log/calls");
		Cursor curCallLogs = cr.query(callUri, null, null, null, strOrder);

		return curCallLogs;
	}

	
	//Pull SMS Logs.
	public static Cursor getAllSMSLogs(ContentResolver cr) {
		Uri smsUri = Uri.parse("content://sms");
		Cursor curSmsLogs = cr.query(smsUri, null, null, null, null);

		return curSmsLogs;
	}

}
