package com.lovigin.app.skillify.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavHostController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.App.Companion.messagesViewModel
import com.lovigin.app.skillify.App.Companion.sharedPreferences
import com.lovigin.app.skillify.activity.EditProfileActivity
import com.lovigin.app.skillify.`object`.Favorite
import com.lovigin.app.skillify.`object`.Skill
import com.lovigin.app.skillify.`object`.User
import com.onesignal.OneSignal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

class UserViewModel {
    private val USER_VIEWMODEL_TAG = "user_viewmodel_tag"
    var auth: FirebaseAuth = Firebase.auth

    var user = mutableStateOf<User?>(null)

    var isLoading = false
    private var storedVerificationId = ""
    private lateinit var context: Context // only with phone auth
    private lateinit var navHostController: NavHostController // only with phone auth
    private var phone = "" // only with phone auth

    // Метод для отправки кода на телефон
    fun sendVerificationCode(
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
        context: Context,
        navHostController: NavHostController
    ) {
        this.context = context
        this.navHostController = navHostController
        this.phone = phoneNumber

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity) // Требуется текущий экземпляр Activity
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Метод для проверки кода подтверждения
    fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential
    ) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "success")
                    checkUser(context, navHostController)
                } else {
                    Log.e("TAG", "failed")
                }
            }
    }

    // Коллбэки для обработки состояния верификации
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("AuthViewModel", "Verification failed", e)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationId = verificationId
        }
    }

    fun loadUser(function: () -> Unit = {}) {
        val db = Firebase.firestore
        db.collection("users")
            .document(App.userViewModel.auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    user.value = document.toObject(User::class.java)!!
                    Log.d(USER_VIEWMODEL_TAG, "DocumentSnapshot data")
                    messagesViewModel.loadMessages()
                }
                function()
            }
            .addOnFailureListener { exception ->
                Log.d("TAG2456", "DocumentSnapshot error: $exception")
            }
        updateData(
            "users",
            auth.currentUser!!.uid,
            mapOf("online" to true)
        )
