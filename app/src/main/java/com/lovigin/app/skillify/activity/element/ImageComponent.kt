package com.lovigin.app.skillify.activity.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.avatars


@OptIn(ExperimentalCoilApi::class)
@Composable
fun ImageComponent(
    url: String,
    contentDescription: String,
    size: Dp = 100.dp,
    padding: Dp = 16.dp,
    defaultSize: Dp = size - 30.dp,
    radius: Dp = 100.dp
) {
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
                .background(Color.Gray.copy(0.4f)),
            contentScale = ContentScale.FillWidth
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.fi_rr_user),
            contentDescription = "Avatar",
            modifier = Modifier
                .padding(padding)
                .size(defaultSize)
                .clip(if (radius == 100.dp) CircleShape else RoundedCornerShape(radius))
                .background(Color.Gray)
                .padding(padding),
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

fun parseColorString(colorString: String): Color {
    val components = colorString.split(",").mapNotNull { it.toFloatOrNull() }
    return if (components.size == 4) {
        Color(components[0], components[1], components[2], components[3])
    } else {
        // Возвращаем какой-либо дефолтный цвет в случае ошибки
        Color.Blue.copy(0.4f)
    }
}

fun getAvatarImage(s: String): Int {
    when (s) {
        "avatar1" -> return R.drawable.avatar1
        "avatar2" -> return R.drawable.avatar2
        "avatar3" -> return R.drawable.avatar3
        "avatar4" -> return R.drawable.avatar4
        "avatar5" -> return R.drawable.avatar5
        "avatar6" -> return R.drawable.avatar6
        "avatar7" -> return R.drawable.avatar7
        "avatar8" -> return R.drawable.avatar8
        "avatar9" -> return R.drawable.avatar9
        "avatar10" -> return R.drawable.avatar10
        "avatar11" -> return R.drawable.avatar11
        "avatar12" -> return R.drawable.avatar12
        "avatar13" -> return R.drawable.avatar13
        "avatar14" -> return R.drawable.avatar14
        "avatar15" -> return R.drawable.avatar15
        "avatar16" -> return R.drawable.avatar16
    }
    return R.drawable.avatar1
}
