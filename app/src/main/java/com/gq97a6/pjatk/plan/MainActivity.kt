package com.gq97a6.pjatk.plan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.gq97a6.pjatk.plan.ui.theme.PlanPJATKTheme
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var test = 1

        setContent {
            PlanPJATKTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Button(onClick = {
                        try {
                            GlobalScope.launch {
                                getDocumentByUrl("https://alteratom.com/")
                            }
                        } catch (e:Exception) {
                            run {}
                        }
                    }) {
                        Text(text = "GET SITE")
                    }
                }
            }
        }
    }
}

fun getDocumentByUrl(urlToScrape: String) =
    skrape(BrowserFetcher) { // <--- pass BrowserFetcher to include rendered JS
        request { url = urlToScrape }
        response { htmlDocument { this } }
    }

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PlanPJATKTheme {
        Greeting("Android")
    }
}