//        updateData(
//            "users",
//            auth.currentUser!!.uid,
//            mapOf("pro" to 0.0)
//        )
        updateData(
            "users",
            auth.currentUser!!.uid,
            mapOf(
                "lastData" to listOf(
                    "android",
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(LocalDateTime.now()),
                    "3 ver. 1.0.3"
                )
            )
        )
    }

    fun firebaseAuthWithGoogle(
        idToken: String,
        navHostController: NavHostController,
        context: Context
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Auth", "signInWithCredential:success ${auth.currentUser?.uid}")
                    checkUser(context, navHostController)
                } else {
                    Log.w("Auth", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun checkUser(context: Context, navHostController: NavHostController) {
        Firebase.firestore.collection("users").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    Log.d("HI", "I'm your user")
                    // User существует
                    loadUser {
                        navHostController.navigate("account")
                    }
                } else {
                    // User не найден
                    auth.currentUser!!.email?.let {
                        registerUser(it, context = context)
                        Log.d("TAG", "email register")
                    } ?: run {
                        registerUser(phone = phone, context = context)
                        Log.d("TAG", "phone register")
                    }
                }
                sharedPreferences.edit().putString("userId", auth.currentUser!!.uid).apply()
            }
            .addOnFailureListener { exception ->
                // Обработка ошибки
                Log.d("Firestore", "Error getting document: ", exception)
            }
    }

    fun registerUser(email: String = "", phone: String = "", context: Context) {
        user.value = User(
            id = auth.currentUser?.uid ?: UUID.randomUUID().toString(),
            email = email,
            phone = phone
        )
        OneSignal.login(user.value!!.id)
        getNickname {
            user.let { user ->
                if (user.value?.nickname?.isNotEmpty() == true)
                    user.value!!.nickname = it
            }
        }
        Firebase.firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .set(user.value!!)
        navHostController.navigate("account")
        context.startActivity(Intent(context, EditProfileActivity::class.java))
    }

    fun deleteData(collection: String, document: String, removeMap: Map<String, Any>) {
        Firebase.firestore
            .collection(collection)
            .document(document)
            .update(mapOf(removeMap.keys.first() to FieldValue.arrayRemove(removeMap.values.first())))
    }

    fun addData(collection: String, document: String, addMap: Map<String, Any>) {
        Firebase.firestore
            .collection(collection)
            .document(document)
            .update(mapOf(addMap.keys.first() to FieldValue.arrayUnion(addMap.values.first())))
    }

    fun updateData(
        collection: String,
        document: String,
        newData: Map<String, Any>
    ) {
        Firebase.firestore
            .collection(collection)
            .document(document)
            .update(newData)
            .addOnSuccessListener {
                Log.d("TAG2456", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("TAG2456", "Error updating document", e)
            }
        newData.forEach { (t, u) ->
            when (t) {
                "nickname" -> {
                    user.value?.nickname = u as String
                }

                "first_name" -> {
                    user.value?.first_name = u as String
                }

                "last_name" -> {
                    user.value?.last_name = u as String
                }

                "bio" -> {
                    user.value?.bio = u as String
                }

                "pro" -> {
                    user.value?.pro = u as Double
                }

                "online" -> {
                    user.value?.online = u as Boolean
                }

                "birthday" -> {
                    user.value?.birthday = u as Date
                }

                "sex" -> {
                    user.value?.sex = u as String
                }

                "urlAvatar" -> {
                    user.value?.urlAvatar = u as String
                }

                "favorites" -> {
                    user.value?.favorites = u as MutableList<Favorite>
                }

                "calls" -> {
                    user.value?.calls = u as MutableList<MutableMap<String, String>>
                }

                "messages" -> {
                    user.value?.messages = u as MutableList<MutableMap<String, String>>
                }

                "learningSkills" -> {
                    user.value?.learningSkills = u as MutableList<Skill>
                }

                "selfSkills" -> {
                    user.value?.selfSkills = u as MutableList<Skill>
                }

                "blockedUsers" -> {
                    user.value?.blockedUsers = u as MutableList<String>
                }

                "devices" -> {
                    user.value?.devices = u as MutableList<String>
                }

                "notifications" -> {
                    user.value?.notifications = u as MutableList<String>
                }

                "subscriptions" -> {
                    user.value?.subscriptions = u as MutableList<String>
                }

                "subscribers" -> {
                    user.value?.subscribers = u as MutableList<String>
                }

                "lastData" -> {
                    user.value?.lastData = u as List<String>
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
        OneSignal.logout()
        App.userViewModel.user.value = null
    }

    private var n = 2
    private fun generateNickname(): String {
        val words = listOf(
            "awesome", "coder", "blue", "sky", "happy", "tiger", "apple", "banana", "carrot",
            "dolphin", "elephant", "fox", "gorilla", "horse", "iguana", "jaguar", "kangaroo",
            "leopard", "monkey", "narwhal", "owl", "penguin", "quokka", "rhinoceros", "squirrel",
            "turtle", "unicorn", "vampire", "whale", "xerus", "yak", "zebra"
        )
        val random = java.util.Random()
        val randomWords = words.shuffled(random).take(1) // Выбираем случайные два слова из списка
        val randomDigits = (1..n).map { random.nextInt(10) } // Генерируем случайные цифры

        return randomWords.joinToString("") + randomDigits.joinToString("")
    }

    private fun getNickname(callback: (String) -> Unit) {
        val db = Firebase.firestore

        fun generateUniqueNickname() {
            val string = generateNickname()

            db.collection("users")
                .whereEqualTo("nickname", string)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.size() > 0) {
                        // Если никнейм уже занят, генерируем новый
                        generateUniqueNickname()
                    } else {
                        // Если никнейм уникален, возвращаем его через колбэк
                        callback(string)
                    }
                }
                .addOnFailureListener { exception ->
                    // В случае ошибки также возвращаем пустую строку через колбэк
                    Log.e("getNickname", "Error getting nickname", exception)
                    callback("")
                }
            n++
        }

        // Начинаем процесс генерации уникального никнейма
        generateUniqueNickname()
    }

    fun makePro() {
        val date = addOneMonth()
        updateData(
            "users",
            auth.currentUser!!.uid,
            mapOf("pro" to date)
        )
    }

    private fun addOneMonth(): Double = LocalDate.now().plusMonths(1).atStartOfDay(
        ZoneId.systemDefault()
    ).toInstant().toEpochMilli().toDouble()
}