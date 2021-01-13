package com.minime.ui;

/**
 * Author: Allen Xu
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

//import com.minime.Constants;
import com.minime.statistics.*;

import java.util.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;
/*
see if this is the correct location
 */
public class MainActivity extends Activity implements SensorEventListener, TextToSpeech.OnInitListener {
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
			}
			else {
				allowCalibrateButton = true;
				plankingStarted = false;
				message.setText("Stopped");
				startButton.setText("Start Plank");
				plankingStartedTime = 0L;
			}
		}
	}
	/*
	* The startButton switches the calibrate on or off
	* @param startButton
	*/
	private void changeCalibrateState (Button startButton) {
		if (allowCalibrateButton==true) {
			if (calibrationStarted==false) {
				calibrationStarted = true;
				startButton.setText("Pause");
				calibrationStartedTime = System.currentTimeMillis();
				this.avg = 0;
				this.calibrationCounter = 0;
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

	/**
	 * Do nothing if planking is off
	 * If planking is on, measures tilt angle and if beyond threshold, sound alarm and pause ticking of time
 	 * @param event
     */
	private void plankTilt(SensorEvent event) {
		if (!plankingStarted) {
			System.out.println("Planking not yet started or has stopped!");
			return;
		}
		long theCurrentTime = System.currentTimeMillis();
		//wait 10 seconds after planking started to start measuring tilt
		if (theCurrentTime-plankingStartedTime<10050) {
			System.out.println("Planking started but wait time not yet up!");
			int time = (int) ((theCurrentTime-plankingStartedTime)/1000);
			time = 10 - time;
			if (time>9) {
				txtTimer.setText("00:"+time);
			}
			else {
				txtTimer.setText("00:0"+time);
			}
			message.setText("Counting down...");
			speech.speak("Counting down", TextToSpeech.QUEUE_FLUSH, null);
			allowCalibrateButton = false;
			//return;
		}

		else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //.TYPE_LINEAR_ACCELERATION) {
			allowCalibrateButton = false;
			System.out.println("Planking started");
			message.setText("");
			//System.out.println(evet.values[0]); //acceleration on x axis
			if (Math.abs(event.values[1]-this.avg)>1.0) {
				this.plankingStartedTime += theCurrentTime - this.prevCurrentTime;
				System.out.println(event.values[1]);
				System.out.println(txtTimer.getText());
				System.out.println(theCurrentTime-plankingStartedTime);
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
				}
				else if (time>9) {
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
	private void calibrateTilt(SensorEvent event) {
		if (!calibrationStarted) {
			//System.out.println("Planking not yet started or has stopped!");
			return;
		}
		long theCurrentTime = System.currentTimeMillis();
		//wait 10 seconds after planking started to start measuring tilt
		if (theCurrentTime-calibrationStartedTime<10050) {
			System.out.println("Planking started but wait time not yet up!");
			int time = (int) ((theCurrentTime-calibrationStartedTime)/1000);
			time = 10 - time;
			if (time>9) {
				txtTimer.setText("00:"+time);
			}
			else {
				txtTimer.setText("00:0"+time);
			}
			message.setText("Counting down...");
			allowPlankButton = false;
			return;
		}
		if (theCurrentTime-calibrationStartedTime<15000) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //.TYPE_LINEAR_ACCELERATION) {
				System.out.println("Calibration started");
				mp.start();
				this.avg = this.avg * this.calibrationCounter;
				this.calibrationCounter++;
				this.avg = (this.avg + event.values[1]) / this.calibrationCounter;
				//System.out.println(evet.values[0]); //acceleration on x axis
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
			System.out.println("calibration finished");
		}
	}

	///////////////////////// GUI variables ///////////////////////////////
	TextView txtTimer = null;
	TextView message = null;
	Button btPlanking = null;
	Button btCalibrating = null;

	// sensor manager
	private SensorManager mShakeSensorMgr;

	//////////////////////// lifecycle functions ///////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		setContentView(R.layout.planking_right_layout);

		//setScenario(SCENARIO_0);

		txtTimer = (Button)  findViewById(R.id.txtTimer);
		message = (TextView)  findViewById(R.id.message);
		btPlanking = (Button)  findViewById(R.id.btPlanking);
		btCalibrating = (Button)  findViewById(R.id.btCalibrating);
		//doWebviewPrep();
		doShakePrep();
		addListeneronButtons();

		mp = MediaPlayer.create(this, R.raw.tada);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		txtTimer = (Button)  findViewById(R.id.txtTimer);
		message = (TextView)  findViewById(R.id.message);
		btPlanking = (Button)  findViewById(R.id.btPlanking);
		btCalibrating = (Button)  findViewById(R.id.btCalibrating);
		//doWebviewPrep();
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
		btCalibrating.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				changeCalibrateState (btCalibrating);

			}
		});

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
		this.calibrateTilt(event);

	}

	/////////////////////////////////// text to speech ////////////////////////////////////
	@Override
	public void onInit(int status)

	{
		if (status == TextToSpeech.SUCCESS)

		{
			int result = speech.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				System.out.println("This Language is not supported");
			}
			else
			{
				System.out.println("This Language is supported");
			}
		}
		else
		{
			System.out.println("Initilization Failed!");
		}
	}

	@Override
	public void onDestroy() {
		// Don’t forget to shutdown tts!
		if (speech != null) {
			speech.stop();
			speech.shutdown();
		}
		super.onDestroy();
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

		try {
			inputStream = openFileInput(filename);
			inputStream.read(buffer);
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String fileContents = new String(buffer);
		return fileContents;
	}
}
