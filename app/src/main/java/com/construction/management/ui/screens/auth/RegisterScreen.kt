package com.construction.management.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.construction.management.data.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.WORKER) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Icon(
            imageVector = Icons.Filled.PersonAdd,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Регистрация",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // First name field
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Имя") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Last name field
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Фамилия") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Username field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Имя пользователя") },
            leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confirm password field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Подтвердите пароль") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (confirmPasswordVisible) "Скрыть пароль" else "Показать пароль"
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Role selection
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { },
        ) {
            OutlinedTextField(
                value = selectedRole.displayName,
                onValueChange = { },
                readOnly = true,
                label = { Text("Роль") },
                leadingIcon = { Icon(Icons.Filled.Work, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = false,
                onDismissRequest = { },
            ) {
                UserRole.values().forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.displayName) },
                        onClick = { selectedRole = role }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Error message
        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Register button
        Button(
            onClick = {
                isLoading = true
                error = null
                // TODO: Implement registration logic
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                    kotlinx.coroutines.delay(1000)
                    isLoading = false
                    navController.navigateUp()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = firstName.isNotBlank() && lastName.isNotBlank() && 
                     email.isNotBlank() && username.isNotBlank() && 
                     password.isNotBlank() && confirmPassword.isNotBlank() && 
                     password == confirmPassword && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Зарегистрироваться")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Login link
        TextButton(
            onClick = { navController.navigateUp() }
        ) {
            Text("Уже есть аккаунт? Войти")
        }
    }
}