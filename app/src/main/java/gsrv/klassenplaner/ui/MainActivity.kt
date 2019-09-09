package gsrv.klassenplaner.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gsrv.klassenplaner.R
import gsrv.klassenplaner.notifications.NotificationChannelHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NotificationChannelHelper(this).createChannels()
    }
}
