package com.yourdomainorname.example.testinggemini0

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import com.yourdomainorname.example.testinggemini0.ui.theme.TestingGemini0Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity()
{
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        val model = GenerativeModel(
            modelName = "gemini-pro",
            apiKey = BuildConfig.apiKey
        )
        setContent {
            val prompt = remember { mutableStateOf("")}
            val responsesList = remember { SnapshotStateList<String>() }
            val compositionScope = rememberCoroutineScope()
            TestingGemini0Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorScheme.background
                ) {
                    Greeting(prompt = prompt, responses = responsesList) {
                        compositionScope.launch {
                            try {
                                val response = model.generateContent(it)
                                val finalResponse = if (null == response.text) "Pas de restour!" else "${prompt.value}:\n ${response.text}"
                                responsesList.add(finalResponse)
                                prompt.value = ""
                            } catch (e: Exception) {
                                Log.e(TAG, e.message.toString())
                                e.stackTrace
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier, prompt: MutableState<String> = remember { mutableStateOf("") }, responses: SnapshotStateList<String>, onSend: (promptContent: String) -> Unit = {})
{
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)) {
        LazyColumn(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize(.8f)) {
            item {
                responses.forEach {
                    Text(text = it, modifier = Modifier.background(colorScheme.onBackground.copy(.1f), shape = shapes.medium).padding(vertical = 2.dp, horizontal = 4.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            TextField(
                value = prompt.value,
                onValueChange = {prompt.value = it}
            )
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = { onSend(prompt.value) }) {
                Text(text = "Send")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    TestingGemini0Theme {

        val responses = SnapshotStateList<String>()
        responses.add("Ceci est un test")
        responses.add("Ceci est un autre test")
        Greeting(responses = responses)
    }
}