package com.example.goodskeeper

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.goodskeeper.ui.theme.GoodsKeeperTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoodsKeeperTheme {
                RegisterScreen(onRegistered = {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }, onBackLogin = {
                    startActivity(Intent(this, LoginActivity::class.java))
                })
            }
        }
    }
}

@Composable
fun RegisterScreen(onRegistered: () -> Unit, onBackLogin: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Create account", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (name.isBlank() || email.isBlank() || password.isBlank()) return@Button
            FirebaseUtils.auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    // save user basic info to Firestore
                    val uid = it.user?.uid ?: ""
                    val data = mapOf("uid" to uid, "name" to name, "email" to email)
                    FirebaseUtils.firestore.collection("users").document(uid).set(data)
                        .addOnSuccessListener { onRegistered() }
                        .addOnFailureListener { Toast.makeText(null, "Save failed: \${it.message}", Toast.LENGTH_LONG).show() }
                }
                .addOnFailureListener { Toast.makeText(null, "Registration failed: \${it.message}", Toast.LENGTH_LONG).show() }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBackLogin, modifier = Modifier.fillMaxWidth()) {
            Text("Back to Login")
        }
    }
}
