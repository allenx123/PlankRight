package com.minime.plank;

/**
 * Author: Allen Xu
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.content.ContentResolver;
import android.webkit.WebViewClient;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.*;
import android.media.MediaPlayer;

import android.speech.tts.TextToSpeech;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
//import com.minime.Constants;
import com.minime.statistics.*;
import com.minime.plank.R;

import java.util.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;
/*
see if this is the correct location
 */
public class MainActivity extends Activity implements SensorEventListener {
	///////////////////////// planking ///////////////////////////////
	boolean plankingStarted = false;
	long plankingStartedTime = 0L;
	long calibrationStartedTime = 0L;
	long planingDuration = 0L;
	boolean allowPlankButton = true;
	boolean allowCalibrateButton = true;
	boolean calibrationStarted = false;
	double calibratedTilt = 1.0;
	double avg = 1;
	int calibrationCounter = 0;
	long prevCurrentTime;

	MediaPlayer mp = null;
	private TextToSpeech speech;
	/**
	 * The startButton switches the planking on or off
	 * @param startButton
     */
	private void changePlankingState (Button startButton) {

		if (allowPlankButton==true) {
			if (plankingStarted==false) {
				plankingStarted = true;
				startButton.setText("Pause");
				plankingStartedTime = System.currentTimeMillis();
				//using loop-playing html5 video does not stop android from stopping the app when sleep times out.
				//play the video in html5 to keep screen alive
				//wvAnimation.loadUrl("javascript:playVid()");

				//ask Android to extend screen life to its maximum (eg, 30 minutes)
				//android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE);
			}
			else {
				allowCalibrateButton = true;
				plankingStarted = false;
				message.setText("Stopped");
				startButton.setText("Begin Plank");
				plankingStartedTime = 0L;
				//using loop-playing html5 video does not stop android from stopping the app when sleep times out.
				//stop playing the video in html5 to save battery life
				//wvAnimation.loadUrl("javascript:pauseVid()");
			}
		}
	}
	/*
	* The startButton switches the calibrate on or off
	* @param startButton
	*/
	/*
	private void changeCalibrateState (Button startButton) {
		if (allowCalibrateButton==true) {
			if (calibrationStarted==false) {
				calibrationStarted = true;
				startButton.setText("Pause");
				calibrationStartedTime = System.currentTimeMillis();
				this.avg = 0;
				this.calibrationCounter = 0;
				//ask Android to extend screen life to its maximum (eg, 30 minutes)
				//android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE);
			}
			else {
				allowPlankButton = true;
				calibrationStarted = false;
				message.setText("Stopped");
				startButton.setText("Calibrate");
				calibrationStartedTime = 0L;
			}
		}

	}
	*/

