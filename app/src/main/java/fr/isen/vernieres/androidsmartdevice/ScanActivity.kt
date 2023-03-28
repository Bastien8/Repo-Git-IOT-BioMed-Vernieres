package fr.isen.vernieres.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import fr.isen.vernieres.androidsmartdevice.databinding.ActivityScanBinding
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass.Device
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private lateinit var adapter: DeviceBLEAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter= DeviceBLEAdapter(deviceList)
        binding.ScanList.adapter=adapter

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            val toast_err = Toast.makeText(applicationContext,"Votre appareil ne possède pas de bluetooth ", Toast.LENGTH_LONG)
            toast_err.show()
            binding.progressBar.isVisible=false
            binding.ScanList.isVisible=false
        } else {
            if (bluetoothAdapter.isEnabled) {
                val toast_val = Toast.makeText(applicationContext,"appareil prêt à l'utilisation",Toast.LENGTH_SHORT)
                toast_val.show()
                scanDeviceWithPermissions()

            } else {
                val toast = Toast.makeText(applicationContext, "Le bluetooth n'est pas actif, activer le svp", Toast.LENGTH_LONG)
                toast.show()
                binding.progressBar.isVisible=false
                binding.ScanList.isVisible=false
            }
        }

    }

    @SuppressLint("MissingPermission")
    override fun onStop() {
        super.onStop()
        if (bluetoothAdapter?.isEnabled == true && allPermissionGranted()) {
            scanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        }

    }
    @SuppressLint("MissingPermission")
    private fun ScanBLE() {

        binding.progressBar.isVisible=false


        binding.floatingActionButton2.setOnClickListener{
            if (!scanning) {
                binding.floatingActionButton2.setImageResource(android.R.drawable.ic_media_pause)
                binding.progressBar.isVisible=true
                handler.postDelayed({
                    scanning = false
                    bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
                }, SCAN_PERIOD)
                scanning = true
                bluetoothAdapter?.bluetoothLeScanner?.startScan(leScanCallback)

            } else {
                binding.floatingActionButton2.setImageResource(android.R.drawable.ic_media_play)
                binding.progressBar.isVisible=false
                scanning = false
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
            }

        }
    }

    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all {it.value}){
                ScanBLE()
            }

        }

    private fun getAllPermissions(): Array<String>{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun scanDeviceWithPermissions(){
        if(allPermissionGranted()) {
            ScanBLE()
        } else {
            requestPermissionLauncher.launch(getAllPermissions())
        }
    }

    private fun allPermissionGranted(): Boolean {
        val allPermissions = getAllPermissions()
        return allPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    }

    private val bluetoothAdapter: BluetoothAdapter by
    lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())

    private var Devices: ArrayList<BLE> = ArrayList()

    //Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000
    private val deviceList = ArrayList<BluetoothDevice>()


    private val leDeviceListAdapter = Device()
    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            adapter.addDevice(result.device)
            /*var ble = BLE()
            ble.name = result.device.name
            ble.address = result.device.address
            Devices.add(ble)*/


        }
    }

    class BLE {
        lateinit var name: String
        lateinit var address: String
    }
}


