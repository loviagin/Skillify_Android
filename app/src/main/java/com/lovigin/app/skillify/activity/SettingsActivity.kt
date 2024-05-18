package com.lovigin.app.skillify.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.element.BackButton
import com.lovigin.app.skillify.activity.ui.theme.SkillifyTheme

class SettingsActivity : ComponentActivity() {

    private var counter = mutableIntStateOf(0)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = App.userViewModel

            var text by remember { mutableStateOf("") }
            SkillifyTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Settings") },
                            navigationIcon = {
                                BackButton {
                                    finish()
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        SettingsItemView(title = "Delete account", description = "") {
                            val intent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://skillify.space/delete-account")
                                )
                            startActivity(intent)
                        }
                        SettingsItemView(title = "App info", description = "") {
                            counter.intValue++
                            if (counter.intValue > 4) {
                                Toast.makeText(
                                    this@SettingsActivity,
                                    "You tapped ${counter.intValue}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        if (counter.intValue > 7) {
                            AlertDialog(
                                onDismissRequest = {},
                                confirmButton = {
                                    Button(onClick = {
                                        if (text == "1234567890") {
                                            Toast.makeText(
                                                this@SettingsActivity,
                                                "You're now a PRO user",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            viewModel.makePro()
                                            counter.intValue = 0
                                        }
                                    }) {
                                        Text("Enter PRO")
                                    }
                                },
                                title = { Text("Enter password") },
                                text = { TextField(value = text, onValueChange = { text = it }) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsItemView(title: String, description: String, onClickEvent: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable {
                onClickEvent()
            }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title)
            Text(text = description)
        }
    }
}
