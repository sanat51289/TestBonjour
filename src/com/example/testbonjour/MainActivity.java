package com.example.testbonjour;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setContentView(R.layout.activity_main);
		final TextView t = (TextView) findViewById(R.id.textView1);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				new SplashTask().execute(null,null);
			}
		}, 1000L);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    private class SplashTask extends AsyncTask<Void, Integer, Void>{

    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    			
    	}
    	

    	@Override
		protected Void doInBackground(Void... arg0) {
			
			for(int i=0; i < 1 ; i++){
				try {
					//fake a delay of 2 seconds
					Thread.sleep(2000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// we will call publish progress, which will invoke onProgressUpdate callback
				// in that method we will change the message that goes on the ProgressDialog
				
				publishProgress(i);
			}
			
			return null;
		}
		
    	// Used to publish intermediate results
    	// we will use this to change message on the Progress Dialog
    	// since we are able to change the message on the UI this means that this method is also run
    	// on UI thread
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			switch(values[0]){
				default:break;
			}
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			// this is one way of calling an activity
			Intent intent = new Intent(MainActivity.this, Bonjour.class);
			startActivity(intent);
			
			// let us remove splash screen from the UI; we call finish()
			finish();
		}
    }

}
