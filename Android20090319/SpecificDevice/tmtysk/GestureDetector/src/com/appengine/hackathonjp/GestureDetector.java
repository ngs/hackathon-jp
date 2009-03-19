package com.appengine.hackathonjp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;
import android.widget.TextView;

public class GestureDetector extends Activity
{
	private static final int NOT_MOVED = 0;
	private static final int MOVED_UP = 1;
	private static final int MOVED_RIGHT = 2;
	private static final int MOVED_DOWN = 3;
	private static final int MOVED_LEFT = 4;
	private static final int MOVED_APPROACHING = 5;
	private static final int MOVED_LEAVING = 6;
	private static final float G = 9.8F;
	private static final float ACC_THRESHOLD = 1.0F;
	private static final float ACC_MARGIN = 2.0F;
	
	private static final int DEVICETOP_TOP = 11;
	private static final int DEVICERIGHT_TOP = 12;
	private static final int DEVICEBOTTOM_TOP = 13;
	private static final int DEVICELEFT_TOP = 14;
	private static final int DISPLAY_TOP = 15;
	private static final int BACK_TOP = 16;
	
	private static final int TIMES_RECORDING = 5;
	
	/*
	private final String[7] STR_MOVED = 
	[
		 getString(R.string.strNotMoved),
		 getString(R.string.strMovedUp),
		 getString(R.string.strMovedRight),
		 getString(R.string.strMovedDown),
		 getString(R.string.strMovedLeft),
		 getString(R.string.strMovedApproaching),
		 getString(R.string.strMovedLeaving)
	];
	*/
	
	private SensorManager sensorManager;
	private MySensorListener mySensorListener;
	private StringBuffer sensorValueHistory;
	private String sensoredDirection;
	private BufferedWriter bufferedWriter;
	
	private int[] recentDirection;

