package fr.isen.vernieres.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val button: Button = findViewById(R.id.button)
        button.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        })



    }
}