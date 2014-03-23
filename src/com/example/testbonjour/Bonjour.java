package com.example.testbonjour;

import java.io.IOException;
import java.util.Timer;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class Bonjour extends Activity {

	private static final String TAG = Bonjour.class.getSimpleName();

	android.net.wifi.WifiManager.MulticastLock lock;
	android.os.Handler handler = new android.os.Handler();
	
	private setUpTask mTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listdevices);


		mTask = new setUpTask();
		
		Log.v(TAG, "+++++ onCreate called() ");
		handler.postDelayed(new Runnable() {
			public void run() {

				Log.v(TAG, "+++++ Handler executing Runnable() ");
				mTask.execute(null, null);
			}
		}, 1000L);

	} /** Called when the activity is first created. */


	//private String type = "_workstation._tcp.local.";
	//private String type = "_test._tcp.local.";
	private String type = "_diplomat._tcp.local.";
	 //private String type = "_airplay._tcp.local.";
	private JmDNS jmdns = null;
	private ServiceListener listener = null;
	private ServiceInfo serviceInfo;


	private class setUpTask extends AsyncTask<Void, String, Void>{
		ProgressDialog loading = new ProgressDialog(Bonjour.this);
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();


			Log.v(TAG, "+++++ starting Async() ");
			loading.requestWindowFeature(Window.FEATURE_NO_TITLE);

			loading.setMessage("Searching for devices...");
			loading.show();	
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);


			Log.v(TAG, "+++++ acquiring multicast lock ");

			lock = wifi.createMulticastLock("mylockthereturn");
			lock.setReferenceCounted(true);
			lock.acquire();


			Log.v(TAG, "+++++ lock acquired? :" + lock.isHeld());
			try {
				jmdns = JmDNS.create();
				
				Log.v(TAG, " ++++++ JmDNS created() :: attaching listener");
				jmdns.addServiceListener(type, listener = new ServiceListener() {

					@Override
					public void serviceResolved(ServiceEvent ev) {

						Log.v(TAG, "+++++ service resolved()");
						publishProgress("Service resolved: " + ev.getInfo().getQualifiedName() + " port:" + ev.getInfo().getPort());
					}

					@Override
					public void serviceRemoved(ServiceEvent ev) {


						Log.v(TAG, "+++++ service removed()");
						publishProgress("Service removed: " + ev.getName());
					}

					@Override
					public void serviceAdded(ServiceEvent event) {
						// Required to force serviceResolved to be called again (after the first search)

						Log.v(TAG, "+++++ service Added()");
						jmdns.requestServiceInfo(event.getType(), event.getName(), 1);
					}
				});

				Log.v(TAG, "+++++ creating ServiceInfo");
				serviceInfo = ServiceInfo.create("_test._tcp.local.", "AndroidTest", 0, "plain test service from android");
				jmdns.registerService(serviceInfo);


				Log.v(TAG, "+++++ Registered for Service");
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			}
			return null;

		}
		protected void onProgressUpdate(String... values) {
			loading.dismiss();
			TextView t = (TextView)findViewById(R.id.text);
			t.setText(values[0]+"\n=== "+t.getText());
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		//new Thread(){public void run() {setUp();}}.start();
	}

	@Override
	protected void onStop() {
		

        Log.v(TAG, "+++++ onStop was called() ");
        

        Log.v(TAG, "+++++ is Task still running? : " + (!mTask.isCancelled()));
        
        if(!mTask.isCancelled()){

            Log.v(TAG, "+++++ Task is still running : attempting to cancel it");
        	mTask.cancel(true);
        }
        
		if (jmdns != null) {

	        Log.v(TAG, "+++++ Releasing JmDNS");
			if (listener != null) {
				jmdns.removeServiceListener(type, listener);
				listener = null;
			}
			jmdns.unregisterAllServices();
			try {
				jmdns.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jmdns = null;
		}
		//repo.stop();
		//s.stop();
		if(lock.isHeld()){
			

	        Log.v(TAG, "+++++ Lock was held : releasing ...");
			lock.release();
		}
		super.onStop();
	}
}



