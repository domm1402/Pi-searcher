package com.domm.pisearcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.util.zip.GZIPInputStream

private val bg = Color(0xFF080D18)
private val panel = Color(0xFF111B2C)
private val cyan = Color(0xFF51E4D2)
private val muted = Color(0xFF8493A9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityHolder.context = applicationContext
        window.statusBarColor = android.graphics.Color.rgb(8,13,24)
        window.navigationBarColor = android.graphics.Color.rgb(8,13,24)
        setContent { PiSearcherApp() }
    }
}

@Composable
fun PiSearcherApp() {
    var query by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("Numbers") }
    var depth by remember { mutableIntStateOf(100_000_000) }
    var theme by remember { mutableStateOf("Midnight") }
    var searching by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }
    var scanned by remember { mutableLongStateOf(0L) }
    var results by remember { mutableStateOf(listOf<Long>()) }
    var message by remember { mutableStateOf("100 million digits are stored inside this app.") }
    var page by remember { mutableStateOf("Search") }
    val scope = rememberCoroutineScope()
    val palette = when(theme) {
        "GTA VI" -> Color(0xFFFF67B7)
        "Hacker" -> Color(0xFF55F28B)
        "Christmas" -> Color(0xFFFF6B73)
        "Ocean" -> Color(0xFF54B9FF)
        else -> cyan
    }
    MaterialTheme(colorScheme = darkColorScheme(primary = palette, background = bg, surface = panel)) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF101D32), bg)))) {
            if (theme == "Christmas") Snowfall()
            Column(Modifier.fillMaxSize().statusBarsPadding()) {
                Row(Modifier.fillMaxWidth().padding(horizontal=22.dp, vertical=16.dp), verticalAlignment=Alignment.CenterVertically) {
                    Box(Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(palette.copy(alpha=.15f)), contentAlignment=Alignment.Center) {
                        Text("π", color=palette, fontSize=29.sp, fontWeight=FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("PI SEARCHER", color=Color.White, fontSize=18.sp, fontWeight=FontWeight.Black, letterSpacing=2.sp)
                        Text("THE INFINITE NUMBER HUNT", color=muted, fontSize=9.sp, letterSpacing=1.6.sp)
                    }
                    Box(Modifier.clip(CircleShape).background(Color(0xFF173B3B)).padding(horizontal=10.dp, vertical=6.dp)) {
                        Text("●  OFFLINE", color=palette, fontSize=10.sp, fontWeight=FontWeight.Bold)
                    }
                }
                Row(Modifier.padding(horizontal=20.dp).fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha=.045f)).padding(4.dp)) {
                    listOf("Search","Explore","Themes").forEach { tab ->
                        Box(Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(if(page==tab) panel else Color.Transparent).clickable { page=tab }.padding(vertical=11.dp), contentAlignment=Alignment.Center) {
                            Text(tab, color=if(page==tab) palette else muted, fontWeight=FontWeight.SemiBold, fontSize=13.sp)
                        }
                    }
                }
                when(page) {
                    "Search" -> {
                        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal=20.dp)) {
                            Spacer(Modifier.height(22.dp))
                            Text("Find your number.", color=Color.White, fontSize=28.sp, fontWeight=FontWeight.Bold)
                            Text("Search through the first 100,000,000 digits of π.", color=muted, fontSize=13.sp, modifier=Modifier.padding(top=5.dp))
                            Spacer(Modifier.height(20.dp))
                            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                listOf("Numbers","Letters").forEach { option ->
                                    FilterChip(selected=mode==option, onClick={mode=option}, label={Text(option)}, leadingIcon={ if(mode==option) Icon(Icons.Default.Check, null, Modifier.size(16.dp)) })
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(panel).border(1.dp, Color.White.copy(alpha=.07f), RoundedCornerShape(20.dp)).padding(16.dp)) {
                                Text(if(mode=="Numbers") "NUMBER SEQUENCE" else "TEXT TO FIND", color=muted, fontSize=10.sp, letterSpacing=1.5.sp, fontWeight=FontWeight.Bold)
                                Spacer(Modifier.height(12.dp))
                                BasicTextField(value=query, onValueChange={query=it.take(80)}, singleLine=true, keyboardOptions=KeyboardOptions(keyboardType=if(mode=="Numbers") KeyboardType.Number else KeyboardType.Text), textStyle=androidx.compose.ui.text.TextStyle(color=Color.White, fontSize=21.sp, fontWeight=FontWeight.Medium), cursorBrush=SolidColor(palette), modifier=Modifier.fillMaxWidth(), decorationBox={ inner ->
                                    Box { if(query.isEmpty()) Text(if(mode=="Numbers") "e.g. 123456789" else "e.g. your name", color=muted.copy(alpha=.65f), fontSize=19.sp); inner() }
                                })
                                Spacer(Modifier.height(12.dp))
                                Text(if(mode=="Letters") "A = 01  ·  B = 02  ·  Space = 00" else "Enter any sequence of digits", color=muted, fontSize=11.sp)
                            }
                            Spacer(Modifier.height(18.dp))
                            Text("SEARCH DEPTH", color=muted, fontSize=10.sp, letterSpacing=1.5.sp, fontWeight=FontWeight.Bold)
                            Spacer(Modifier.height(10.dp))
                            Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {
                                listOf(100_000,1_000_000,5_000_000,10_000_000,50_000_000,100_000_000).chunked(3).forEach { row ->
                                    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                                        row.forEach { n ->
                                            val label=if(n>=1_000_000) "${n/1_000_000}M" else "${n/1000}K"
                                            Box(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(if(depth==n) palette.copy(alpha=.16f) else panel).border(1.dp, if(depth==n) palette.copy(alpha=.6f) else Color.Transparent, RoundedCornerShape(12.dp)).clickable { depth=n }.padding(vertical=12.dp), contentAlignment=Alignment.Center) {
                                                Text(label, color=if(depth==n) palette else Color.White, fontSize=13.sp, fontWeight=FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                            Button(onClick={
                                if(searching) return@Button
                                val target=if(mode=="Letters") encodeLetters(query) else query.filter{it.isDigit()}
                                if(target.isEmpty()) { message="Enter something to search first."; return@Button }
                                if(target.length>80) { message="Search sequence is too long."; return@Button }
                                searching=true; results= emptyList(); scanned=0; progress=0f; message="Scanning the bundled π dataset…"
                                scope.launch {
                                    try {
                                        val found=withContext(Dispatchers.IO) { searchPi(target, depth) { count -> scanned=count; progress=count.toFloat()/depth } }
                                        results=found; message=if(found.isEmpty()) "No matches in the first ${depth} digits." else "Found ${found.size} match(es)."; 
                                    } catch(e: Exception) { message="Dataset unavailable. Rebuild using the GitHub Actions APK workflow." }
                                    searching=false
                                }
                            }, enabled=!searching, modifier=Modifier.fillMaxWidth().height(54.dp), shape=RoundedCornerShape(16.dp), colors=ButtonDefaults.buttonColors(containerColor=palette, contentColor=bg)) {
                                if(searching) { CircularProgressIndicator(Modifier.size(19.dp), color=bg, strokeWidth=2.dp); Spacer(Modifier.width(10.dp)); Text("SEARCHING…") } else { Icon(Icons.Default.Search, null); Spacer(Modifier.width(9.dp)); Text("SEARCH π", fontWeight=FontWeight.Black, letterSpacing=1.sp) }
                            }
                            if(searching) {
                                Spacer(Modifier.height(14.dp))
                                LinearProgressIndicator(progress={progress.coerceIn(0f,1f)}, modifier=Modifier.fillMaxWidth().height(5.dp).clip(CircleShape), color=palette, trackColor=panel)
                                Text("${"%,d".format(scanned)} / ${"%,d".format(depth)} digits scanned", color=muted, fontSize=11.sp, modifier=Modifier.padding(top=7.dp))
                            }
                            Spacer(Modifier.height(18.dp))
                            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(panel).padding(16.dp)) {
                                Text("RESULTS", color=muted, fontSize=10.sp, fontWeight=FontWeight.Bold, letterSpacing=1.5.sp)
                                Text(message, color=Color.White, fontSize=13.sp, modifier=Modifier.padding(top=8.dp))
                                results.take(100).forEachIndexed { i, pos ->
                                    Row(Modifier.fillMaxWidth().padding(top=10.dp), verticalAlignment=Alignment.CenterVertically) {
                                        Text("#${i+1}", color=palette, fontSize=12.sp, fontWeight=FontWeight.Bold, modifier=Modifier.width(38.dp))
                                        Text("Position ${"%,d".format(pos)}", color=Color.White, fontSize=13.sp, modifier=Modifier.weight(1f))
                                        Icon(Icons.Default.CheckCircle, null, tint=palette, modifier=Modifier.size(16.dp))
                                    }
                                }
                            }
                            Spacer(Modifier.height(28.dp))
                        }
                    }
                    "Explore" -> Column(Modifier.weight(1f).padding(20.dp)) {
                        Text("Inside π", color=Color.White, fontSize=27.sp, fontWeight=FontWeight.Bold)
                        Text("A closer look at the digits stored on your device.", color=muted, fontSize=13.sp, modifier=Modifier.padding(top=5.dp,bottom=18.dp))
                        StatCard("100,000,000","DIGITS BUNDLED",palette)
                        StatCard("3.14159265358979323846","THE OPENING DIGITS",palette)
                        StatCard("100 MB+","RAW NUMBER DATA",palette)
                        Text("The complete dataset is packaged in the APK. Searching works offline after installation.", color=muted, fontSize=13.sp, modifier=Modifier.padding(top=14.dp))
                    }
                    else -> Column(Modifier.weight(1f).padding(20.dp)) {
                        Text("Choose your atmosphere", color=Color.White, fontSize=25.sp, fontWeight=FontWeight.Bold)
                        Text("Change the look of your number-hunting setup.", color=muted, fontSize=13.sp, modifier=Modifier.padding(top=5.dp,bottom=18.dp))
                        listOf("Midnight","Hacker","Christmas","GTA VI","Ocean").forEach { t ->
                            Row(Modifier.fillMaxWidth().padding(bottom=10.dp).clip(RoundedCornerShape(16.dp)).background(panel).clickable { theme=t }.padding(16.dp), verticalAlignment=Alignment.CenterVertically) {
                                Box(Modifier.size(12.dp).clip(CircleShape).background(when(t){"Hacker"->Color(0xFF55F28B);"Christmas"->Color(0xFFFF6B73);"GTA VI"->Color(0xFFFF67B7);"Ocean"->Color(0xFF54B9FF);else->cyan}))
                                Text(t, color=Color.White, fontWeight=FontWeight.SemiBold, modifier=Modifier.weight(1f).padding(start=12.dp))
                                if(theme==t) Icon(Icons.Default.CheckCircle, null, tint=palette)
                            }
                        }
                        Text("Christmas adds falling snow. Hacker mode switches to terminal green. GTA VI brings Vice City neon.", color=muted, fontSize=12.sp, modifier=Modifier.padding(top=8.dp))
                    }
                }
            }
        }
    }
}

