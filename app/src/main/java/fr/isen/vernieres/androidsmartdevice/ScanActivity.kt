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
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class ScanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    private fun ScanBLE() {
        var scanning = true
        binding.progressBar.isVisible=false
        binding.ScanList.isVisible=false


        binding.ScanList.layoutManager=LinearLayoutManager(this)
        binding.ScanList.adapter=DeviceBLEAdapter(arrayListOf("Device 1","Device 2","Device 3 "))

        binding.floatingActionButton2.setOnClickListener{
            if (scanning == true) {
                binding.floatingActionButton2.setImageResource(android.R.drawable.ic_media_pause)
                binding.progressBar.isVisible=true
                binding.ScanList.isVisible=true
            }
            else {
                binding.floatingActionButton2.setImageResource(android.R.drawable.ic_media_play)
                binding.progressBar.isVisible=false
                binding.ScanList.isVisible=false
            }
            scanning = !scanning
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

   // private val bluetoothLeScanner = BluetoothAdapter.bluetoothLeScanner
    private var scanning = false
    private val handler = Handler()

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    /*private fun scanLeDevice() {
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }*/
}


