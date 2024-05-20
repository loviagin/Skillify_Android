package com.lovigin.app.skillify.screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.App.Companion.sharedPreferences
import com.lovigin.app.skillify.Const.icons
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.HeaderApp
import com.lovigin.app.skillify.activity.ProfileActivity
import com.lovigin.app.skillify.activity.element.GridScreen
import com.lovigin.app.skillify.activity.element.HomeText
import com.lovigin.app.skillify.activity.element.ImageComponent
import com.lovigin.app.skillify.model.UserViewModel
import com.lovigin.app.skillify.`object`.User
import com.lovigin.app.skillify.ui.theme.BrandBlue
import com.lovigin.app.skillify.ui.theme.BrandLightRed


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navHostController: NavHostController,
    context: Context
) {
    val viewModel by remember { mutableStateOf(App.userViewModel) }

    var selectedTab by remember { mutableIntStateOf(0) }
    val chosenSkills = remember { mutableStateListOf<String>() }

    val users = remember { mutableStateListOf<User>() }

    LaunchedEffect(true) {
        loadUsers(users)
    }

    LazyColumn {
        item {
            HeaderApp(context = context, navController = navHostController)
        }
        item {
            HomeText(text = stringResource(R.string.our_top_users_str))
            TopProUsersView(
                urlAvatar = viewModel.user.value?.urlAvatar,
                context = context,
                viewModel
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp)
                    .padding(end = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeText(text = stringResource(R.string.search_by_str))
                TabRow(
                    selectedTabIndex = selectedTab
                ) {
                    Tab(
                        selected = selectedTab == 0, onClick = {
                            chosenSkills.clear()
                            selectedTab = 0
                        },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.Black,
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 6.dp,
                                    topEnd = 6.dp,
                                    bottomEnd = 0.dp,
                                    bottomStart = 0.dp
                                )
                            )
                            .background(if (selectedTab == 0) BrandBlue else Color.White)
                            .padding(5.dp)
                    ) {
                        Text(text = stringResource(R.string.self_skill_txt))
                    }

                    Tab(
                        selected = selectedTab == 1, onClick = {
                            chosenSkills.clear()
                            selectedTab = 1
                        },
                        selectedContentColor = Color.White,
                        unselectedContentColor = Color.Black,
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(
                                    topStart = 6.dp,
                                    topEnd = 6.dp,
                                    bottomEnd = 0.dp,
                                    bottomStart = 0.dp
                                )
                            )
                            .background(if (selectedTab == 1) BrandBlue else Color.White)
                            .padding(4.dp)
                    ) {
                        Text(text = stringResource(R.string.learning_skills_str))
                    }
                }
            } // row with Searching text
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BrandBlue.copy(0.19f))
                    .padding(5.dp)

            ) {
                if (viewModel.user.value != null) { // user is authed
                    if (selectedTab == 0) { // self skills
                        if (viewModel.user.value!!.selfSkills.isNotEmpty()) { // selfSkills isn't empty
                            FlowRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                viewModel.user.value!!.selfSkills.forEach {
                                    Chip(
                                        onClick = {
                                            if (chosenSkills.contains(it.name))
                                                chosenSkills.remove(it.name)
                                            else
                                                chosenSkills.add(it.name)
                                        },
                                        colors = ChipDefaults.chipColors(
                                            backgroundColor = if (chosenSkills.contains(it.name)) BrandBlue.copy(
                                                0.5f
                                            ) else Color.White
                                        ),
                                        modifier = Modifier
                                            .padding(top = 5.dp)
                                            .padding(horizontal = 5.dp),
                                        leadingIcon = {
                                            icons[it.name]?.let { it1 ->
                                                Text(
                                                    text = it1,
                                                    modifier = Modifier.padding(5.dp)
                                                )
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = it.name,
                                            modifier = Modifier.padding(vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            OfferSetSkills(navHostController)
                        }
                    } else {
                        if (viewModel.user.value!!.learningSkills.isNotEmpty()) { // selfSkills isn't empty
                            FlowRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                            }
                        } else {
                            OfferSetSkills(navHostController)
                        }
                    }
                } else {
                    WelcomeHomeSkills(navHostController)
                }
            }
        }

        item {
            if (chosenSkills.isNotEmpty()) {
                GridScreen(users.filter { user ->
                    if (selectedTab == 1) {
                        chosenSkills.any { skill ->
                            user.selfSkills.any { it.name == skill }
                        }
                    } else {
                        chosenSkills.any { skill ->
                            user.learningSkills.any { it.name == skill }
                        }
                    }
                }, 2, context)
            } else {
                GridScreen(users = users, 2, context)
            }
        }
    }
}

@Composable
fun OfferSetSkills(navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.to_get_more_experience_with_our_app_txt),
            fontWeight = FontWeight.Bold
        )
        Text(text = stringResource(R.string.please_set_your_skills_in_the_account_tab_txt))
        Button(onClick = {
            navHostController.navigate("account")
        }) {
            Text(stringResource(R.string.go_to_account_txt))
        }
    }
}

