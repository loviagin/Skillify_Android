package com.lovigin.app.skillify.activity

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.App.Companion.messagesViewModel
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.element.AvatarComponent
import com.lovigin.app.skillify.activity.element.BackButton
import com.lovigin.app.skillify.activity.element.ImageComponent
import com.lovigin.app.skillify.activity.element.PictureComponent
import com.lovigin.app.skillify.activity.ui.theme.SkillifyTheme
import com.lovigin.app.skillify.`object`.Chat
import com.lovigin.app.skillify.`object`.Message
import com.lovigin.app.skillify.ui.theme.BrandBlue
import com.lovigin.app.skillify.worker.NotificationSender
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

private const val REQUEST_CODE_PHOTO = 1001
private const val REQUEST_CODE_VIDEO = 1002
private const val REQUEST_CODE_FILE = 1003
private const val REQUEST_CODE_CROP = 1004

class ChatActivity : ComponentActivity() {

    private var messages = mutableStateListOf<Chat>()
    private var mediaUrl = mutableStateOf("")
    private lateinit var userId: MutableState<String>
    private lateinit var idMessage: MutableState<String>

    private var listenerRegistration: ListenerRegistration? = null

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        userId = mutableStateOf(intent.getStringExtra("userId") ?: "")
        idMessage = mutableStateOf(intent.getStringExtra("idMessage") ?: "")
        setContent {
            var name by remember { mutableStateOf(intent.getStringExtra("name") ?: "") }
            var imageUrl by remember { mutableStateOf(intent.getStringExtra("imageUrl") ?: "") }

            var messageText by remember { mutableStateOf("") }
            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()
            var mediaOpened by rememberSaveable { mutableStateOf(false) }
            var actionMenuOpen by rememberSaveable { mutableStateOf(false) }
            var pressOffset by remember {
                mutableStateOf(DpOffset.Zero)
            }
            var itemHeight by remember {
                mutableStateOf(0.dp)
            }
            val density = LocalDensity.current

            LaunchedEffect(Unit) {
                loadChats(idMessage.value) {
                    coroutineScope.launch {
                        snapshotFlow { messages.size }
                            .collect {
                                if (messages.isNotEmpty()) {
                                    listState.scrollToItem(messages.size - 1)
                                }
                            }
                    }
                }
            }

            SkillifyTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                        .systemBarsPadding(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        startActivity(
                                            Intent(
                                                this@ChatActivity,
                                                ProfileActivity::class.java
                                            ).putExtra("idUser", userId.value)
                                        )
                                    }
                                ) {
                                    ImageComponent(
                                        url = imageUrl,
                                        contentDescription = "Avatar",
                                        size = 40.dp,
                                        padding = 0.dp,
                                        defaultSize = 40.dp
                                    )
                                    Text(
                                        text = name,
                                        modifier = Modifier.padding(start = 15.dp),
                                        fontSize = 16.sp
                                    )
                                }
                            },
                            navigationIcon = {
                                BackButton {
                                    finish()
                                }
                            },
                        )
                    }, bottomBar = {
                        BottomAppBar {
                            if (intent.getStringExtra("blockedText")!!.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Lock",
                                        tint = Color.Red
                                    )
                                    Text(
                                        text = intent.getStringExtra("blockedText")!!,
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.onSizeChanged {
                                        itemHeight = with(density) { it.height.toDp() }
                                    },
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Media",
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .pointerInput(true) {
                                                detectTapGestures(
                                                    onTap = {
                                                        pressOffset =
                                                            DpOffset(it.x.toDp(), it.y.toDp())
                                                        mediaOpened = true
                                                    }
                                                )
                                            }
                                    )
                                    OutlinedTextField(
                                        value = messageText,
                                        onValueChange = { messageText = it },
                                        modifier = Modifier
                                            .weight(1f),
                                        placeholder = {
                                            Text(
                                                stringResource(R.string.enter_message_txt),
                                                fontSize = 16.sp
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = if (isSystemInDarkTheme()) Color.Black.copy(
                                                0.5f
                                            ) else Color.White,
                                            unfocusedContainerColor = if (isSystemInDarkTheme()) Color.Black.copy(
                                                0.5f
                                            ) else Color.White,
                                            focusedBorderColor = BrandBlue.copy(0.3f),
                                            unfocusedBorderColor = Color.Gray.copy(0.5f),
                                        ),
                                        maxLines = 3,
                                        shape = RoundedCornerShape(30.dp)
                                    )
                                    IconButton(
                                        onClick = {
                                            if (messageText.isNotEmpty()) {
                                                val c = Chat(
                                                    cUid = App.userViewModel.user.value!!.id,
                                                    text = messageText,
                                                    time = System.currentTimeMillis() / 1000.0,
                                                    status = "u"
                                                )
                                                messagesViewModel.sendMessage(c, idMessage.value)
                                            }
                                            NotificationSender.sendNotification(
                                                this@ChatActivity,
                                                userId.value,
                                                messageText,
                                                name
                                            )

                                            messageText = ""
                                        },
                                        modifier = Modifier.padding(horizontal = 1.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Send"
                                        )
                                    }

                                    DropdownMenu(expanded = mediaOpened, onDismissRequest = {
                                        mediaOpened = false
                                    }) {
                                        DropdownMenuItem(
                                            text = { Text(text = stringResource(R.string.photo_txt)) },
                                            onClick = {
                                                handleMediaOption("photo", this@ChatActivity)
                                                mediaOpened = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .onSizeChanged {
                                itemHeight = with(density) { it.height.toDp() }
                            },
                    ) {
                        Image(
                            painter = painterResource(id = if (isSystemInDarkTheme()) R.drawable.chatdark else R.drawable.chatlight),
                            contentDescription = "Background Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        LazyColumn(
                            state = listState
                        ) {
                            messages.forEach { chat ->
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp, vertical = 10.dp),
                                        horizontalArrangement = if (chat.cUid == App.userViewModel.user.value?.id) Arrangement.End else Arrangement.Start
                                    ) {
                                        ChatItemView(chat)
                                    }
                                }
                            }
                        }

                        if (mediaUrl.value.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(0.5f))
                                    .onSizeChanged {
                                        itemHeight = with(density) { it.height.toDp() }
                                    }
                            ) {
                                val painter = rememberImagePainter(
                                    data = mediaUrl.value,
                                    builder = {
                                        placeholder(R.drawable.fi_rr_user)
                                        crossfade(true)
                                    },
                                )

                                Image(
                                    painter = painter,
                                    contentDescription = "Media",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Gray.copy(0.4f))
                                        .clickable { mediaUrl.value = "" },
                                    contentScale = ContentScale.FillWidth
                                )

                                DropdownMenu(
                                    expanded = actionMenuOpen, onDismissRequest = {
                                        actionMenuOpen = false
                                    },
                                    offset = pressOffset.copy(
                                        y = pressOffset.y - itemHeight,
//                                        x = pressOffset.x - (itemHeight - 20.dp)
                                    )
                                ) {
                                    DropdownMenuItem(
                                        text = { Text(text = "Save photo") },
                                        onClick = {
                                            Toast.makeText(
                                                this@ChatActivity,
                                                getString(R.string.in_development_str),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            actionMenuOpen = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text(text = "Delete") },
                                        onClick = {
                                            Toast.makeText(
                                                this@ChatActivity,
                                                getString(R.string.in_development_str),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            actionMenuOpen = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ChatItemView(chat: Chat) {
        Column(
            modifier = Modifier
                .widthIn(min = 100.dp, max = 320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (chat.cUid == App.userViewModel.user.value?.id) BrandBlue
                    else Color.White
                )
                .padding(15.dp),
            horizontalAlignment = if (chat.cUid == App.userViewModel.user.value?.id) Alignment.End else Alignment.Start
        ) {
            chat.text?.let {
                Text(
                    text = it,
                    color = if (chat.cUid == App.userViewModel.user.value?.id) Color.White else Color.Black
                )
            }
            chat.mediaUrl?.let {
                PictureComponent(
                    url = it,
                    contentDescription = "Media",
                    size = 200.dp,
                    padding = 0.dp,
                    radius = 0.dp,
                    onClick = {
                        Log.d("TAG", "ChatItemView: $it")
                        mediaUrl.value = it
                    }
                )
            }

            Row {
                Text(
                    text = formatTime(chat.time),
                    color = if (chat.cUid == App.userViewModel.user.value?.id) Color.White else Color.Black,
                    fontSize = 13.sp
                )
                if (chat.cUid == App.userViewModel.user.value?.id) {
                    if (chat.status == "r") {
                        Icon(
                            painter = painterResource(id = R.drawable.fi_rr_double_check),
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .size(20.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.fi_rr_check),
                            contentDescription = "Check",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .size(20.dp)
                        )
                    }
                }
            }
        }
    }

    private fun handleMediaOption(option: String, context: Context) {
        when (option) {
            "photo" -> {
                // Запуск активности для выбора фото из галереи
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
                // Проверка наличия активности для обработки данного интента
                if (intent.resolveActivity(context.packageManager) != null) {
                    (context as ComponentActivity).startActivityForResult(
                        intent,
                        REQUEST_CODE_PHOTO
                    )
                } else {
                    Toast.makeText(
                        context,
                        "No application available to select photo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            "video" -> {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = "video/*"
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    (context as ComponentActivity).startActivityForResult(
                        intent,
                        REQUEST_CODE_VIDEO
                    )
                } else {
                    Toast.makeText(
                        context,
                        "No application available to select video",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            "file" -> {
                // Запуск активности для выбора файла
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "*/*"
                }
                // Проверка наличия активности для обработки данного интента
                if (intent.resolveActivity(context.packageManager) != null) {
                    (context as ComponentActivity).startActivityForResult(intent, REQUEST_CODE_FILE)
                } else {
                    Toast.makeText(
                        context,
                        "No application available to select file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            else -> {
                Toast.makeText(context, "Invalid option selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_PHOTO -> {
                    // Запуск UCrop для обрезки выбранного фото
                    val sourceUri = data.data
                    val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image.jpg"))
                    UCrop.of(sourceUri!!, destinationUri)
                        .withMaxResultSize(512, 512)
                        .start(this, REQUEST_CODE_CROP)
                }

                REQUEST_CODE_CROP -> {
                    // Обработка обрезанного изображения
                    val resultUri = UCrop.getOutput(data)
                    resultUri?.let { uri ->
                        uploadImageToFirebase(uri)
                    }
                }

                REQUEST_CODE_VIDEO -> {
                    val selectedVideoUri = data.data
                    selectedVideoUri?.let { uri ->
                        trimVideo(uri)
                    }
                }

                REQUEST_CODE_FILE -> {
                    // Обработка выбранного файла
//                    val selectedFileUri = data.data
                    // Ваш код для обработки файла
                }
            }
        }
    }

    private fun trimVideo(videoUri: Uri) {
        val videoDuration = getVideoDuration(videoUri)
        if (videoDuration > 60000) { // 1 минута = 60000 миллисекунд
            Toast.makeText(this, "Video is longer than 1 minute", Toast.LENGTH_SHORT).show()
            return
        }

        val realPath = getRealPathFromURI(videoUri)
        if (realPath != null) {
            val outputDir = cacheDir
            val outputFile = File(outputDir, "trimmed_video.mp4")
            val command = arrayOf(
                "-i", realPath,
                "-t", "00:01:00",
                "-c", "copy",
                outputFile.path
            )
            FFmpeg.executeAsync(command) { executionId, returnCode ->
                if (returnCode == com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS) {
                    uploadVideoToFirebase(Uri.fromFile(outputFile))
                } else {
                    Toast.makeText(this@ChatActivity, "Video trim failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            Toast.makeText(this@ChatActivity, "Failed to get video path", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadVideoToFirebase(uri: Uri) {
        val storageReference = Firebase.storage.reference.child("media/${UUID.randomUUID()}.mp4")
        storageReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveVideoUrlToFirestore(downloadUri.toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Video upload failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveVideoUrlToFirestore(videoUrl: String) {
        val chat = Chat(
            cUid = App.userViewModel.user.value!!.id,
            mediaUrl = videoUrl,
            time = System.currentTimeMillis() / 1000.0,
            status = "u"
        )
        Firebase.firestore
            .collection("messages")
            .document(intent.getStringExtra("idMessage") ?: "")
            .update("messages", FieldValue.arrayUnion(chat.toMap()))
            .addOnSuccessListener {
                NotificationSender.sendNotification(
                    this@ChatActivity,
                    userId.value,
                    "🏞️ Video",
                    "${App.userViewModel.user.value!!.first_name} ${App.userViewModel.user.value!!.last_name}"
                )
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Failed to save video URL: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        Firebase.firestore
            .collection("messages")
            .document(idMessage.value)
            .update("lastData", listOf(App.userViewModel.user.value!!.id, "🏞️ Video", "u"))

        Firebase.firestore
            .collection("messages")
            .document(idMessage.value)
            .update("time", System.currentTimeMillis() / 1000.0)
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val storageReference = Firebase.storage.reference.child("media/${UUID.randomUUID()}.jpg")
        storageReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    saveImageUrlToFirestore(downloadUri.toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Image upload failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        val chat = Chat(
            cUid = App.userViewModel.user.value!!.id,
            mediaUrl = imageUrl,
            time = System.currentTimeMillis() / 1000.0,
            status = "u"
        )
        Firebase.firestore
            .collection("messages")
            .document(intent.getStringExtra("idMessage") ?: "")
            .update("messages", FieldValue.arrayUnion(chat.toMap()))
            .addOnSuccessListener {
                NotificationSender.sendNotification(
                    this@ChatActivity,
                    userId.value,
                    "🏞️ Image",
                    "${App.userViewModel.user.value!!.first_name} ${App.userViewModel.user.value!!.last_name}"
                )
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Failed to save image URL: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        Firebase.firestore
            .collection("messages")
            .document(idMessage.value)
            .update("lastData", listOf(App.userViewModel.user.value!!.id, "🏞️ Image", "u"))

        Firebase.firestore
            .collection("messages")
            .document(idMessage.value)
            .update("time", System.currentTimeMillis() / 1000.0)
    }

    private fun loadChats(idMessage: String, onMessagesLoaded: () -> Unit) {
        listenerRegistration = Firebase.firestore
            .collection("messages")
            .document(idMessage)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null && value.exists()) {
                    val message = value.toObject(Message::class.java)
                    messages.clear()

                    message?.messages?.forEach { chat ->
                        if (chat.cUid != App.userViewModel.user.value?.id && chat.status == "u") {
                            chat.status = "r"
                        }
                        messages.add(chat)
                    }

                    if (!message?.lastData!!.contains(App.userViewModel.user.value!!.id)) {
                        Firebase.firestore.collection("messages")
                            .document(idMessage)
                            .update(
                                "messages", message.messages?.map { it.toMap() },
                                "lastData", FieldValue.arrayRemove("u"),
                                "lastData", FieldValue.arrayUnion("r")
                            )
                            .addOnSuccessListener {
                                onMessagesLoaded()
                            }
                            .addOnFailureListener { e ->
                                Log.e("FirestoreError", "Error updating messages", e)
                            }
                    } else {
                        Firebase.firestore.collection("messages")
                            .document(idMessage)
                            .update(
                                "messages", message.messages?.map { it.toMap() }
                            )
                            .addOnSuccessListener {
                                onMessagesLoaded()
                            }
                            .addOnFailureListener { e ->
                                Log.e("FirestoreError", "Error updating messages", e)
                            }
                    }
                }
            }
    }

    private fun formatTime(timeInSeconds: Double): String {
        // Преобразуем время в миллисекундах
        val timeInMillis = (timeInSeconds * 1000).toLong()
        val time =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInMillis), ZoneId.systemDefault())
        val now = LocalDateTime.now()
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
        val formatterDate = DateTimeFormatter.ofPattern("dd MMM", Locale("en"))

        return when {
            time.toLocalDate() == now.toLocalDate() -> {
                getString(R.string.today_txt, time.format(formatterTime))
            }

            time.toLocalDate() == now.minusDays(1).toLocalDate() -> {
                getString(R.string.yesterday_txt, time.format(formatterTime))
            }

            time.toLocalDate()
                .isAfter(now.minusDays(now.dayOfWeek.value.toLong()).toLocalDate()) -> {
                "${
                    time.dayOfWeek.getDisplayName(
                        java.time.format.TextStyle.FULL,
                        Locale("en")
                    )
                }, ${time.format(formatterTime)}"
            }

            else -> {
                "${time.format(formatterDate)}, ${time.format(formatterTime)}"
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        var realPath: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            if (idx != -1) {
                realPath = cursor.getString(idx)
            }
            cursor.close()
        }
        return realPath
    }

    private fun getVideoDuration(uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(this, uri)
        val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        retriever.release()
        return time?.toLong() ?: 0L
    }

    override fun onStop() {
        super.onStop()
        listenerRegistration?.remove()
        listenerRegistration = null
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}