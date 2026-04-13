package com.example.smart_plant_care

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.smart_plant_care.data.local.db.AppDatabase
import com.example.smart_plant_care.data.repository.PlantRepository
import com.example.smart_plant_care.ui.theme.Smart_Plant_CareTheme
import com.example.smartplantcare.ui.main.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val context = applicationContext
            val db = AppDatabase.getDatabase(context)
            val repository = PlantRepository(db.plantDao())

            Smart_Plant_CareTheme {
                MainScreen(repository = repository)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Smart_Plant_CareTheme {
        Greeting("Android")
    }
}