@Composable
fun WelcomeHomeSkills(navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.to_get_more_experience_with_our_app_txt),
            fontWeight = FontWeight.Bold
        )
        Text(text = stringResource(R.string.please_login_or_sign_up_in_skillify_txt))
        Button(onClick = {
            navHostController.navigate("account")
        }) {
            Text(stringResource(R.string.go_to_auth_page_txt))
        }
    }
}

@Composable
fun TopProUsersView(urlAvatar: String? = null, context: Context, viewModel: UserViewModel) {
    val proUsers = remember { mutableStateListOf<User>() }

    LaunchedEffect(true) {
        loadProUsers(proUsers)
    }

    LazyRow(
        modifier = Modifier.padding(start = 8.dp)
    ) {
        // Проверка и отображение текущего пользователя, если есть urlAvatar
        if (urlAvatar != null) {
            item {
                Box {
                    ProUser(
                        id = App.userViewModel.user.value?.id ?: "",
                        urlAvatar = urlAvatar,
                        name = App.userViewModel.user.value?.first_name ?: "",
                        context = context
                    )
                    if (!App.isUserPro(App.userViewModel.user.value!!.pro)) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 30.dp, end = 12.dp)
                                .clip(CircleShape)
                                .background(BrandBlue)
                                .size(20.dp)
                        )
                    }
                }
            }
        }

        items(proUsers) { user ->
            ProUser(
                id = user.id,
                urlAvatar = user.urlAvatar,
                name = user.first_name,
                context = context
            )
        }
    }
}

@Composable
fun ProUser(
    id: String,
    urlAvatar: String,
    name: String,
    context: Context,
    isSelf: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                context.startActivity(
                    Intent(context, ProfileActivity::class.java).putExtra("idUser", id)
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ImageComponent(
            url = urlAvatar,
            contentDescription = "User avatar",
            size = 100.dp,
            padding = 10.dp
        )
        if (isSelf) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Check",
                tint = BrandLightRed
            )
        }
        Text(text = name.substring(0, minOf(name.length, 8)))
    }
}

private fun loadProUsers(proUsers: MutableList<User>) {
    Firebase.firestore
        .collection("users")
        .whereGreaterThan("pro", System.currentTimeMillis() / 1000)
        .whereLessThan("blocked", 3)
        .get()
        .addOnSuccessListener {
            if (it != null && !it.isEmpty) {
                for (doc in it) {
                    if (doc.id != sharedPreferences.getString("userId", "")) {
                        proUsers.add(doc.toObject(User::class.java))
                    }
                }
            }
        }
        .addOnFailureListener {
            Log.d("TAG", "loadProUsers: $it")
        }
}

private fun loadUsers(users: MutableList<User>) {
    Firebase.firestore
        .collection("users")
        .whereNotEqualTo("first_name", "")
        .whereLessThan("blocked", 3)
        .get()
        .addOnSuccessListener {
            if (it != null && !it.isEmpty) {
                for (doc in it) {
                    val u = doc.toObject(User::class.java)
                    if (u.selfSkills.isNotEmpty() || u.learningSkills.isNotEmpty()) {
                        if (App.userViewModel.auth.currentUser != null) {
                            if (App.userViewModel.user.value?.blockedUsers?.contains(u.id) != true) {
                                users.add(u)
                            }
                        } else {
                            users.add(u)
                        }
                    }
                }
            }
        }
        .addOnFailureListener {
            Log.d("TAG", "loadUsers: $it")
        }
}