	/**
	 * Do nothing if planking is off
	 * If planking is on, measures tilt angle and if beyond threshold, sound alarm and pause ticking of time
 	 * @param event
     */
	private void plankTilt(SensorEvent event) {
		if (!plankingStarted) {
			debug("Planking not yet started or has stopped!");
			return;
		}
		long theCurrentTime = System.currentTimeMillis();
		//wait 10 seconds after planking started to start measuring tilt
		if (theCurrentTime-plankingStartedTime<10050) {
			debug("Planking started but wait time not yet up!");
			int time = (int) ((theCurrentTime-plankingStartedTime)/1000);
			time = 10 - time;
			if (time>9) {
				txtTimer.setText("00:"+time);
			}
			else {
				txtTimer.setText("00:0"+time);
			}
			if (time==2) {
				//speech.setSpeechRate(0.05f);
				speech.speak("Planking starts now.", TextToSpeech.QUEUE_FLUSH, null);
				//speech.setSpeechRate(1.0f);
			}
			/*
			else if (time==2) {
				speech.speak("2", TextToSpeech.QUEUE_FLUSH, null);
			}
			else if (time==1) {
				speech.speak("1", TextToSpeech.QUEUE_FLUSH, null);
			}
			*/
			//message.setText("Counting down...");
			message.setText("Place device on back");
			//speech.speak("Counting down", TextToSpeech.QUEUE_FLUSH, null);
			allowCalibrateButton = false;
			//return;
		}

		else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //.TYPE_LINEAR_ACCELERATION) {
			allowCalibrateButton = false;
			debug("Planking started");
			message.setText("");
			//double num = Double.parseDouble(readFromFile());
			//debug(evet.values[0]); //acceleration on x axis
			/*
			if (this.avg != 1.0 && Math.abs(event.values[1]-num)>1.0) {
				/*
				if (event.values[1]-num>1.0) {
					speech.speak("Too low!", TextToSpeech.QUEUE_FLUSH, null);
				}
				else if (event.values[1]-num<-1.0) {
					speech.speak("Too high!", TextToSpeech.QUEUE_FLUSH, null);
				}
				*/
			/*
				this.plankingStartedTime += theCurrentTime - this.prevCurrentTime;
				debug(event.values[1]);
				debug(txtTimer.getText());
				debug(theCurrentTime-plankingStartedTime);
				mp.start();
			}
			*/
			//change to else if when bringing back calibrate
			if (this.avg == 1.0 && Math.abs(event.values[1]-1.0)>1.0) {
				/*
				if (event.values[1]-1.0>1.0) {
					speech.speak("Too low!", TextToSpeech.QUEUE_FLUSH, null);
				}
				else if (event.values[1]-1.0<-1.0) {
					speech.speak("Too high!", TextToSpeech.QUEUE_FLUSH, null);
				}
				*/
				this.plankingStartedTime += theCurrentTime - this.prevCurrentTime;
				debug(event.values[1]);
				debug(txtTimer.getText());
				debug(theCurrentTime-plankingStartedTime);
				mp.start();
			}
			else {
				int time = (int) ((theCurrentTime-plankingStartedTime-10000)/1000);
				if (time>59) {
					int seconds = time%60;
					int minutes = time/60;
					String minutesString;
					String secondsString;
					if (minutes>9) {
						minutesString = ""+minutes;
					}
					else {
						minutesString = "0"+minutes;
					}
					if (seconds>9) {
						secondsString = ""+seconds;
					}
					else {
						secondsString = "0"+seconds;
					}
					txtTimer.setText(minutesString+":"+secondsString);
					if (seconds==0 && minutes==1) {
						speech.speak(minutes+" minute", TextToSpeech.QUEUE_FLUSH, null);
						debug(minutes+" minute");
					}
					else if (seconds==0 && minutes>1) {
						speech.speak(minutes+" minutes", TextToSpeech.QUEUE_FLUSH, null);
					}
					else if(seconds>0 && seconds%10==0 && minutes==1) {
						speech.speak(minutes+" minute "+seconds+" seconds", TextToSpeech.QUEUE_FLUSH, null);
					}
					else if (seconds>0 && seconds%10==0 && minutes>1) {
						speech.speak(minutes+" minutes "+seconds+" seconds", TextToSpeech.QUEUE_FLUSH, null);
					}
					txtTimer.setText(minutesString+":"+secondsString);
				}
				else if (time>9) {
					if (time%10==0) {
						speech.speak(time+" seconds", TextToSpeech.QUEUE_FLUSH, null);
						debug(time+" seconds"
						);
					}
					txtTimer.setText("00:"+time);
				}
				else {
					txtTimer.setText("00:0"+time);
				}
			}
			//event.values[1]; //acceleration on y axis
			//event.values[2]; //acceleration on z axis
		}
		this.prevCurrentTime = theCurrentTime;
	}
	/**
	 * Do nothing if calibrate is off
	 * If calibrate is on, measures calibrate value  and store it in calibratedTilt
	 * Switch calibrate off after ? seconds
	 * @param event
	 */
	/*
	private void calibrateTilt(SensorEvent event) {
		if (!calibrationStarted) {
			//debug("Planking not yet started or has stopped!");
			return;
		}
		long theCurrentTime = System.currentTimeMillis();
		//wait 10 seconds after planking started to start measuring tilt
		if (theCurrentTime-calibrationStartedTime<10050) {
			debug("Planking started but wait time not yet up!");
			int time = (int) ((theCurrentTime-calibrationStartedTime)/1000);
			time = 10 - time;
			if (time>9) {
				txtTimer.setText("00:"+time);
			}
			else {
				txtTimer.setText("00:0"+time);
			}
			if (time==3) {
				//speech.setSpeechRate(0.05f);
				speech.speak("Calibration starts now.", TextToSpeech.QUEUE_FLUSH, null);
				//speech.setSpeechRate(1.0f);
			}
			message.setText("Counting down...");
			allowPlankButton = false;
			return;
		}
		if (theCurrentTime-calibrationStartedTime<15000) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //.TYPE_LINEAR_ACCELERATION) {
				debug("Calibration started");
				mp.start();
				this.avg = this.avg * this.calibrationCounter;
				this.calibrationCounter++;
				this.avg = (this.avg + event.values[1]) / this.calibrationCounter;
				//debug(evet.values[0]); //acceleration on x axis
				//event.values[1]; //acceleration on y axis
				//event.values[2]; //acceleration on z axis
			}
			message.setText("Calibrating...");
			allowPlankButton = false;
		}
		else {
			calibrationStarted = false;
			allowPlankButton = true;
			btCalibrating.setText("Calibrate");
			message.setText("Calibration finished");
			debug("calibration finished");
			speech.speak("Calibration finished", TextToSpeech.QUEUE_FLUSH, null);
			writeToFile(this.avg+"");
		}
	}
	*/

	///////////////////////// GUI variables ///////////////////////////////
	TextView txtTimer = null;
	TextView message = null;
	Button btPlanking = null;
	Button btCalibrating = null;
	WebView wvAnimation = null;
	// sensor manager
	private SensorManager mShakeSensorMgr;

	//////////////////////// lifecycle functions ///////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		setContentView(R.layout.planking_right_layout);

		//setScenario(SCENARIO_0);

		txtTimer = (TextView)  findViewById(R.id.txtTimer);
		message = (TextView)  findViewById(R.id.message);
		btPlanking = (Button)  findViewById(R.id.btPlanking);
		//btCalibrating = (Button)  findViewById(R.id.btCalibrating);

		doWebviewPrep();
		doShakePrep();
		addListeneronButtons();

		mp = MediaPlayer.create(this, R.raw.tada);

		speech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if(status != TextToSpeech.ERROR) {
					speech.setLanguage(Locale.US);
				}
			}
		});

		//ask Android to extend screen life to its maximum (eg, 30 minutes)
		//android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE);
		try {
			int screenOffTimeout = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_OFF_TIMEOUT);
			if (screenOffTimeout < 60*2000) {
				//launch dialog box to alert user
				launchAlertDialog("In order to get the best experience, please change screen timeout in settings to 2 minutes or more.");
			}
		} catch (Exception e) {

		}

	}
	private void launchAlertDialog(String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Warning");

		// set dialog message
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				//.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				//	public void onClick(DialogInterface dialog,int id) {
				//		// if this button is clicked, close
				//		// current activity
				//		MainActivity.this.finish();
				//	}
				//})
				.setNegativeButton("OK",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}
	private void doWebviewPrep () {
		/* using loop-playing html5 video does not stop android from stopping the app when sleep times out.
		wvAnimation = (WebView) findViewById(R.id.wvAnimation);
		String mime = "text/html";
		String encoding = "utf-8";
		String templateHtml = Utility.getMsg(R.raw.screen_stabalization_test_html, this);
		wvAnimation.getSettings().setJavaScriptEnabled(true);
		wvAnimation.loadDataWithBaseURL(null, templateHtml, mime, encoding, null);
		*/
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		txtTimer = (TextView)  findViewById(R.id.txtTimer);
		message = (TextView)  findViewById(R.id.message);
		btPlanking = (Button)  findViewById(R.id.btPlanking);
		//btCalibrating = (Button)  findViewById(R.id.btCalibrating);
		doWebviewPrep();
		doShakePrep();
		addListeneronButtons();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unregister();
	}
	@Override
	public void onResume(){
		if(speech ==null){
			speech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
				@Override
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR) {
						speech.setLanguage(Locale.US);
					}
				}
			});
		}
		super.onResume();
	}
	public void onPause(){
		if(speech !=null){
			speech.stop();
			speech.shutdown();
			speech = null;
		}
		super.onPause();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	//button click event handler to switch pause on or off
	public void addListeneronButtons() {

		btPlanking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				changePlankingState(btPlanking);
			}
		});
		/*
		btCalibrating.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				//changeCalibrateState (btCalibrating);

			}
		});
		*/

	}

	/**
	 * prepare for shake event handling
	 */
	private void doShakePrep() {
		// Get a sensor manager to listen for shakes
		mShakeSensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		// Listen for shakes
		Sensor accelerometer = mShakeSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//.TYPE_LINEAR_ACCELERATION);
		if (accelerometer != null) {
			mShakeSensorMgr.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
		}
	}
	void unregister() {
		mShakeSensorMgr.unregisterListener(this);
	}
	/**
	 * ignore; required by SensorEventListener interface
	 */

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	/**
	 * shake event handling; required by SensorEventListener interface
	 * For an understanding of the event.values[0], event.values[1], event.values[2], when
	 * the sensor type is TYPE_LINEAR_ACCELERATION, refer to the following and search for LINEAR_ACCELERATION
	 * https://developer.android.com/reference/android/hardware/SensorEvent.html
	 */

	@Override
	public void onSensorChanged(SensorEvent event) {
		plankTilt(event);
		//this.calibrateTilt(event);

	}
	///////////////////////////////////// file on internal storage /////////////////////////////////
	private void writeToFile (String value) {
		String filename = "myfile";
		String fileContents = value;
		java.io.FileOutputStream outputStream;

		try {
			outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
			outputStream.write(fileContents.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String readFromFile () {
		String filename = "myfile";
		//String fileContents = value;
		byte[] buffer = new byte[100];
		java.io.FileInputStream inputStream;
		String line = "";

		try {
			inputStream = openFileInput(filename);
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(inputStream));
			line = reader.readLine();
			//inputStream.read(buffer);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fileContents = line; //new String(buffer);
		return fileContents;
	}
	private void debug (float s) {
		//System.out.println(s);
	}
	private void debug (CharSequence s) {
		//System.out.println(s);
	}
	private void debug (String s) {
		//System.out.println(s);
	}
}
