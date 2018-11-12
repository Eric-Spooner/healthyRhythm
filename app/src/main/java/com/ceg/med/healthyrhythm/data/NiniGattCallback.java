package com.ceg.med.healthyrhythm.data;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import static android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
import static com.ceg.med.healthyrhythm.activity.MainActivity.HEALTHY_RHYTHM_LOG_TAG;

public class NiniGattCallback extends BluetoothGattCallback {
    /**
     * Service ID
     */
    private static final String NINI_SEND_PRESSURE = "0000ffb0-0000-1000-8000-00805f9b34fb";
    /**
     * Characteristics ID
     */
    private static final String SEND_DATA = "0000ffb2-0000-1000-8000-00805f9b34fb";
    private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();

    private BluetoothGatt bluetoothGatt;

    private static CallbackAble<Integer> callbackAble;

    public NiniGattCallback(CallbackAble<Integer> callback) {
        callbackAble = callback;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Log.d(HEALTHY_RHYTHM_LOG_TAG, "onConnectionStateChange: " + status + " -> " + newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // GATT Connected
            // Searching GATT Service
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // GATT Disconnected
            Log.d(HEALTHY_RHYTHM_LOG_TAG, "Bluetooth Disconnected");
        }
    }

    /**
     * naoki
     *
     * @param gatt
     * @param status
     */
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        Log.d(HEALTHY_RHYTHM_LOG_TAG, "onServicesDiscovered received: " + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            BluetoothGattService bluetoothGattService = gatt.getService(UUID.fromString(NINI_SEND_PRESSURE));
            Log.d(HEALTHY_RHYTHM_LOG_TAG, "service: " + bluetoothGattService.getUuid().toString());
            BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(UUID.fromString(SEND_DATA));
            Log.d(HEALTHY_RHYTHM_LOG_TAG, "characteristic: " + characteristic.getUuid().toString());
            boolean b = gatt.setCharacteristicNotification(characteristic, true);
            if (b) {
                for (BluetoothGattDescriptor bluetoothGattDescriptor : characteristic.getDescriptors()) {
                    bluetoothGattDescriptor.setValue(ENABLE_NOTIFICATION_VALUE);
                    writeGattDescriptor(bluetoothGattDescriptor);
                }
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        byte[] emg_data = characteristic.getValue();
        ByteReader byteReader = new ByteReader();
        byteReader.setByteData(emg_data);
//        String value = String.format("%02X %02X %02X", emg_data[2] - 0x30, emg_data[3] - 0x30, emg_data[5] - 0x30);
        int val = (emg_data[5] - 0x30) + (emg_data[3] - 0x30) * 10;
        callbackAble.callback(val);
//        Log.d("NEW VALUE: ", "value: " + value + " :int: " + val);
    }

    /**
     * naoki
     *
     * @param d
     */
    public void writeGattDescriptor(BluetoothGattDescriptor d) {
        //put the descriptor into the write queue
        descriptorWriteQueue.add(d);
        //if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
        if (descriptorWriteQueue.size() == 1) {
            bluetoothGatt.writeDescriptor(d);
        }
    }

    public void setBluetoothGatt(BluetoothGatt gatt) {
        bluetoothGatt = gatt;
    }

    public static void set(CallbackAble<Integer> callback) {
        callbackAble = callback;
        Log.d(HEALTHY_RHYTHM_LOG_TAG, "SET CALLBACK " + callback.toString());
    }

}
