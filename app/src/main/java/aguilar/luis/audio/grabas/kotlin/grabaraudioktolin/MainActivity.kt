package aguilar.luis.audio.grabas.kotlin.grabaraudioktolin

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {
    lateinit var recorder: MediaRecorder
    lateinit var player: MediaPlayer
    lateinit var file: File
    lateinit var button1: Button
    lateinit var button2: Button
    lateinit var tv1: TextView
    var sum=0
    var prefs: SharedPreferences? = null
    lateinit var context:Context
    // coding180.com
    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate (savedInstanceState)
        setContentView(R.layout.activity_main)

        tv1 = findViewById <TextView>(R.id.textView) as TextView
        button1 = findViewById<Button> (R.id.button) as Button
        button2 = findViewById <Button>(R.id.button2) as Button

        var array = arrayListOf<String>()
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,array)

        prefs = this.getSharedPreferences("Audios", 0)
        sum=prefs!!.getInt("valor",0)
        Toast.makeText(this,sum.toString(),Toast.LENGTH_SHORT).show()
        for (x in 1..sum) {
            array.add("Grabacion: "+x)
        }
        list.adapter=adapter


        button1.setOnClickListener {
            recorder = MediaRecorder()
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            val path = File (Environment.getExternalStorageDirectory().getPath())
            try {
                file = File.createTempFile ("temporary", ".3gp", path)
            } catch (e: IOException) {
            }

            recorder.setOutputFile(file.absolutePath)
            try {
                recorder.prepare ()
            } catch (e: IOException) {
            }

            recorder.start()
            tv1.text = "Recording"
            button1.setEnabled(false)
            button2.setEnabled(true)
        }

        button2.setOnClickListener {
            recorder.stop ()
            recorder.release ()
            player = MediaPlayer ()
            player.setOnCompletionListener (this)
            try {
                player.setDataSource(file.absolutePath)

                prefs = this.getSharedPreferences("Audios", 0)
                val editor = prefs!!.edit()
                sum++
                editor.putString("Audio"+sum, file.absolutePath.toString())
                editor.putInt("valor",sum)
                editor.apply()
if (sum==0){sum=1}
                array.clear()
                sum=prefs!!.getInt("valor",0)
                for (x in 1..sum) {
                    array.add("Grabacion: "+x)
                }
                list.adapter=adapter

            } catch (e: IOException) {
            }

            try {
                player.prepare ()
            } catch (e: IOException) {
            }

            button1.setEnabled (true)
            button2.setEnabled (false)
            tv1.text = "Ready to play"
        }



        list.setOnItemClickListener(AdapterView.OnItemClickListener{parent, view, position, id ->
            prefs = this.getSharedPreferences("Audios", 0)

            player = MediaPlayer ()
            player.setOnCompletionListener (this)
            try {
                player.setDataSource(prefs!!.getString("Audio"+(position+1),"Nulo"))

            } catch (e: IOException) {
            }

            try {
                player.prepare ()
            } catch (e: IOException) {
            }
            player.start()
            button1.setEnabled (false);
            button2.setEnabled (false);
            tv1.setText ("Playing");

        })
    }

    override fun onCompletion (mp: MediaPlayer) {
        button1.setEnabled (true)
        button2.setEnabled (true)
        tv1.setText ("Ready")
    }

}
