package fr.isen.vernieres.androidsmartdevice

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.vernieres.androidsmartdevice.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    private lateinit var adapter: DeviceBLEAdapter
    private val DeviceList = ArrayList<ListBLE>()
    private var bluetoothGatt: BluetoothGatt? = null
    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter= DeviceBLEAdapter(arrayListOf())
        binding.ScanList.adapter=adapter


        if (bluetoothAdapter == null) {
            val toast_err = Toast.makeText(applicationContext,"Votre appareil ne possède pas de bluetooth ", Toast.LENGTH_LONG)
            toast_err.show()
            binding.progressBar.isVisible=false
            binding.ScanList.isVisible=false
        } else {
            if (bluetoothAdapter?.isEnabled == true) {
                val toast_val = Toast.makeText(applicationContext,"appareil prêt à l'utilisation",Toast.LENGTH_SHORT)
                toast_val.show()
                scanDeviceWithPermissions()
                binding.progressBar.isVisible=false

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
        if (!scanning) {
            handler.postDelayed({
                scanning = false
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothAdapter?.bluetoothLeScanner?.startScan(leScanCallback)

        } else {
            scanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
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
            initToggleActions()
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

    private val bluetoothAdapter: BluetoothAdapter? by
    lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())

    //Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            var multi = false
            val NewDevice = ListBLE()
            //adapter.addDevice(result.device)
            //adapter.notifyDataSetChanged()
            if(result.device.name != null){
                if(DeviceList.isNotEmpty()){
                    for(i in 0 until DeviceList.size){
                        if(DeviceList[i].Nom == result.device.name){
                            multi = true
                        }
                    }
                    if(multi == false){
                        Log.w(" SCAN ", "Device Name = ${result.device.name}")
                        NewDevice.addDevice(result.device.name,result.device.address)
                        DeviceList.add(NewDevice)
                    }
                } else {
                    Log.w(" SCAN ", "Device Name = ${result.device.name}")
                    NewDevice.addDevice(result.device.name,result.device.address)
                    DeviceList.add(NewDevice)
                }
                binding.ScanList.adapter = DeviceBLEAdapter(DeviceList)
            }



        }
    }
    private fun initToggleActions() {
        binding.floatingActionButton2.setOnClickListener {
            binding.floatingActionButton2.setImageResource(android.R.drawable.ic_media_pause)
            ScanBLE()
            togglePlayPauseAction()
        }
        binding.ScanList.layoutManager = LinearLayoutManager(this)
        adapter = DeviceBLEAdapter(arrayListOf())
        binding.ScanList.adapter = adapter
    }
    private fun togglePlayPauseAction() {
        if (scanning) {
            binding.floatingActionButton2.setImageResource(android.R.drawable.ic_media_pause)
            binding.progressBar.isVisible = true
        } else {
            binding.floatingActionButton2.setImageResource(android.R.drawable.ic_media_play)
            binding.progressBar.isVisible = false
        }
    }
    class ListBLE {
        var Nom : String = ""
        var Adresse : String = ""

        fun addDevice (Name : String, address : String){
            Nom = Name
            Adresse = address
        }
    }
    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                // connect to the GATT server on the device
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
                return true
            } catch (exception: IllegalArgumentException) {
                Log.w("connect", "Device not found with provided address.  Unable to connect.")
                return false
            }
        } ?: run {
            return false
        }

    }
}