@Composable private fun StatCard(value:String, label:String, accent:Color) {
    Column(Modifier.fillMaxWidth().padding(bottom=12.dp).clip(RoundedCornerShape(18.dp)).background(panel).padding(18.dp)) {
        Text(label, color=muted, fontSize=10.sp, letterSpacing=1.5.sp, fontWeight=FontWeight.Bold)
        Text(value, color=accent, fontSize=23.sp, fontWeight=FontWeight.Bold, modifier=Modifier.padding(top=7.dp))
    }
}

@Composable private fun Snowfall() {
    val transition= rememberInfiniteTransition(label="snow")
    val phase by transition.animateFloat(0f,1f, infiniteRepeatable(tween(9000, easing=LinearEasing)), label="fall")
    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
        for(i in 0 until 55) {
            val x=((i*97)%size.width.toInt().coerceAtLeast(1)).toFloat()
            val y=((i*173 + phase*size.height*1.4f)%size.height)
            drawCircle(Color.White.copy(alpha=.42f), radius=if(i%4==0) 2.5f else 1.3f, center=androidx.compose.ui.geometry.Offset(x,y))
        }
    }
}

private fun encodeLetters(input:String):String = input.uppercase().map { c -> when { c in 'A'..'Z' -> "%02d".format(c-'A'+1); c==' ' -> "00"; else -> "" } }.joinToString("")

