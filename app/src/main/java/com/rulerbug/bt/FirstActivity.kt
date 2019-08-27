package com.rulerbug.bt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_first.*

class FirstActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        bt.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(Intent(this@FirstActivity, ChengActivity::class.java))
            }

        })
        bt_close.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                BluetoothUtils.CloseBluetooth(this@FirstActivity)
            }

        })
    }
}
