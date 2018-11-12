package com.ceg.med.healthyrhythm.list;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;

/**
 * Represents one Item of the Myo List
 */
public class MyoListItem {

    private final String name;
    private final String macAdress;
    private final BluetoothDevice device;
    private final ScanRecord scanRecord;

    public MyoListItem(String name, String macAdress, BluetoothDevice device, ScanRecord scanRecord) {
        this.name = name;
        this.macAdress = macAdress;
        this.device = device;
        this.scanRecord = scanRecord;
    }

    public String getName() {
        return name;
    }

    public String getMacAdress() {
        return macAdress;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public ScanRecord getScanRecord() {
        return scanRecord;
    }

}
