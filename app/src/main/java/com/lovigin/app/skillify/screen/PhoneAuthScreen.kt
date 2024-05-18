package com.lovigin.app.skillify.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.lovigin.app.skillify.R
import com.lovigin.app.skillify.model.UserViewModel

@Composable
fun PhoneAuthScreen(
    authViewModel: UserViewModel,
    context: Context,
    navHostController: NavHostController
) {
    var phoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = "Skillify",
            Modifier
                .width(90.dp)
                .padding(10.dp)
        )
        Text(text = "Enter your phone number",
            modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "+", fontSize = 20.sp)
            TextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = it.filter { char -> char.isDigit() }.take(12)
                },
                label = { Text("1 000 000 00 00") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().padding(start = 10.dp)
            )
        }
        Button(
            onClick = {
                isActive = true
                authViewModel.sendVerificationCode("+${phoneNumber}", authViewModel.callbacks, context, navHostController)
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            enabled = phoneNumber.isNotEmpty().and(phoneNumber.length > 10)
        ) {
            Text("Send Verification Code")
        }
        if (isActive) {
            TextField(
                value = verificationCode,
                onValueChange = { verificationCode = it },
                label = { Text("Enter Verification Code") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    authViewModel.verifyVerificationCode(verificationCode)
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                enabled = verificationCode.isNotEmpty()
            ) {
                Text("Verify Code")
            }
        }
    }
}
