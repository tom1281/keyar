package com.keyar;

import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ARScan extends Activity {
	private final static String TAG = ARScan.class.getSimpleName();
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
	//Characteristic used to send and receive data
	public final static UUID charUUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
	//Service used to pass-through data
	public final static UUID servUUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");

	private BluetoothManager bluetoothManager;
	private BluetoothAdapter bluetoothAdapter;
	private String bluetoothDeviceAddress;
	private BluetoothGatt bluetoothGatt;
	private BluetoothDevice device;
	private BluetoothGattService service;
	private BluetoothGattCharacteristic charac;
	private boolean connected = false;
	
	int i=0;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_arscan);
		
		button = (Button)findViewById(R.id.button1);
		
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				i++;
				writeCharacteristic("Count: " + i);
				
			}
		});

		initializeBtDevice();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bluetoothGatt = device.connectGatt(this, false, mGattCallback);
		bluetoothGatt.discoverServices();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		connected = false;
		bluetoothGatt.disconnect();
		bluetoothGatt.close();
		bluetoothGatt = null;
		finish();
	}

	public boolean initializeBtDevice() {
		if (bluetoothManager == null) {
			bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (bluetoothManager == null) {
				Log.e(TAG, "Unable to initialize BluetoothManager.");
				return false;
			}
		}

		bluetoothAdapter = bluetoothManager.getAdapter();
		if (bluetoothAdapter == null) {
			Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
			return false;
		}

		// Create intent to get the remote bt device address
		final Intent intent = getIntent();
		bluetoothDeviceAddress = intent.getStringExtra("DEVICE_ADDRESS");
		device = bluetoothAdapter.getRemoteDevice(bluetoothDeviceAddress);

		return true;
	}

	// The BluetoothGattCallback is used to deliver results to the client, such as connection status, as well as any further GATT client operations.
	private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			// Connection established
			if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
				Log.i(TAG, "Connected to GATT server.");
				// Discover services
				gatt.discoverServices();

			} else if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_DISCONNECTED) {

				// Handle a disconnect event
				Log.i(TAG, "Disconnected from GATT server.");
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				service = bluetoothGatt.getService(servUUID);
				if (service == null) {
					Log.e(TAG, "Service not found!");
				}
				charac = service.getCharacteristic(charUUID);
				if (charac == null) {
					Log.e(TAG, "Char not found!");
				}
				connected = true;
				Log.i(TAG, "Service discovered.");
			} else {
				Log.w(TAG, "onServicesDiscovered received: " + status);
			}
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.arscan, menu);
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

	public boolean writeCharacteristic(String data) {
		boolean status = false;
		if (connected) {
			byte[] value = null;
			value = data.getBytes();
			charac.setValue(value);
			status = bluetoothGatt.writeCharacteristic(charac);
			Log.i(TAG, "Data sent! Status: " + status + ", Data: "+data);
		}
		return status;
	}

}
