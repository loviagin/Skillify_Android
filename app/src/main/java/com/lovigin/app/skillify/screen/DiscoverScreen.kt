package com.lovigin.app.skillify.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.element.UserView
import com.lovigin.app.skillify.`object`.User
import com.lovigin.app.skillify.ui.theme.BrandBlue
import com.lovigin.app.skillify.ui.theme.BrandLightRed

@Composable
fun DiscoverScreen(
    context: Context
) {
    var allUsers by remember { mutableStateOf(listOf<User>()) }
    var search by remember { mutableStateOf("") }

    val filteredUsers = remember(search, allUsers) {
        if (search.isEmpty()) {
            allUsers
        } else {
            allUsers.filter { user ->
                user.first_name.contains(search, ignoreCase = true) || user.last_name.contains(
                    search,
                    ignoreCase = true
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAllUsers { users ->
            allUsers = users
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.banner1),
                contentDescription = "News 1",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                contentScale = ContentScale.FillWidth
            )
        }

        item {
            Row {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(15.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        BrandBlue,  // Начальный цвет
                                        BrandLightRed  // Конечный цвет
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(100f, 600f)
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.fi_rr_film),
                            contentDescription = "Video",
                            Modifier
                                .size(50.dp)
                                .padding(bottom = 5.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Text(
                            text = stringResource(R.string.courses_str),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.coming_soon_str),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(15.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        BrandBlue,  // Начальный цвет
                                        BrandLightRed  // Конечный цвет
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(100f, 600f)
                                )
                            )
                            .padding(horizontal = 10.dp, vertical = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.fi_rr_groups),
                            contentDescription = "Chat groups",
                            Modifier
                                .size(50.dp)
                                .padding(bottom = 5.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Text(
                            text = stringResource(R.string.chat_groups_txt),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.coming_soon_str),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(15.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandBlue)
                            .padding(horizontal = 10.dp, vertical = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.fi_sr_star),
                            contentDescription = "Pro",
                            Modifier
                                .size(50.dp)
                                .padding(bottom = 5.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                        Text(
                            text = stringResource(R.string.pro_str),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        )
                        Text(
                            text = stringResource(R.string.coming_soon_str),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        item {
            TextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 10.dp),
                placeholder = { Text(text = stringResource(R.string.search_str)) },
                maxLines = 1
            )
        }

        items(filteredUsers) { user ->
            UserView(user = user, context)
        }
    }
}


private fun loadAllUsers(onUsersLoaded: (List<User>) -> Unit) {
    Firebase.firestore
        .collection("users")
        .whereNotEqualTo("first_name", "")
        .whereLessThan("blocked", 3)
        .get()
        .addOnSuccessListener {
            val users = it?.map { doc ->
                doc.toObject(User::class.java)
            } ?: emptyList()
            onUsersLoaded(users)
        }
        .addOnFailureListener {
            Log.d("TAG", "loadUsers: $it")
        }
}