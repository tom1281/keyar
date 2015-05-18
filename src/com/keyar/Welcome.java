package com.keyar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Welcome extends Activity {

	private BluetoothAdapter bluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;

	private ProgressBar pbar;
	private Button scanButton;
	private TextView tview;

	private static final int REQUEST_CODE_BT = 1;
	// Stops scanning after 3 seconds.
	private static final long SCAN_PERIOD = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);

		scanButton = (Button) findViewById(R.id.scanButton);
		pbar = (ProgressBar) findViewById(R.id.progressBar);
		tview = (TextView) findViewById(R.id.info);
		
		//Handler allows to send and process messages
		mHandler = new Handler();

		//Check, if BT V4 is supported on this device.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
			finish();
		}

		//BluetoothManager allows to "conduct overall Bluetooth Management"
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		// Represents the local device Bluetooth adapter. The BluetoothAdapter lets you perform fundamental Bluetooth tasks, such as initiate device
		// discovery, query a list of bonded (paired) devices
		bluetoothAdapter = bluetoothManager.getAdapter();


		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				scanDevice(true);
			}
		});

		scanDevice(true);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		scanDevice(false);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Checks if Bluetooth is already enabled; if not - shows a dialog with enable request
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBt, REQUEST_CODE_BT);
		}

	}
	
	//"Callback interface used to deliver LE scan results."
	private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
		//Callback reporting an LE device found during a device scan initiated by the startLeScan(BluetoothAdapter.LeScanCallback) function.
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				public void run() {
					//If device with name "HMSoft" is found, start a new activity
					if (device.getName().equals("HMSoft")) {
						Intent intent = new Intent(getBaseContext(), ARScan.class);
						intent.putExtra(ARScan.EXTRAS_DEVICE_ADDRESS, device.getAddress());
						bluetoothAdapter.stopLeScan(leScanCallback);
						startActivity(intent);
					}
				};
			});

		}
	};

	//Scanning management
	private void scanDevice(boolean scan) {
		//For "true" starts scanning and stops after passing "SCAN_PERIOD"
		if (scan) {
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mScanning = false;
					bluetoothAdapter.stopLeScan(leScanCallback);
					updateScreen();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			bluetoothAdapter.startLeScan(leScanCallback);
			updateScreen();

		} else {
			mScanning = false;
			bluetoothAdapter.stopLeScan(leScanCallback);
		}
	}
	
	//Funktion for updating screen information
	private void updateScreen() {
		if (mScanning) {
			pbar.setVisibility(View.VISIBLE);
			scanButton.setVisibility(View.INVISIBLE);
			tview.setText(getString(R.string.ble_device_searching));
		} else {
			pbar.setVisibility(View.INVISIBLE);
			scanButton.setVisibility(View.VISIBLE);
			tview.setText(getString(R.string.ble_device_not_found));
		}
	}

	// You try to connect
	// You get a callback indicating it is connected
	// You discover services
	// You are told services are discovered
	// You get the characteristics
	// For each characteristic you get the descriptors
	// For the descriptor you set it to enable notification/indication with
	// BluetoothGattDescriptor.setValue()
	// You write the descriptor with BluetoothGatt.writeDescriptor()
	// You enable notifications for the characteristic locally with
	// BluetoothGatt.setCharacteristicNotification(). Without this you won't get
	// called back.
	// You get notification that the descriptor was written
	// Now you can write data to the characteristic. All of the characteristic
	// and descriptor configuration has do be done before anything is written to
	// any characteristic.

}
