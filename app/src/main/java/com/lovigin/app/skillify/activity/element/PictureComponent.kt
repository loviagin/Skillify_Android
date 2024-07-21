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


@OptIn(ExperimentalCoilApi::class)
@Composable
fun PictureComponent(
    url: String,
    contentDescription: String,
    size: Dp = 100.dp,
    padding: Dp = 16.dp,
    defaultSize: Dp = size - 30.dp,
    radius: Dp = 100.dp,
    onClick: () -> Unit = {}
) {
    val painter = rememberImagePainter(
        data = url,
        builder = {
            placeholder(R.drawable.fi_rr_user)
            if (radius == 100.dp) transformations(CircleCropTransformation())
            crossfade(true)
        },
    )

    if (url.isNotEmpty()) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(size)
                .padding(padding)
                .clip(if (radius == 100.dp) CircleShape else RoundedCornerShape(radius))
                .background(Color.Gray.copy(0.4f))
                .clickable { onClick() },
            contentScale = ContentScale.FillWidth
        )
    } else {
        Image(
            painter = painterResource(id = R.drawable.fi_rr_user),
            contentDescription = "Avatar",
            modifier = Modifier
                .padding(padding)
                .width(defaultSize)
                .height(defaultSize)
                .clip(if (radius == 100.dp) CircleShape else RoundedCornerShape(radius))
                .background(Color.Gray)
                .padding(padding)
                .clickable { onClick() },
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}
