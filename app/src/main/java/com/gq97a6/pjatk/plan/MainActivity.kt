package com.gq97a6.pjatk.plan

import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.gq97a6.pjatk.plan.ui.theme.PlanPJATKTheme
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.Method
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.File


var rootFolder = ""

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootFolder = filesDir.canonicalPath.toString()

        setContent {
            PlanPJATKTheme {

                var html by remember { mutableStateOf("") }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AndroidView(factory = {
                        WebView(it).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = WebViewClient()
                            loadData(html, "text/html", "")
                        }
                    }, update = {
                        it.loadData(html, "text/html", "")
                    }, modifier = Modifier.weight(10f))

                    Button(
                        onClick = {
                            GlobalScope.launch {
                                html = getHTMLJsoup()
                                run {}
                            }
                        },
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(text = "GET SITE")
                    }
                }
            }
        }
    }
}

fun getHTMLJsoup(): String {
    val response2: Connection.Response = Jsoup.connect("https://planzajec.pjwstk.edu.pl/Logowanie.aspx")
        .method(Connection.Method.GET)
        .execute()

    val responseDocument: Document = response2.parse()

    val eventValidation = responseDocument.select("input[name=__EVENTVALIDATION]").first().attr("value")
    val viewState = responseDocument.select("input[name=__VIEWSTATE]").first().attr("value")
    val viewStateGen = responseDocument.select("input[name=__VIEWSTATEGENERATOR]").first().attr("value")

    val response: Connection.Response = Jsoup.connect("https://planzajec.pjwstk.edu.pl/Logowanie.aspx")
        .method(Connection.Method.POST)
        .data("__EVENTTARGET", "")
        .data("__EVENTARGUMENT", "")
        .data("__VIEWSTATE", viewState)
        .data("__VIEWSTATEGENERATOR", viewStateGen)
        .data("__EVENTVALIDATION", eventValidation)
        .data("ctl00\$ContentPlaceHolder1\$Login1\$UserName", "")
        .data("ctl00\$ContentPlaceHolder1\$Login1\$Password", "")
        .data("ctl00\$ContentPlaceHolder1\$Login1\$LoginButton", "Zaloguj")
        .execute()

    val cookies: Map<String, String> = response.cookies()
    val homePage: Document = Jsoup.connect("https://planzajec.pjwstk.edu.pl/TwojPlan.aspx").cookies(cookies).get()
    return homePage.body().html()
}

fun getHTML() = skrape(BrowserFetcher) {
    request { url = "https://planzajec.pjwstk.edu.pl/Logowanie.aspx" }

    val site = response { htmlDocument { this } }

    request {
        method = Method.POST
        followRedirects = true
        url = "https://planzajec.pjwstk.edu.pl/Logowanie.aspx"
        //url = "https://www.w3schools.com/action_page.php"

        body {
            form {
                "ctl00\$ContentPlaceHolder1\$Login1\$UserName" to "s24271"
                "ctl00\$ContentPlaceHolder1\$Login1\$Password" to "AgrestTruskawka95!"
                "ctl00\$ContentPlaceHolder1\$Login1\$LoginButton" to "Zaloguj"
            }
        }
    }
    response { htmlDocument { this } }.apply { File("$rootFolder/LAST_SITE.html").writeText(this.html) }.html
}