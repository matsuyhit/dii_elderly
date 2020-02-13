package com.example.client_elderly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.*
import kotlinx.android.synthetic.main.activity_main.*
import androidx.appcompat.widget.Toolbar
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.*

import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

import java.io.StringWriter


// メッセージ用の識別子
const val MSG_CONNECTION_SUCCESS = 111 // 接続成功
const val MSG_CONNECTION_FAILED = 222  // 接続失敗
const val MSG_IOEXCEPTION = 333        // 例外発生

class MainActivity : AppCompatActivity() {

    private var tcpcom: ComTcpClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        setContentView(R.layout.activity_main)

        val channel = Channel<Int>()
        GlobalScope.launch(Dispatchers.Main) {
            when (channel.receive()) {
                MSG_CONNECTION_SUCCESS -> {
                    connectionStatus.text = "Successfully!!"
                }

                MSG_CONNECTION_FAILED -> {
                    connectionStatus.text = "Failed!!"
                    // エラー処理
                }

                MSG_IOEXCEPTION -> {
                    connectionStatus.text = "Error!!"
                    //エラー処理
                }
            }
        }

        connectionFab.setOnClickListener {
            val ip = editIpAddress.text.toString() // 今回は "192.168.10.7"が代入される。
            val port = editPort.text.toString()    // "55555"が代入される。

            if (!ip.isEmpty() && !port.isEmpty()) {
                tcpcom = ComTcpClient(ip, port.toInt(), channel)
                tcpcom?.connect()
                connectionStatus.text = "Connecting..."
            }
        }

        button1.setOnClickListener {
            tcpcom?.sendOrReceive { outputStream, _ ->
                outputStream.write("Activity_requested_1".toByteArray())
            }
        }

        button2.setOnClickListener {

            tcpcom?.sendOrReceive { outputStream, inputStream ->

                outputStream.write("Activity_requested_2".toByteArray())
                Log.d("received", "1")

                val reader = BufferedReader(inputStream.reader())
                var content: String
                try {
                    //Log.d("received", "before content")
                    content = reader.readText()
                    Log.d("received", "received text is:")
                    Log.d("received", content)
                    Log.d("received", "well done!")

                } finally {
                    reader.close()
                }
            }

            //ひょっとして、tcpcomは別のスレッドだから、このかっこを出てからtextを変更しないと怒られるのか??
            //だとしたら、上の変数がまだ生きているならここで変更すれば解決な気がする
            //getData.text = tmp.reader().toString()

        }

        buttonQuit.setOnClickListener {
            tcpcom?.sendOrReceive { outputStream, _ ->
                outputStream.write("ON_CLICK_BUTTON_QUIT".toByteArray())
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        tcpcom?.close()
    }
}