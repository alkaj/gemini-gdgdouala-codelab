package com.yourdomainorname.example.testinggemini0

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.ai.client.generativeai.GenerativeModel
import com.yourdomainorname.example.testinggemini0.ui.theme.TestingGemini0Theme
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
            val responsesList = remember { SnapshotStateList< Pair<Int, String>>() }
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
                                val finalResponse = Pair(2, if (null == response.text) "Pas de restour!" else response.text!!)
                                responsesList.add(Pair(1, prompt.value))
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
fun Greeting(modifier: Modifier = Modifier, prompt: MutableState<String> = remember { mutableStateOf("") }, responses: SnapshotStateList<Pair<Int, String>>, onSend: (promptContent: String) -> Unit = {})
{
    LazyColumn(
        reverseLayout = true,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()) {
                TextField(
                    value = prompt.value,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {onSend(prompt.value)}
                    ),
                    shape = shapes.medium,
                    onValueChange = {prompt.value = it},
                    modifier = Modifier.fillMaxWidth(.7f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onSend(prompt.value) }) {
                    Text(text = "Send")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        items(responses.size) {
            val response = responses.reversed()[it]
            if (response.first == 1)
            {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End) {
                    Text(text = response.second, modifier = Modifier
                        .background(colorScheme.onBackground.copy(.1f), shape = shapes.medium)
                        .padding(vertical = 4.dp, horizontal = 8.dp))
                }
            } else Text(text = response.second, modifier = Modifier
                .background(colorScheme.primary.copy(.1f), shape = shapes.medium)
                .padding(vertical = 4.dp, horizontal = 8.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    TestingGemini0Theme {
        val responses = SnapshotStateList<Pair<Int, String>>()
        responses.add(Pair(1, "C'est quoi ca?"))
        responses.add(Pair(2, "C'est un test"))
        Greeting(responses = responses)
    }
}