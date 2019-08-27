package com.rulerbug.bt

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class ChengActivity : AppCompatActivity() {
    private var DevicesArrayAdapter: ArrayAdapter<String>? = null

    /**
     * changes the title when discovery is finished
     */
    private val mFindBlueToothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.e("1123", "anew")
            val action = intent.action
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Get the BluetoothDevice object from the Intent
                val device = intent
                        .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                // If it's already paired, skip it, because it's been listed
                // already
                if (device.bondState != BluetoothDevice.BOND_BONDED) {
                    Log.e("1123", "anew")
                    DevicesArrayAdapter!!.add(device.name + "\n"
                            + device.address)
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                setProgressBarIndeterminateVisibility(false)
                //                setTitle(R.string.select_bluetooth_device);
                Log.i("tag", "finish discovery" + (DevicesArrayAdapter!!.count - 2))
                if (DevicesArrayAdapter!!.count == 0) {
                    val noDevices = "null"
                    DevicesArrayAdapter!!.add(noDevices)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initBroadcast()
        initView()
        initBluetooth()
        initClick()
    }


    private fun initClick() {
        lv.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                bluetoothAdapter!!.cancelDiscovery()
                val info = (view as TextView).text.toString()
                val address = info.substring(info.length - 17)
                val intent = Intent(this@ChengActivity, BluetoothService::class.java)
                intent.putExtra(BluetoothService.MAC_KEY, address)
                startService(intent)
                finish()
            }

        })
    }

    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when (msg!!.what) {
                0 -> {
                    Toast.makeText(this@ChengActivity, msg.obj as String, Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    Toast.makeText(this@ChengActivity, "连接失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    var bluetoothAdapter: BluetoothAdapter? = null
    private val REQUEST_ENABLE_BT: Int = 2

    private fun initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter!!.isEnabled) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_ENABLE_BT)
            } else {
                getList()
            }
        }
        lv.postDelayed(object : Runnable {
            override fun run() {
                DevicesArrayAdapter!!.add("未扫描")
                if (bluetoothAdapter!!.isDiscovering()) {
                    bluetoothAdapter!!.cancelDiscovery()
                }
                bluetoothAdapter!!.startDiscovery()
            }

        }, 2000)

    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            getList()
        }
    }

    private fun getList() {
        val bondedDevices = bluetoothAdapter!!.bondedDevices
        for (bondedDevice in bondedDevices) {
            DevicesArrayAdapter!!.add(bondedDevice.name + "\n"
                    + bondedDevice.address)
        }
    }

    private fun initView() {
        DevicesArrayAdapter = ArrayAdapter(this, R.layout.bluetooth_device_name_item)
        lv.adapter = DevicesArrayAdapter
    }

    private fun initBroadcast() {
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(mFindBlueToothReceiver, filter)
        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(mFindBlueToothReceiver, filter)
    }
}
