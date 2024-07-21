package com.lovigin.app.skillify.activity.element

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lovigin.app.skillify.activity.ProfileActivity
import com.lovigin.app.skillify.`object`.User

@Composable
fun FollowUserView(context: Context, user: User) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .clickable {
                context.startActivity(
                    Intent(
                        context,
                        ProfileActivity::class.java
                    )
                        .putExtra("idUser", user.id)
                )
            },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageComponent(
                url = user.urlAvatar,
                contentDescription = "Avatar"
            )

            Column {
                Text(text = "${user.first_name} ${user.last_name}", fontWeight = FontWeight.Bold)
                Text(text = "@${user.nickname}")
            }
        }
    }
}