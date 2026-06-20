package com.CecilStLabs.heartmonitor

import android.Manifest
import android.util.Log

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.CecilStLabs.heartmonitor.ui.theme.HeartMonitorTheme

class HeartMonitorScanCallback : android.bluetooth.le.ScanCallback()
{

    override fun onScanResult(callbackType: Int, result: ScanResult?)
    {
        if (null != result) {
            Log.d("HeartMonitor", "FOUND: " + result.scanRecord?.deviceName)
        }
    }

    /**
     * Callback when batch results are delivered.
     *
     * @param results List of scan results that are previously scanned.
     */
    override fun onBatchScanResults(results: MutableList<ScanResult?>?)
    {
        Log.d("HeartMonitor", "batch")
    }

    /**
     * Callback when scan could not be started.
     *
     * @param errorCode Error code (one of SCAN_FAILED_*) for scan failure.
     */
    /*override fun onScanFailed(@ScanCallback.ScanFailed errorCode: Int)
    {
        Log.d("HeartMonitor", "FAILED: $errorCode")
    }*/
}

class MainActivity : ComponentActivity() {

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    // true indicates the ble adapter is actively scanning
    private var scanning = false

    private val bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner

    private val handler = Handler()

    //private val leDeviceListAdapter = LeDeviceListAdapter()

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback()
    {
        override fun onScanResult(callbackType: Int, result: ScanResult)
        {
            super.onScanResult(callbackType, result)

            Log.d("HeartMonitor", "Found " + result.device)

            // i have no idea what this is
            //leDeviceListAdapter.addDevice(result.device)
            //leDeviceListAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()

        if (bluetoothAdapter == null) {

            setContent {
                HeartMonitorTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Greeting(
                            name = "Device doesn't support Bluetooth",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
        else
        {
            setContent {
                HeartMonitorTheme {
                    Scaffold(modifier = Modifier.fillMaxSize())
                    { innerPadding ->
                        Greeting(
                            name = "Scanning ...",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun scanLeDevice()
    {
        if (!scanning)
        {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                   scanning = false
                   bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)

            Log.d("HeartMonitor", "starting scanning...")
            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        }
        else
        {
            Log.d("HeartMonitor", "starting scanning...")
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HeartMonitorTheme {
        Greeting("")
    }
}

