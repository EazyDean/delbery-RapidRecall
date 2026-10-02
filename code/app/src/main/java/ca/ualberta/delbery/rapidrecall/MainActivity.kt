package ca.ualberta.delbery.rapidrecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import ca.ualberta.delbery.rapidrecall.ui.RapidRecallApp
import ca.ualberta.delbery.rapidrecall.ui.RapidRecallViewModel
import ca.ualberta.delbery.rapidrecall.ui.theme.RapidRecallTheme

/**
 * Android entry point for RapidRecall.
 *
 * The activity deliberately owns only platform setup. Screen state and game rules live in
 * [RapidRecallViewModel], so a configuration change does not erase the current session.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(this)[RapidRecallViewModel::class.java]

        setContent {
            RapidRecallTheme {
                RapidRecallApp(viewModel = viewModel)
            }
        }
    }
}
