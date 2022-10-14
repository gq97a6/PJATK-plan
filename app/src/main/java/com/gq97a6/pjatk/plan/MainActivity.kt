package com.gq97a6.pjatk.plan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gq97a6.pjatk.plan.ui.theme.PlanPJATKTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File


var rootFolder = ""

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootFolder = filesDir.canonicalPath.toString()

        setContent {
            PlanPJATKTheme {

                var html by remember { mutableStateOf("ŁADOWANIE...") }

                var pass by remember { mutableStateOf("") }
                var login by remember { mutableStateOf("") }

                var logined by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    EditText(
                        label = { Text("Login") },
                        modifier = Modifier.padding(20.dp),
                        value = login,
                        onValueChange = {
                            login = it
                        })

                    EditText(
                        label = { Text("Password") },
                        modifier = Modifier.padding(20.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        value = pass,
                        onValueChange = {
                            pass = it
                        })

                    BasicButton(
                        onClick = {
                            logined = true
                            GlobalScope.launch { html = getHTMLJsoup(login, pass) }
                        },
                        Modifier
                            .padding(20.dp)
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Login")
                    }

                    if (logined) {
                        Dialog({ logined = false }) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 50.dp, horizontal = 10.dp)
                                    .background(Color.White)
                            ) {
                                Text(text = html, modifier = Modifier.padding(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getHTMLJsoup(login: String, pass: String): String {
    var response = Jsoup
        .connect("https://planzajec.pjwstk.edu.pl/Logowanie.aspx")
        .method(Connection.Method.GET)
        .execute()

    val responseDocument = response.parse()

    val eventValidation = responseDocument
        .select("input[name=__EVENTVALIDATION]")
        .first()
        .attr("value")

    val viewState = responseDocument
        .select("input[name=__VIEWSTATE]")
        .first()
        .attr("value")

    val viewStateGen = responseDocument
        .select("input[name=__VIEWSTATEGENERATOR]")
        .first()
        .attr("value")

    response = Jsoup.connect("https://planzajec.pjwstk.edu.pl/Logowanie.aspx")
        .method(Connection.Method.POST)
        .data("__EVENTTARGET", "")
        .data("__EVENTARGUMENT", "")
        .data("__VIEWSTATE", viewState)
        .data("__VIEWSTATEGENERATOR", viewStateGen)
        .data("__EVENTVALIDATION", eventValidation)
        .data("ctl00\$ContentPlaceHolder1\$Login1\$UserName", login)
        .data("ctl00\$ContentPlaceHolder1\$Login1\$Password", pass)
        .data("ctl00\$ContentPlaceHolder1\$Login1\$LoginButton", "Zaloguj")
        .execute()

    val homePage: Document = Jsoup
        .connect("https://planzajec.pjwstk.edu.pl/TwojPlan.aspx")
        .cookies(response.cookies())
        .get()

    return homePage.select("div[class=rsAptContent]")
        .map { "${it.childNodes()[0]}".replace("\n", "") }
        .filter { it.isNotBlank() }
        .joinToString("\n")
        .apply { File("$rootFolder/LAST_SITE.html").writeText(this) }
}

//fun getHTML() = skrape(BrowserFetcher) {
//    request { url = "https://planzajec.pjwstk.edu.pl/Logowanie.aspx" }
//
//    val site = response { htmlDocument { this } }
//
//    request {
//        method = Method.POST
//        followRedirects = true
//        url = "https://planzajec.pjwstk.edu.pl/Logowanie.aspx"
//        //url = "https://www.w3schools.com/action_page.php"
//
//        body {
//            form {
//                "ctl00\$ContentPlaceHolder1\$Login1\$UserName" to "s24271"
//                "ctl00\$ContentPlaceHolder1\$Login1\$Password" to "AgrestTruskawka95!"
//                "ctl00\$ContentPlaceHolder1\$Login1\$LoginButton" to "Zaloguj"
//            }
//        }
//    }
//    response { htmlDocument { this } }.apply { File("$rootFolder/LAST_SITE.html").writeText(this.html) }.html
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditText(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small
) {
    OutlinedTextField(
        enabled = enabled,
        readOnly = readOnly,
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(60.dp)
            .fillMaxWidth(),
        singleLine = singleLine,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        maxLines = maxLines ?: 1,
        interactionSource = interactionSource,
        shape = shape
    )
}

@Composable
fun BasicButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke = BorderStroke(2.dp, Color.Black),
    contentPadding: PaddingValues = PaddingValues(13.dp),
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(enabled, role = Role.Button, onClick = onClick)
            .border(border, shape)
            .padding(contentPadding),
        contentAlignment = contentAlignment
    ) {
        content()
    }
}