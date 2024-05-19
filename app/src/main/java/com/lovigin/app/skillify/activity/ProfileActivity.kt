package com.lovigin.app.skillify.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.element.AvatarComponent
import com.lovigin.app.skillify.activity.element.BackButton
import com.lovigin.app.skillify.`object`.User
import com.lovigin.app.skillify.ui.theme.BrandBlue
import com.lovigin.app.skillify.ui.theme.BrandLightRed
import com.lovigin.app.skillify.ui.theme.SkillifyTheme
import com.lovigin.app.skillify.worker.NotificationSender

class ProfileActivity : ComponentActivity() {

    var user = mutableStateOf(App.userViewModel.user.value)
    var id = mutableStateOf("")

    private fun loadUser(id: String?) {
        Firebase.firestore
            .collection("users")
            .document(id!!)
            .get().addOnSuccessListener { u ->
                if (u.exists()) {
                    user.value = u.toObject(User::class.java)!!
                }
            }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SkillifyTheme {
                val viewModel = App.userViewModel

                if (intent.hasExtra("idUser")) {
                    id.value = intent.getStringExtra("idUser").toString()
                    loadUser(id.value)
                } else {
                    id.value = App.userViewModel.user.value?.id.toString()
                }

                var expanded by remember { mutableStateOf(false) }
                val contacts = listOf(
                    stringResource(R.string.share_profile_str),
                    stringResource(R.string.add_to_favorites_str),
                    if (viewModel.auth.currentUser != null && viewModel.user.value!!.blockedUsers.contains(
                            id.value
                        )
                    ) stringResource(R.string.unblock_user_str)
                    else stringResource(R.string.block_user_str)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = rememberScrollState())
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            BrandBlue.copy(0.7f),
                                            BrandLightRed.copy(0.7f)
                                        )
                                    )
                                )
                                .systemBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                BackButton(onClick = { finish() })

                                Box {
                                    IconButton(onClick = {
                                        expanded = !expanded
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = "More"
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        contacts.forEach { contact ->
                                            DropdownMenuItem(
                                                text = { Text(text = contact) },
                                                onClick = {
                                                    when (contact) {
                                                        getString(R.string.block_user_str) -> {
                                                            val update: MutableList<String> =
                                                                viewModel.user.value!!.blockedUsers
                                                            update.add(id.value)

                                                            viewModel.updateData(
                                                                "users",
                                                                viewModel.user.value!!.id,
                                                                mapOf("blockedUsers" to update)
                                                            )
                                                            Log.d("TAG", "onCreate: blocked")
                                                        }

                                                        getString(R.string.unblock_user_str) -> {
                                                            val update: MutableList<String> =
                                                                viewModel.user.value!!.blockedUsers
                                                            update.remove(id.value)

                                                            viewModel.updateData(
                                                                "users",
                                                                viewModel.user.value!!.id,
                                                                mapOf("blockedUsers" to update)
                                                            )
                                                            Log.d("TAG", "onCreate: unblocked")
                                                        }

                                                        else -> {
                                                            Toast.makeText(
                                                                this@ProfileActivity,
                                                                getString(R.string.in_development_str),
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            } // row with Back button

                            user.value?.let {
                                AvatarComponent(
                                    url = it.urlAvatar,
                                    contentDescription = "Avatar",
                                    padding = 5.dp
                                )
                            }

                            user.value?.let {
                                Text(
                                    text = "${it.first_name} ${it.last_name}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = "@${it.nickname}",
                                )
                            }
                        } // column top view

                        user.value?.let {
                            if (it.bio.isNotEmpty()) {
                                Text(
                                    text = it.bio,
                                    modifier = Modifier.padding(
                                        top = 15.dp,
                                        start = 15.dp,
                                        end = 15.dp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.padding(vertical = 20.dp)
                        ) {
                            Row {
                                Column(
                                    Modifier
                                        .padding(15.dp)
                                        .clickable {
                                            startActivity(
                                                Intent(
                                                    this@ProfileActivity,
                                                    FollowActivity::class.java
                                                )
                                                    .putExtra("type", 0)
                                                    .putExtra("idUser", user.value!!.id)
                                            )
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    user.value?.let { Text(text = "${it.subscribers.size}") }
                                    Text(text = getString(R.string.subscribers_str))
                                }

                                Column(
                                    Modifier
                                        .padding(15.dp)
                                        .clickable {
                                            startActivity(
                                                Intent(
                                                    this@ProfileActivity,
                                                    FollowActivity::class.java
                                                )
                                                    .putExtra("type", 1)
                                                    .putExtra("idUser", user.value!!.id)
                                            )
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    user.value?.let { Text(text = "${it.subscriptions.size}") }
                                    Text(text = getString(R.string.subscriptions_str))
                                }
                            }
                        } // card followers

                        if (intent.hasExtra("idUser")) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Button(
                                    onClick = { /*TODO*/ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                                ) {
                                    Text(text = stringResource(R.string.message_str), fontSize = 15.sp)
                                }
                                IconButton( // subscribe button
                                    onClick = {
                                        if (viewModel.user.value!!.subscriptions.contains(id.value))  {
                                            viewModel.deleteData(
                                                "users",
                                                viewModel.user.value!!.id,
                                                mapOf("subscriptions" to id.value)
                                            )

                                            viewModel.deleteData(
                                                "users",
                                                id.value,
                                                mapOf("subscribers" to viewModel.user.value!!.id)
                                            )

                                            user.value!!.subscribers.remove(viewModel.user.value!!.id)
                                            viewModel.user.value!!.subscriptions.remove(id.value)
                                        } else {
                                            viewModel.addData(
                                                "users",
                                                viewModel.user.value!!.id,
                                                mapOf("subscriptions" to id.value)
                                            )

                                            viewModel.addData(
                                                "users",
                                                id.value,
                                                mapOf("subscribers" to viewModel.user.value!!.id)
                                            )

                                            user.value!!.subscribers.add(viewModel.user.value!!.id)
                                            viewModel.user.value!!.subscriptions.add(id.value)
                                            NotificationSender.sendNotification(this@ProfileActivity, id.value, "New subscriber ${viewModel.user.value!!.first_name}")
                                        }
                                        restartActivity()
                                    },
                                    Modifier
                                        .padding(start = 15.dp)
                                        .clip(
                                            CircleShape
                                        )
                                        .background(BrandBlue)
                                        .size(40.dp),
                                ) {
                                    Icon(
                                        painter = if (viewModel.user.value!!.subscriptions.contains(
                                                user.value!!.id
                                            ) && viewModel.user.value!!.subscribers.contains(
                                                user.value!!.id
                                            )
                                        ) painterResource(id = R.drawable.fi_rr_friends) else if (viewModel.user.value!!.subscriptions.contains(
                                                user.value!!.id
                                            )
                                        ) painterResource(id = R.drawable.fi_rr_user_remove)
                                        else painterResource(id = R.drawable.fi_rr_user_add),
                                        contentDescription = "Add",
                                        tint = Color.White,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = stringResource(R.string.my_skills_str),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .padding(horizontal = 15.dp),
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold
                        )

                        if (user.value!!.selfSkills.isNotEmpty()) {
                            user.value!!.selfSkills.forEach { u ->
                                Card(
                                    modifier = Modifier
                                        .padding(vertical = 5.dp)
                                        .padding(horizontal = 15.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = u.name)
                                        u.level?.let { Text(text = it) }
                                    }
                                }
                            }
                        } else {
                            Text(text = stringResource(
                                R.string.didn_t_set_self_skills_txt,
                                user.value!!.first_name
                            ))
                        }

                        Text(
                            text = getString(R.string.learning_skills_str),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .padding(horizontal = 15.dp),
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold
                        )

                        if (user.value!!.learningSkills.isNotEmpty()) {
                            user.value!!.learningSkills.forEach { u ->
                                Card(
                                    modifier = Modifier
                                        .padding(vertical = 5.dp)
                                        .padding(horizontal = 15.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = u.name)
                                        u.level?.let { Text(text = it) }
                                    }
                                }
                            }
                        } else {
                            Text(text = stringResource(
                                R.string.didn_t_set_learning_skills_txt,
                                user.value!!.first_name
                            ))
                        }

                        Spacer(modifier = Modifier.padding(25.dp))
                    }
                }
            }
        }
    }

    private fun restartActivity() {
        Handler(Looper.getMainLooper()).post {
            val intent = Intent(this, ProfileActivity::class.java).putExtra("idUser", id.value)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}
