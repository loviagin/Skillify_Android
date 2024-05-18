package com.lovigin.app.skillify.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.element.ImageComponent
import com.lovigin.app.skillify.ui.theme.SkillifyTheme
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class EditProfileActivity : ComponentActivity() {

    private val viewModel = App.userViewModel
    private val user = viewModel.user.value
    private val items = listOf("-", "Male", "Female", "Other")
    private val newValues = mutableMapOf<String, Any>()
    private var imageUrl by mutableStateOf(user?.urlAvatar ?: "")

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestImagePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var uCropLauncher: ActivityResultLauncher<Intent>

    private fun handleImageAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestImagePermissionLauncher.launch(
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Инициализация лаунчеров
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Разрешение предоставлено, запускаем выбор изображения
                    pickImageLauncher.launch("image/*")
                } else {
                    // Разрешение отклонено, показываем сообщение
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }

        requestImagePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Запуск функций, связанных с изображениями
                    pickImageLauncher.launch("image/*")
                } else {
                    Toast.makeText(
                        this,
                        "Permission for media images is denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let { startCropActivity(it) }
            }

        uCropLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val resultUri = UCrop.getOutput(result.data!!)
                    resultUri?.let {
                        uploadImageToFirebase(it, storageRef)
                    }
                } else {
                    Log.e("EditProfileActivity", "Cropping failed or was cancelled.")
                }
            }

        setContent {
            var first_name by remember { mutableStateOf(user?.first_name ?: "") }
            var last_name by remember { mutableStateOf(user?.last_name ?: "") }
            var bio by remember { mutableStateOf(user?.bio ?: "") }
            var nickname by remember { mutableStateOf(user?.nickname ?: "") }
            var birthday by remember { mutableStateOf(user?.birthday ?: Date()) }
            var sex by remember { mutableStateOf(user?.sex ?: "-") }

            var isBirthday by remember { mutableStateOf(false) }
            var expanded by remember { mutableStateOf(false) }

            SkillifyTheme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Edit profile") },
                            navigationIcon = {
                                if (user!!.first_name.isNotEmpty()) {
                                    IconButton(onClick = { finish() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                }
                            })
                    }) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ImageComponent(
                                url = imageUrl,
                                contentDescription = "Avatar",
                                size = 140.dp
                            )

                            Text(
                                text = "Choose avatar*",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Gray.copy(alpha = 0.3f))
                                    .padding(horizontal = 16.dp)
                                    .padding(vertical = 5.dp)
                                    .clickable {
                                        handleImageAccess()
                                    }
                            )
                        }
                        TextField(
                            value = first_name,
                            onValueChange = {
                                first_name = it.substring(0, minOf(15, it.length))
                                newValues["first_name"] = it
//                                viewModel.user.value!!.first_name = first_name
                            },
                            label = { Text(text = "First name *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .padding(horizontal = 16.dp),
                            maxLines = 1
                        )
                        TextField(
                            value = last_name,
                            onValueChange = {
                                last_name = it.substring(0, minOf(15, it.length))
                                newValues["last_name"] = it
//                                viewModel.user.value!!.last_name = last_name
                            },
                            label = { Text(text = "Last name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .padding(horizontal = 16.dp),
                            maxLines = 1
                        )

                        TextField(
                            value = bio,
                            onValueChange = {
                                bio = it.substring(0, minOf(45, it.length))
                                newValues["bio"] = it
//                                viewModel.user.value!!.bio = bio
                            },
                            label = { Text(text = "Short description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .padding(horizontal = 16.dp),
                            minLines = 3
                        )

                        TextField(
                            value = nickname,
                            onValueChange = {
                                nickname = it.substring(0, minOf(15, it.length))
                                newValues["nickname"] = it
//                                viewModel.user.value!!.nickname = nickname
                            },
                            label = { Text(text = "Nickname *") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .padding(horizontal = 16.dp),
                            maxLines = 1
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "Your birthday",
                                modifier = Modifier.padding(16.dp)
                            )

                            Button(
                                onClick = {
                                    isBirthday = true
                                },
                                modifier = Modifier.padding(end = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray.copy(alpha = 0.3f),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(
                                    text = "${
                                        Calendar.getInstance().apply { time = birthday }
                                            .get(Calendar.DAY_OF_MONTH)
                                    } " +
                                            "${
                                                getMonthName(birthday)
                                            } " +
                                            "${
                                                Calendar.getInstance()
                                                    .apply { time = birthday }
                                                    .get(Calendar.YEAR)
                                            }"
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp)
                        ) {
                            Text(
                                text = "Select your gender",
                                modifier = Modifier.padding(16.dp)
                            )

                            Button(
                                onClick = {
                                    expanded = !expanded
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Gray.copy(alpha = 0.3f),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(sex)
                            }

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()

                            ) {
                                items.forEach { item ->
                                    DropdownMenuItem(
                                        onClick = {
                                            sex = item
                                            newValues["sex"] = item
//                                            viewModel.user.value!!.sex = sex
                                            expanded = false
                                        }, text = {
                                            Text(text = item)
                                        }
                                    )
                                }
                            }
                        }

                        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                        val dateState = DatePickerState(
                            initialSelectedDateMillis = Calendar.getInstance()
                                .apply {
                                    set(Calendar.YEAR, currentYear - 12)
                                    set(Calendar.MONTH, 1)
                                    set(Calendar.DAY_OF_MONTH, 1)
                                }.timeInMillis,
                            locale = Locale.getDefault(),
                            yearRange = 1800..(currentYear - 12),
                            initialDisplayMode = DisplayMode.Picker
                        )

                        if (isBirthday) {
                            Dialog(
                                onDismissRequest = {
                                    isBirthday = false
                                },
                                properties = DialogProperties(
                                    usePlatformDefaultWidth = false
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .clip(
                                            RoundedCornerShape(16.dp)
                                        )
                                        .background(Color.White)
                                ) {
                                    DatePicker(
                                        state = dateState,
                                        modifier = Modifier
                                            .fillMaxWidth()

                                    )
                                    Button(
                                        onClick = {
                                            birthday =
                                                dateState.selectedDateMillis?.let { Date(it) }!!
                                            newValues["birthday"] = birthday
//                                            viewModel.user.value!!.birthday = birthday
                                            isBirthday = false
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .padding(bottom = 16.dp)
                                    ) {
                                        Text(text = "Save")
                                    }
                                }
                            }
                        }

                        if (user!!.email.isNotEmpty()) {
                            TextField(
                                value = user.email,
                                enabled = false,
                                onValueChange = {},
                                label = { Text(text = "Email") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .padding(horizontal = 16.dp),
                            )
                        } else if (user.phone.isNotEmpty()) {
                            TextField(
                                value = user.phone,
                                enabled = false,
                                onValueChange = {},
                                label = { Text(text = "Phone") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .padding(horizontal = 16.dp),
                            )
                        }
                        Button(onClick = {
                            Log.d("TAG", "${newValues["birthday"]}")
                            if (!newValues.containsKey("first_name") && first_name.isEmpty()) {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "First name is required",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (!newValues.containsKey("nickname") && nickname.isEmpty()) {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Nickname is required",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (!newValues.containsKey("urlAvatar") && imageUrl.isEmpty()) {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Avatar is required",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else if (newValues["birthday"]?.let { App.isOld((it as? Double) ?: return@let false) } != true &&
                                !App.isOld(birthday.time.toDouble())) {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "You must be 12 years old or older",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            viewModel.updateData(
                                "users",
                                App.userViewModel.auth.currentUser!!.uid,
                                newValues
                            )
                            Log.d("info", "newValues: $newValues")
                            finish()
                        }, modifier = Modifier.padding(16.dp)) {
                            Text(text = "Save")
                        }
                        Text(
                            text = "* - required fields",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri, storageRef: StorageReference) {
        val fileRef = storageRef.child("avatars/${fileUri.lastPathSegment}")
        fileRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                if (user!!.urlAvatar.isNotEmpty()) {
                    deleteFileFromFirebaseStorage(user.urlAvatar)
                }

                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                    val downloadUrl = downloadUri.toString()
                    Log.d("TAG", "File Location: $downloadUrl")

                    imageUrl = downloadUrl
                    newValues["urlAvatar"] = downloadUrl
//                    viewModel.updateData(
//                        "users",
//                        App.userViewModel.auth.currentUser!!.uid,
//                        mapOf("urlAvatar" to downloadUrl)
//                    )
                }
                Log.d("TAG", "uploadImageToFirebase: Success")
                // Загрузка успешна, обработайте URL изображения, например, покажите сообщение или обновите UI
            }
            .addOnFailureListener {
                Log.d("TAG", "uploadImageToFirebase: Failure")
                // Обработка ошибки загрузки
            }

    }

    private fun getMonthName(date: Date): String {
        val dateFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun startCropActivity(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "${UUID.randomUUID()}.jpg"))
        val options = UCrop.Options().apply {
            setCompressionQuality(80)
            setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)
            setShowCropFrame(true)
            setShowCropGrid(true)
        }
        // Создание Intent от UCrop
        val uCropIntent = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withOptions(options)
            .getIntent(this)

        // Запуск Activity через Launcher
        uCropLauncher.launch(uCropIntent)
    }


    private fun deleteFileFromFirebaseStorage(fileUrl: String) {
        Thread {
            try {
                val storage = FirebaseStorage.getInstance()
                val fileRef = storage.getReferenceFromUrl(fileUrl)
                fileRef.delete().addOnSuccessListener {
                    // Обрабатываем успешное удаление в основном потоке
                    runOnUiThread {
                        Log.d("DeleteFile", "File successfully deleted")
                    }
                }.addOnFailureListener {
                    // Обрабатываем ошибку в основном потоке
                    runOnUiThread {
                        Log.e("DeleteFile", "Error deleting file", it)
                    }
                }
            } catch (e: IllegalArgumentException) {
                runOnUiThread {
                    Log.e("DeleteFile", "Invalid file URL", e)
                }
            }
        }.start()
    }
}