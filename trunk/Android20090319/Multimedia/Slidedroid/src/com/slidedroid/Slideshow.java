package com.slidedroid;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.Media;
import android.util.Log;

public class Slideshow extends Activity {
	private final static String TAG = "Slideshow";
	private Cursor imgCursor;
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        setCursor();
        
        showCursorEntries();
        
    }
    
    
    private String [] proj = new String [] {
			Images.Media._ID,
			// Images.Media._COUNT,
			Images.Media.DATA,
			Images.Media.DATE_TAKEN,
			Images.Media.DISPLAY_NAME,
			Images.Media.DESCRIPTION,
			Images.Media.SIZE
	};
    
    private void setCursor() {
    	Uri imgUri = Media.EXTERNAL_CONTENT_URI;
    	// Uri imgUri = Media.INTERNAL_CONTENT_URI;
    	
    	imgCursor = managedQuery(imgUri, proj, null, null, Media.DATE_TAKEN + " ASC");   	
    	Log.d(TAG, "Got img cursor: " + imgCursor.getCount() + " entires");
    }
    
    private void showCursorEntries() {
    	
    	if (imgCursor.moveToFirst()) {
    		String dateTaken, dataStr;
    		int size, id;
    		int sid;
    		int [] c = new int[proj.length];
    		for(int i=0; i<proj.length; i++) {
    			c[i] = imgCursor.getColumnIndex(proj[i]);
    		}
    	
    		do {
    			id = imgCursor.getInt(0);
    			dateTaken = imgCursor.getString(3);
    			size = imgCursor.getInt(5);
    			dataStr= imgCursor.getString(1);
    			
    			Log.d(TAG, "id=" + id + ", taken=" + dateTaken + ", s=" + dataStr);
    			
    		} while (imgCursor.moveToNext());
    	
    		
    	}
    }
    
}