private fun searchPi(pattern:String, limit:Int, onProgress:(Long)->Unit):List<Long> {
    val failure=IntArray(pattern.length)
    var j=0
    for(i in 1 until pattern.length) {
        while(j>0 && pattern[i]!=pattern[j]) j=failure[j-1]
        if(pattern[i]==pattern[j]) j++
        failure[i]=j
    }
    val found=ArrayList<Long>()
    val input=GZIPInputStream(BufferedInputStream(MainActivityHolder.context!!.assets.open("pi-100m.txt.gz"), 1 shl 16), 1 shl 16)
    input.use { stream ->
        var pos=0L; var matched=0; val buffer=ByteArray(1 shl 16); var lastReport=0L
        while(pos<limit && found.size<100) {
            val wanted=minOf(buffer.size.toLong(), limit-pos).toInt()
            val n=stream.read(buffer,0,wanted); if(n<0) break
            for(k in 0 until n) {
                val c=buffer[k].toInt().toChar()
                while(matched>0 && c!=pattern[matched]) matched=failure[matched-1]
                if(c==pattern[matched]) matched++
                pos++
                if(matched==pattern.length) { found.add(pos-pattern.length); matched=failure[matched-1] }
            }
            if(pos-lastReport>=500_000) { onProgress(pos); lastReport=pos }
        }
        onProgress(pos)
    }
    return found
}

private object MainActivityHolder { var context: android.content.Context? = null }
