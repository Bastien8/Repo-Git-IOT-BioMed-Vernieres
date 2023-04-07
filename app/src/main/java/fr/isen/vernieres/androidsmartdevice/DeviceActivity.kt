package fr.isen.vernieres.androidsmartdevice

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.view.isVisible
import fr.isen.vernieres.androidsmartdevice.databinding.ActivityDeviceBinding
import java.util.*

@SuppressLint("MissingPermission")
class DeviceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeviceBinding

    private val bluetoothAdapter: BluetoothAdapter? by
    lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private var bluetoothGatt: BluetoothGatt? = null

    private val serviceUUID = UUID.fromString("0000feed-cc7a-482a-984a-7f2ed5b3e58f")
    private val characteristicLedUUID = UUID.fromString("0000abcd-8e22-4541-9d4c-21edae82ed19")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val address = intent.getStringExtra("address")

        binding.Title.text = name


        connect(address!!)
        var ledBlueOn = false
        var ledGreenOn = false
        var ledRedOn = false

        binding.Led1.setOnClickListener {
            val service = bluetoothGatt?.getService(serviceUUID)
            val characteristic = service?.getCharacteristic(characteristicLedUUID)
            if(ledBlueOn) {
                binding.Led1.clearColorFilter()
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else {
                binding.Led1.setColorFilter(Color.BLUE)
                characteristic?.value = byteArrayOf(0x01)
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
            ledBlueOn = !ledBlueOn
            ledGreenOn = false
            ledRedOn = false
            binding.Led2.clearColorFilter()
            binding.Led3.clearColorFilter()
        }
        binding.Led2.setOnClickListener {
            val service = bluetoothGatt?.getService(serviceUUID)
            val characteristic = service?.getCharacteristic(characteristicLedUUID)
            if(ledGreenOn) {
                binding.Led2.clearColorFilter()
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else {
                binding.Led2.setColorFilter(Color.GREEN)
                characteristic?.value = byteArrayOf(0x02)
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
            ledGreenOn = !ledGreenOn
            ledBlueOn = false
            ledRedOn = false
            binding.Led1.clearColorFilter()
            binding.Led3.clearColorFilter()
        }
        binding.Led3.setOnClickListener {
            val service = bluetoothGatt?.getService(serviceUUID)
            val characteristic = service?.getCharacteristic(characteristicLedUUID)
            if(ledRedOn) {
                binding.Led3.clearColorFilter()
                characteristic?.value = byteArrayOf(0x00)
                bluetoothGatt?.writeCharacteristic(characteristic)
            } else {
                binding.Led3.setColorFilter(Color.RED)
                characteristic?.value = byteArrayOf(0x03)
                bluetoothGatt?.writeCharacteristic(characteristic)
            }
            ledRedOn = !ledRedOn
            ledGreenOn = false
            ledBlueOn = false
            binding.Led1.clearColorFilter()
            binding.Led2.clearColorFilter()

        }

    }


    /*override fun onStop(){
        super.onStop()
        bluetoothGatt?.close()
    }*/


    //private fun displayContentConnected(){
       // binding.Title.text = getString(R.string.Title)
       // binding.progressBar2.isVisible = false
       // binding.imageView2.isVisible =  true
    //}

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

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt?.discoverServices()
                // successfully connected to the GATT Server
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
            }
        }
    }


}