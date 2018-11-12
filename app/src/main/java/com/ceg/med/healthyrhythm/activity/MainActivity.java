package com.ceg.med.healthyrhythm.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ceg.med.healthyrhythm.list.MyoListAdapter;
import com.ceg.med.healthyrhythm.list.MyoListItem;
import com.ceg.med.healthyrhythm.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.ceg.med.healthyrhythm.activity.DetailActivity.PARAMETER_BLUETOOTH_DEVICE;
import static com.ceg.med.healthyrhythm.activity.DetailActivity.PARAMETER_MAC;

/**
 * The main activity.
 */
public class MainActivity extends AppCompatActivity {

    public static final String HEALTHY_RHYTHM_LOG_TAG = "HealthyRhythm";
    public static String ID;

    // intent code for enabling Bluetooth
    private static final int REQUEST_ENABLE_BT = 1;

    // device scanning time in ms
    private static final long SCAN_PERIOD = 10000;

    private ArrayList<MyoListItem> listItems = new ArrayList<>();
    private List<String> knownAddresses = new ArrayList<>();
    private MyoListAdapter adapter;
    private BluetoothAdapter bluetoothAdapter;
    private ScanCallback scanCallback;
    private Handler bluetoothScanHandler;

    private ProgressBar searchingBar;

    private String savedMyoMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = mBluetoothManager.getAdapter();
        bluetoothScanHandler = new Handler();
        searchingBar = (ProgressBar) findViewById(R.id.main_progress);
        scanDevice();
    }


    private void init() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
//        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = findViewById(R.id.first_image);
        imageView.setImageDrawable(getDrawable(R.mipmap.hctr));

        ListView listView = findViewById(R.id.list_view);
        adapter = new MyoListAdapter(listItems, this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyoListItem item = (MyoListItem) adapter.getItem(position);
                bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                connectAndContinue(item);
            }
        });

        listView.setAdapter(adapter);
    }

    /**
     * Connects a selected Myo and continues to the next view
     *
     * @param item {@link MyoListItem} to connect to.
     */
    private void connectAndContinue(MyoListItem item) {
        Intent nextScreen = new Intent(getApplicationContext(), DetailActivity.class);
        nextScreen.putExtra(PARAMETER_MAC, item.getMacAdress());
        nextScreen.putExtra(PARAMETER_BLUETOOTH_DEVICE, item.getDevice());
        startActivity(nextScreen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.starttoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rescan:
                scanDevice();
                return true;
            case android.R.id.home:
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Adds items to the myo bracelet list.
     *
     * @param title       Title of the bluetooth deivce found.
     * @param description Description of the bluetooth device.
     * @param device      The {@link BluetoothDevice} itself.
     * @return MyoListItem
     */
    public MyoListItem addItems(String title, String description, BluetoothDevice device, ScanRecord scanRecord) {
        MyoListItem myoListItem = new MyoListItem(title, description, device, scanRecord);
        listItems.add(myoListItem);
        adapter.notifyDataSetChanged();
        return myoListItem;
    }

    /**
     * Is used to clear the list.
     */
    private void clearItems() {
        listItems.clear();
        knownAddresses.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * Scans for new Bluetooth devices.
     */
    public void scanDevice() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            clearItems();
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    handleBluetoothResult(result);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                    bluetoothAdapter.getBluetoothLeScanner().stopScan(this);
                    searchingBar.setProgress(0);
                    searchingBar.setVisibility(View.INVISIBLE);
                }
            };
            bluetoothScanHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                    searchingBar.setProgress(0);
                    searchingBar.setVisibility(View.INVISIBLE);
                }
            }, SCAN_PERIOD);
            bluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
            searchingBar.setProgress(50);
            searchingBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Handles the result of the bluetooth search.
     *
     * @param scanResult The {@link ScanResult} of the bluetooth search.
     */
    private void handleBluetoothResult(ScanResult scanResult) {
        BluetoothDevice device = scanResult.getDevice();

        String msg = "name=" + device.getName() + ", bondStatus="
                + device.getBondState() + ", address="
                + device.getAddress() + ", type" + device.getType();
        Log.d(HEALTHY_RHYTHM_LOG_TAG, msg);

        if (!knownAddresses.contains(device.getAddress())) { //&& device.getType() == MYO_DEVICE_TYPE) {
            knownAddresses.add(device.getAddress());
            addItems(device.getName() != null ? device.getName() : "unknown", device.getAddress(), device, scanResult.getScanRecord());
        }
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}