	private TextView accelView;
	private ToggleButton opButton;
	private float accX, accY, accZ, absMaxDirection, absX, absY, absZ;
	private int orientation;
	private String direction;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		BufferedWriter bufferedWriter = null;
		recentDirection = new int[TIMES_RECORDING];
		for (int i=0; i<TIMES_RECORDING; i++)
		{
			recentDirection[i] = NOT_MOVED;
		}
		mySensorListener = new MySensorListener();
		accelView = (TextView) findViewById(R.id.accelView);
		opButton = (ToggleButton) findViewById(R.id.opButton);
		opButton.setOnClickListener(new OnClickListener()
		{
            public void onClick(View v)
            {
            	if(((ToggleButton) v).isChecked())
            	{
            		sensorValueHistory = new StringBuffer();
            		sensorManager.registerListener(mySensorListener, 
                    		SensorManager.SENSOR_ACCELEROMETER|SensorManager.SENSOR_ORIENTATION,
                    		SensorManager.SENSOR_DELAY_FASTEST);
            	}
            	else
            	{
            		try
            		{
            			//saveMeasuredData();
            		}
            		catch(Exception e)
            		{
            		}
            		sensorManager.unregisterListener(mySensorListener);
            	}
            }
        }
		);
	}
	
	public void saveMeasuredData() throws Exception
	{
		try
		{
			bufferedWriter = new BufferedWriter(
					new FileWriter("/sdcard/"
							+ (new Long(System.currentTimeMillis()).toString()) + ".csv"));
			bufferedWriter.write(sensorValueHistory.toString(), 0, sensorValueHistory.length());
		} 
		finally
		{
			if(bufferedWriter != null)
			{
				try
				{
					bufferedWriter.close();
					bufferedWriter = null;
				}
				catch(IOException exception)
				{
				}
			}
		}
	}

	class MySensorListener implements SensorListener
	{
        public void onAccuracyChanged(int sensor, int accuracy)
        {
        }

		public void onSensorChanged(int sensor, float[] values)
		{
			if (sensor != SensorManager.SENSOR_ACCELEROMETER
					&& sensor != SensorManager.SENSOR_ORIENTATION)
				return;
			
			/*
			if (sensor == SensorManager.SENSOR_ORIENTATION)
			{
				if ((values[1] > -45.0)&&(values[1] <= 45.0)
						&&(values[2] > -45.0)&&(values[2] <= 45.0))
				{
					// device is lying display_top
					orientation = DISPLAY_TOP;
				}
				else if ((values[1] > 45.0)&&(values[1] <= 135.0)
						&&(values[0] > -45.0)&&(values[0] <= 45.0))
				{
					// device is standing
					orientation = DEVICETOP_TOP;
				}
				else if ((values[1] > 135.0)&&(values[1] < -135.0)
					&&(values[2] > -45.0)&&(values[2] <= 45.0))
				{
					// device is lying back_top
					orientation = BACK_TOP;
				}
				else if ((values[1] > -135.0)&&(values[1] < -45.0)
						&&(values[2] > -45.0)&&(values[2] <= 45.0))
				{
					// device is bottom_top
					orientation = DEVICEBOTTOM_TOP;
				}
				else if ((values[1] <= -45.0)
						&&(values[0] > -45.0)&&(values[0] <= -45.0))
				{
					// device is lying left_top
					orientation = DEVICELEFT_TOP;
				}
				else if ((values[2] > 45.0)
						&&(values[0] > -45.0)&&(values[0] <= -45.0))
				{
					// device is lying right_top
					orientation = DEVICELEFT_TOP;
				}
			}
			*/
			
			if (sensor == SensorManager.SENSOR_ACCELEROMETER)
				/*&&
				(Math.sqrt(
					values[0]*values[0] +
					values[1]*values[1] +
					values[2]*values[2])
					 > 5.0))
					 */
			{
				/*
				sensorValueHistory.append(
						"accelero," + (new Float(values[0]).toString()) +
						"," + (new Float(values[1]).toString()) +
						"," + (new Float(values[2]).toString()) + "\n");
						*/
				accX = values[0];
				accY = values[1];
				accZ = values[2];
				
				/*
				switch(orientation) {
				case DEVICETOP_TOP:
					accY += G;
					break;
				case DEVICERIGHT_TOP:
					accX += G;
					break;
				case DEVICEBOTTOM_TOP:
					accY -= G;
					break;
				case DEVICELEFT_TOP:
					accX -= G;
					break;
				case DISPLAY_TOP:
					accZ -= G;
					break;
				case BACK_TOP:
					accZ += G;
					break;
				}
				*/
				// fixed devicetop_top
				accY += SensorManager.STANDARD_GRAVITY;
				
				absX = Math.abs(accX);
				absY = Math.abs(accY);
				absZ = Math.abs(accZ);
				
				absMaxDirection = Math.max(
					Math.max(
						absX,
						absY
					), absZ
				);
				
				if (absMaxDirection == absX)
				{
					// Moved X
					if (accX > 0)
					{
						//direction = new String("Moved Right\n");
						pushDirection(MOVED_RIGHT);
					}
					else
					{
						//direction = new String("Moved Left\n");
						pushDirection(MOVED_LEFT);
					}
					//sensorValueHistory.append(direction);
				}
				else if (absMaxDirection == absY)
				{
					// Moved Y
					if (accY > 0)
					{
						//direction = new String("Moved Up\n");
						pushDirection(MOVED_UP);
					}
					else
					{
						pushDirection(MOVED_DOWN);
						//direction = new String("Moved Down\n");
					}
					//sensorValueHistory.append(direction);

				}
				/*
				else if ((absMaxDirection == absZ)&&(absZ > ACC_THRESHOLD))
				{
					// Moved Z
					if (accZ > 0)
					{
						//direction = new String("Moved Approaching\n");
						pushDirection(MOVED_APPROACHING);
					}
					else
					{
						//direction = new String("Moved Leaving\n");
						pushDirection(MOVED_LEAVING);
					}
					//sensorValueHistory.append(direction);
				}
				*/
				else
				{
					pushDirection(NOT_MOVED);
				}
				
				if (recentContinuousDirection() != NOT_MOVED)
				{
					//sensorValueHistory.append((new Integer(recentContinuousDirection()).toString()) + "\n");
			        sensoredDirection = (new Integer(recentContinuousDirection()).toString()) + "\n";
			        //sensoredDirection = (new Float(absMaxDirection).toString()) + "\n";
				}
				
				/*
						(new Float(Math.sqrt(
								values[0]*values[0] +
								values[1]*values[1] +
								values[2]*values[2])
								)).toString()
						+ "\n"
						);
						*/
			}
			/*
			else 
			{
				sensorValueHistory.append(
						"orientation," + (new Float(values[0]).toString()) +
						"," + (new Float(values[1]).toString()) +
						"," + (new Float(values[2]).toString()) + "\n");
			}
			*/
			//accelView.setText(sensorValueHistory);
			accelView.setText(sensoredDirection);
		}

	}
	
	public void pushDirection(int direction)
	{
		System.out.println("pushed " + (new Integer(direction).toString()));
		for (int i=0; i<TIMES_RECORDING; i++)
		{
			if (i == 0) continue;
			recentDirection[i-1] = recentDirection[i];
		}
		recentDirection[TIMES_RECORDING-1] = direction;
	}
	
	public int recentContinuousDirection()
	{
		int direction = recentDirection[0];
		for (int i=1; i<TIMES_RECORDING; i++)
		{
			if (direction != recentDirection[i])
			{
				direction = NOT_MOVED;
				break;
			}
		}
		return direction;
	}
}