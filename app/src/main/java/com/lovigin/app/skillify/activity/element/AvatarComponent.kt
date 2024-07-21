package com.lovigin.app.skillify.activity.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.avatars

@OptIn(ExperimentalCoilApi::class)
@Composable
fun AvatarComponent(
    url: String,
    contentDescription: String,
    size: Dp = 100.dp,
    padding: Dp = 16.dp,
    radius: Dp = 100.dp
) {
    var showDialog by remember { mutableStateOf(false) }

    val painter = rememberImagePainter(
        data = url,
        builder = {
            placeholder(R.drawable.fi_rr_user)
            if (radius == 100.dp) transformations(CircleCropTransformation())
            crossfade(true)
        },
    )

    if (avatars.contains(url.split(":").first())) {
        Image(
            painter = painterResource(id = getAvatarImage(url.split(":").first())),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(size)
                .padding(padding)
                .clip(if (radius == 100.dp) CircleShape else RoundedCornerShape(radius))
                .background(parseColorString(url.split(":").last()))
                .padding(top = 10.dp)
        )
    } else if (url.isNotEmpty()) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(size)
                .padding(padding)
                .clip(if (radius == 100.dp) CircleShape else RoundedCornerShape(radius))
                .background(Color.Gray.copy(0.4f))
                .clickable {
                    showDialog = true
                },
            contentScale = ContentScale.Fit
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.fi_rr_user),
            contentDescription = "Avatar",
            modifier = Modifier
                .padding(padding)
                .width((size - 30.dp))
                .height((size - 30.dp))
                .clip(if (radius == 100.dp) CircleShape else RoundedCornerShape(radius))
                .background(Color.Gray)
                .padding(padding)
                .clickable {
                    showDialog = true
                },
            colorFilter = ColorFilter.tint(Color.White)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = {
                Image(
                    painter = rememberImagePainter(url),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            },
            confirmButton = {
            },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        )
    }
}
