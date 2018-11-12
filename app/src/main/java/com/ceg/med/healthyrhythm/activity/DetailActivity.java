package com.ceg.med.healthyrhythm.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ceg.med.healthyrhythm.R;
import com.ceg.med.healthyrhythm.data.CallbackAble;
import com.ceg.med.healthyrhythm.data.NiniGattCallback;

import static android.view.View.VISIBLE;

/**
 * Activity for the Myo Details view.
 */
public class DetailActivity extends AppCompatActivity implements CallbackAble<Integer> {

    public static final String PARAMETER_MAC = "myoMacAddress";
    public static final String PARAMETER_BLUETOOTH_DEVICE = "myoBluetoothDevice";
    private ProgressBar connectionBar;
    private BluetoothGatt bluetoothGatt;
    private ProgressBar bar;
    private int maxVal;

    private NiniGattCallback niniGattCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent i = getIntent();
        BluetoothDevice bluetoothDevice = i.getExtras().getParcelable(PARAMETER_BLUETOOTH_DEVICE);

        ImageView imageView = findViewById(R.id.first_image);
        imageView.setImageDrawable(getDrawable(R.drawable.rk));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), HealthyRhythmUnityPlayerActivity.class);
                startActivity(i);
            }
        });

        Toolbar myToolbar = findViewById(R.id.myo_toolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);
        setSupportActionBar(myToolbar);

        connectionBar = findViewById(R.id.detail_progress);
        connectionBar.setProgress(0);
        connectionBar.setVisibility(VISIBLE);

        bar = findViewById(R.id.myo_sensor_one);
        maxVal = 0;
        bar.setProgress(0);
        niniGattCallback = new NiniGattCallback(this);
        bluetoothGatt = bluetoothDevice.connectGatt(this, false, niniGattCallback);
        bar.setProgress(0);
        connectionBar.setProgress(100);
        connectionBar.setVisibility(View.GONE);
        niniGattCallback.setBluetoothGatt(bluetoothGatt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detailtoolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeBluetoothConnection();
                finish();
                return true;

            case R.id.myo_clear_max:
                bar.setSecondaryProgress(0);
                maxVal = 0;
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        closeBluetoothConnection();
        finish();
    }

    /**
     * Closes the bluetooth connection to the myo.
     */
    public void closeBluetoothConnection() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

    @Override
    public void callback(Integer value) {
        bar.setProgress(value);
        if (maxVal < value) {
            maxVal = value;
            bar.setSecondaryProgress(value);
        }
    }

}
