package com.lovigin.app.skillify.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.EditProfileActivity
import com.lovigin.app.skillify.activity.LearningSkillsActivity
import com.lovigin.app.skillify.activity.ProfileActivity
import com.lovigin.app.skillify.activity.SelfSkillsActivity
import com.lovigin.app.skillify.activity.SettingsActivity
import com.lovigin.app.skillify.activity.element.ImageComponent
import com.lovigin.app.skillify.ui.theme.BrandBlue
import com.lovigin.app.skillify.ui.theme.BrandLightRed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AccountScreen(navController: NavHostController, context: Context) {
    val viewModel = App.userViewModel
    val user by remember {
        mutableStateOf(App.userViewModel.user.value)
    }
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        viewModel.loadUser {
            navController.navigate("account")
        }
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    var online by remember {
        mutableStateOf(user?.online ?: true)
    }

    Box(Modifier.pullRefresh(state)) {
        Column(
            modifier = Modifier
                .padding(top = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (!refreshing) {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            if (user != null) {
                                context.startActivity(
                                    Intent(
                                        context,
                                        EditProfileActivity::class.java
                                    )
                                )
                            }
                        }
                    ) {
                        if (user != null && user!!.urlAvatar.isNotEmpty()) {
                            ImageComponent(
                                url = user!!.urlAvatar,
                                contentDescription = "Avatar",
                                size = 100.dp
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.fi_rr_user),
                                contentDescription = "Avatar",
                                Modifier
                                    .padding(16.dp)
                                    .width(80.dp)
                                    .height(80.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                                    .padding(16.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                        ) {
                            user?.let {
                                Text(
                                    text = "${it.first_name} ${it.last_name}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                Text(
                                    text = "@${it.nickname}",
                                    fontSize = 18.sp,
                                )
                            } ?: run {
                                BlurRectangle()
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Edit button",
                            Modifier
                                .padding(end = 16.dp)
                                .width(30.dp)
                                .height(30.dp),
                            tint = Color.Gray
                        )
                    }

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                    AccountComponent(
                        icon = R.drawable.fi_rr_eye,
                        description = "View Profile",
                        name = stringResource(R.string.view_profile_str),
                        action = {
                            context.startActivity(Intent(context, ProfileActivity::class.java))
                        }
                    )

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(R.string.status_online_str), color = Color.Black)

                        Spacer(modifier = Modifier.weight(1f))

                        user?.let {
                            if (App.isUserPro(it.pro)) {
                                Switch(
                                    checked = online,
                                    onCheckedChange = { newValue ->
                                        online = newValue
//                                viewModel.updateData(online = newValue)
                                    }
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.only_for_pro_users_str),
                                    color = Color.Gray
                                )
                            }
                        } ?: run {
                            BlurRectangle()
                        }
                    }
                } // main card 1

                Text(
                    text = stringResource(R.string.configure_your_account_str),
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 5.dp),
                )
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                ) {
                    AccountComponent(
                        icon = R.drawable.fi_sr_star,
                        description = stringResource(id = R.string.skillify_pro_str),
                        name = stringResource(id = R.string.skillify_pro_str),
                        color = BrandBlue,
                        action = {
                            Toast.makeText(
                                context,
                                context.getString(R.string.coming_soon_str), Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                    AccountComponent(
                        icon = R.drawable.fi_rr_books,
                        description = "View Profile",
                        name = stringResource(R.string.self_skills_str),
                        action = {
                            if (user != null) {
                                context.startActivity(
                                    Intent(
                                        context,
                                        SelfSkillsActivity::class.java
                                    )
                                )
                            }
                        }
                    )

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                    AccountComponent(
                        icon = R.drawable.fi_rr_lump,
                        description = "View Profile",
                        name = stringResource(R.string.learning_skills_str),
                        action = {
                            if (user != null) {
                                context.startActivity(
                                    Intent(
                                        context,
                                        LearningSkillsActivity::class.java
                                    )
                                )
                            }
                        }
                    )
                } // card with skills and pro

                Text(
                    text = stringResource(R.string.about_us_str),
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 5.dp),
                )
                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 26.dp)
                        .fillMaxWidth(),
                ) {
                    AccountComponent(
                        icon = R.drawable.fi_rr_link,
                        description = "Instagram link",
                        name = stringResource(R.string.follow_us_on_instagram_str),
                        action = {
                            val intent =
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://www.instagram.com/_skillify")
                                )
                            context.startActivity(intent)
                        }
                    )

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                    AccountComponent(
                        icon = R.drawable.fi_rr_globe,
                        description = "Our website link",
                        name = stringResource(R.string.check_our_website_str),
                        action = {
                            val intent =
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://skillify.space"))
                            context.startActivity(intent)
                        }
                    )
                }

                Card(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                ) {
                    AccountComponent(
                        icon = R.drawable.fi_rr_settings,
                        description = "Other settings",
                        name = stringResource(R.string.other_settings_str),
                        action = {
                            context.startActivity(Intent(context, SettingsActivity::class.java))
                        }
                    )

                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.1f))

                    AccountComponent(
                        icon = R.drawable.fi_rr_logout,
                        description = "Log out",
                        name = stringResource(R.string.log_out_str),
                        color = BrandLightRed,
                        action = {
                            viewModel.logout()
                            navController.navigate("account")
                        }
                    )
                }
            }
        }

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun AccountComponent(
    icon: Int,
    description: String,
    name: String,
    color: Color = Color.Black,
    action: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 12.dp)
            .clickable(onClick = action),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = description,
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.padding(end = 10.dp)

        )
        Text(text = name, color = color)

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Edit button",
            Modifier
                .width(30.dp)
                .height(30.dp),
            tint = Color.Gray
        )
    }
}

@Composable
fun BlurRectangle() {
    Box(
        modifier = Modifier
//            .fillMaxWidth()
            .background(Color.Gray.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(16.dp))
            .padding(10.dp)
            .blur(16.dp), // Применяем заблюривание
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.loading_txt), color = Color.White)
    }
}