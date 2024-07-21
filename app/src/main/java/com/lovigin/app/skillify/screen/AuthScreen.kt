package com.lovigin.app.skillify.screen

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lovigin.app.skillify.App
import com.lovigin.app.skillify.App.Companion.sharedPreferences
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.activity.EditProfileActivity
import com.lovigin.app.skillify.ui.theme.BrandBlue
import com.lovigin.app.skillify.ui.theme.BrandLightRed
import com.lovigin.app.skillify.ui.theme.Gray40
import com.onesignal.OneSignal

@Composable
fun AuthScreen(navHostController: NavHostController, context: Context) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorEmail by remember { mutableStateOf("") }
    var errorPass by remember { mutableStateOf("") }
    val viewModel = App.userViewModel

    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(stringResource(R.string.default_google_service_key))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
    val signInLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    viewModel.firebaseAuthWithGoogle(account.idToken!!, navHostController, context)
                } catch (e: ApiException) {
                    Log.e("Auth", "Google sign in failed", e)
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = "Skillify",
            Modifier
                .width(90.dp)
                .padding(10.dp)
        )
        Text(
            text = stringResource(R.string.welcome_abroad_str),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.your_email_str)) },
            leadingIcon = { Icon(imageVector = Icons.Filled.Email, contentDescription = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .padding(top = 15.dp)
                .clip(RoundedCornerShape(10.dp)),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray,
                unfocusedContainerColor = Gray40,
                focusedLabelColor = BrandBlue,
                focusedIndicatorColor = BrandBlue,
                focusedTextColor = BrandBlue,
                focusedLeadingIconColor = BrandBlue
            ),
            singleLine = true
        )
        Text(text = errorEmail, color = Color.Red, fontSize = 12.sp, textAlign = TextAlign.Start)
        TextField(
            value = password,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            label = { Text(stringResource(R.string.password_str)) },
            leadingIcon = { Icon(imageVector = Icons.Filled.Lock, contentDescription = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray,
                unfocusedContainerColor = Gray40,
                focusedLabelColor = BrandBlue,
                focusedIndicatorColor = BrandBlue,
                focusedTextColor = BrandBlue,
                focusedLeadingIconColor = BrandBlue
            ),
            singleLine = true
        )
        Text(text = errorPass, color = Color.Red, fontSize = 12.sp, textAlign = TextAlign.Start)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (email.isEmpty()) {
                        errorEmail = context.getString(R.string.email_is_empty_txt)
                        errorPass = ""
                    } else if (!email.contains("@")) {
                        errorEmail = context.getString(R.string.email_is_incorrect_txt)
                        errorPass = ""
                    } else if (password.isEmpty()) {
                        errorEmail = ""
                        errorPass = context.getString(R.string.password_is_empty_txt)
                    } else if (password.length < 6) {
                        errorEmail = ""
                        errorPass =
                            context.getString(R.string.password_is_less_than_6_characters_txt)
                    } else {
                        errorEmail = ""
                        errorPass = ""
                        signInUser(email, password, context, navHostController)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
            ) {
                Text(
                    text = stringResource(R.string.sign_in_str)
                )
            }

            if (viewModel.isRegistrationOn) {
                Button(
                    onClick = {
                        if (email.isEmpty()) {
                            errorEmail = context.getString(R.string.email_is_empty_txt)
                            errorPass = ""
                        } else if (!email.contains("@")) {
                            errorEmail = context.getString(R.string.email_is_incorrect_txt)
                            errorPass = ""
                        } else if (password.isEmpty()) {
                            errorEmail = ""
                            errorPass = context.getString(R.string.password_is_empty_txt)
                        } else if (password.length < 6) {
                            errorEmail = ""
                            errorPass =
                                context.getString(R.string.password_is_less_than_6_characters_txt)
                        } else {
                            errorEmail = ""
                            errorPass = ""
                            signUpUser(email, password, context, navHostController)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandLightRed)
                ) {
                    Text(text = stringResource(R.string.registration_str))
                }
            } else {
                Text(text = "Registration is disabled")
            }
        }
        Text(stringResource(R.string.or_use_another_methods_txt), Modifier.padding(top = 15.dp))
        Row(
            modifier = Modifier.padding(top = 10.dp)
        ) {
            IconButton(onClick = {
                signInLauncher.launch(googleSignInClient.signInIntent)
            }) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google sign in",
                    Modifier
                        .width(34.dp)
                        .height(34.dp)
                        .padding(end = 10.dp)
                )
            }

            IconButton(onClick = {
                navHostController.navigate("phone")
            }) {
                Image(
                    painter = painterResource(id = R.drawable.fi_rr_phone),
                    contentDescription = "Phone sign in",
                    Modifier
                        .width(34.dp)
                        .height(34.dp)
                        .padding(start = 10.dp)
                )
            }
        }
    }
}

fun signInUser(
    email: String,
    password: String,
    context: Context,
    navController: NavHostController
) {
    val viewModel = App.userViewModel

    viewModel.auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success ${viewModel.auth.currentUser?.uid}")
                viewModel.auth.currentUser?.let {
                    OneSignal.login(it.uid)
                    sharedPreferences.edit().putString("userId", it.uid).apply()
                }
                viewModel.loadUser {
                    navController.navigate("account")
                }
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    context.applicationContext,
                    context.getString(R.string.email_or_password_is_incorrect_txt),
                    Toast.LENGTH_SHORT
                ).show()
                Log.w(TAG, "signInWithEmail:failure", task.exception)
            }
        }
}

fun signUpUser(
    email: String,
    password: String,
    context: Context,
    navController: NavHostController
) {
    val viewModel = App.userViewModel

    viewModel.auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success ${viewModel.auth.currentUser?.uid}")
                sharedPreferences.edit().putString("userId", viewModel.auth.currentUser!!.uid).apply()
                viewModel.registerUser(email) {
                    navController.navigate("account")
                    context.startActivity(Intent(context, EditProfileActivity::class.java))
                }
            } else {
                Log.w(TAG, "signUpWithEmail:failure", task.exception)
                Toast.makeText(
                    context.applicationContext,
                    context.getString(R.string.user_with_the_same_email_txt),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}
