package com.constructionmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import com.constructionmanager.app.ui.theme.ConstructionManagerTheme
import com.constructionmanager.app.navigation.ConstructionManagerNavigation
import com.constructionmanager.app.data.database.DatabaseInitializer
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var databaseInitializer: DatabaseInitializer
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Инициализируем базу данных с демо-данными
        lifecycleScope.launch {
            databaseInitializer.initializeDatabase()
        }
        
        setContent {
            ConstructionManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConstructionManagerNavigation()
                }
            }
        }
    }
}