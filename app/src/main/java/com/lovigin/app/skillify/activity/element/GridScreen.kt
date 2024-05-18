package com.lovigin.app.skillify.activity.element

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lovigin.app.skillify.`object`.User
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import com.lovigin.app.skillify.Const
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.ProfileActivity
import com.lovigin.app.skillify.ui.theme.BrandBlue
import com.lovigin.app.skillify.ui.theme.BrandLightRed

@Composable
fun GridScreen(users: List<User>, columns: Int = 3, context: Context) {
    Column(
        modifier = Modifier.padding(15.dp)
    ) {
        val rows = users.chunked(columns)
        rows.forEach { rowUsers ->
            Row(modifier = Modifier.fillMaxWidth()) {
                rowUsers.forEach { user ->
                    GridItem(user, Modifier.weight(1f), context)
                }
            }
        }
    }
}

@Composable
fun GridItem(user: User, modifier: Modifier = Modifier, context: Context) {
    Card(
        modifier = modifier
            .padding(5.dp)
            .clickable {
                context.startActivity(
                    Intent(
                        context,
                        ProfileActivity::class.java
                    ).putExtra("idUser", user.id)
                )
            }
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            BrandBlue.copy(0.2f),  // Начальный цвет
                            BrandLightRed.copy(0.25f)  // Конечный цвет
                        ),
                        start = Offset(0f, 0f), // Начало градиента (верхний левый угол)
                        end = Offset(0f, 1100f) // Конец градиента (нижний левый угол)
                    )
                )
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageComponent(
                url = user.urlAvatar,
                contentDescription = "User avatar",
                size = 100.dp,
//                padding = 10.dp
            )
            val name = "${user.first_name} ${user.last_name}"
            Text(
                text = "${
                    name.substring(
                        0,
                        minOf(name.length, 23)
                    )
                }${if (name.length > 23) "..." else ""}",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            if (user.bio.isNotEmpty()) {
                Text(
                    text = "${
                        user.bio.substring(
                            0,
                            minOf(user.bio.length, 23)
                        )
                    }${if (user.bio.length > 23) "..." else ""}",
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Check",
//                    tint = Color.White
                )
                Text(text = ": ", fontWeight = FontWeight.Bold)
                if (user.selfSkills.size > 3) {
                    Text(
                        text = Const.icons[user.selfSkills[0].name] ?: "",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                    Text(
                        text = Const.icons[user.selfSkills[1].name] ?: "",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                    Text(
                        text = Const.icons[user.selfSkills[2].name] ?: "",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                    Text(
                        text = "+",
                        fontSize = 18.sp,
//                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                } else if (user.selfSkills.isEmpty()) {
                    Text(text = " No self skills")
                } else {
                    user.selfSkills.forEach { skill ->
                        Text(
                            text = Const.icons[skill.name] ?: "",
                            fontSize = 18.sp,
//                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.fi_rr_bulb),
                    contentDescription = "Check",
//                    tint = Color.White
                )
                Text(text = ": ", fontWeight = FontWeight.Bold)
                if (user.learningSkills.size > 3) {
                    Text(
                        text = Const.icons[user.learningSkills[0].name] ?: "",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                    Text(
                        text = Const.icons[user.learningSkills[1].name] ?: "",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                    Text(
                        text = Const.icons[user.learningSkills[2].name] ?: "",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                    Text(
                        text = "+",
                        fontSize = 18.sp,
//                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )
                } else if (user.learningSkills.isEmpty()) {
                    Text(text = " No learning")
                } else {
                    user.learningSkills.forEach { skill ->
                        Text(
                            text = Const.icons[skill.name] ?: "",
                            fontSize = 18.sp,
//                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp)
                        )
                    }
                }
            }
        }
    }
}