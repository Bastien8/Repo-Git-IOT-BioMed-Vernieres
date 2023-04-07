package fr.isen.vernieres.androidsmartdevice

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.isen.vernieres.androidsmartdevice.databinding.ScanCellBinding

class DeviceBLEAdapter(var device: ArrayList<ScanActivity.ListBLE>, val onItemClickListener: (String, String) -> Unit) : RecyclerView.Adapter<DeviceBLEAdapter.ScanViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScanCellBinding.inflate(inflater, parent, false)
        return ScanViewHolder(binding)
    }

    override fun getItemCount(): Int =device.size

    override fun onBindViewHolder(holder: ScanViewHolder, position: Int) {
         holder.DeviceName.text = device[position].Nom

        holder.DeviceName.setOnClickListener {
            onItemClickListener(device[position].Nom, device[position].Adresse)
        }
    }

    class ScanViewHolder(binding: ScanCellBinding) : RecyclerView.ViewHolder(binding.root){
        val DeviceName = binding.DeviceName
    }


}
