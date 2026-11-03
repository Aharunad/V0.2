package org.pages.reader
import android.app.Activity
import android.os.Bundle
import android.webkit.*
import android.net.Uri
import android.util.Base64
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : Activity() {
 private lateinit var web: WebView
 private var chooser: ValueCallback<Array<Uri>>? = null
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  web = WebView(this)
  web.settings.javaScriptEnabled = true
  web.settings.domStorageEnabled = true
  web.settings.databaseEnabled = true
  web.settings.allowFileAccess = false
  web.settings.mediaPlaybackRequiresUserGesture = false
  web.addJavascriptInterface(PagesBridge(), "PAGESNative")
  val loader = WebViewAssetLoader.Builder().addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this)).build()
  web.webViewClient = object : WebViewClientCompat() {
   override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest) = loader.shouldInterceptRequest(request.url)
  }
  web.webChromeClient = object : WebChromeClient() {
   override fun onShowFileChooser(v: WebView, cb: ValueCallback<Array<Uri>>, params: FileChooserParams): Boolean {
    chooser?.onReceiveValue(null); chooser = cb
    return try { startActivityForResult(params.createIntent(), 701); true } catch (_: Exception) { chooser = null; false }
   }
  }
  setContentView(web)
  web.loadUrl("https://appassets.androidplatform.net/assets/index.html")
 }
 inner class PagesBridge {
  private val hosts = listOf("feeds.bbci.co.uk","www.theguardian.com","feeds.npr.org","feeds.arstechnica.com","theconversation.com","ichef.bbci.co.uk","i.guim.co.uk","media.npr.org")
  private fun safe(s:String):URL { val u=URL(s); require(u.protocol=="https" && hosts.any { u.host==it || u.host.endsWith("."+it) }); return u }
  @JavascriptInterface fun getText(s:String):String {
   val c=safe(s).openConnection() as HttpURLConnection; c.connectTimeout=10000; c.readTimeout=12000
   c.setRequestProperty("User-Agent","PAGESReader/1.0")
   return c.inputStream.bufferedReader().use { it.readText().take(1500000) }.also { c.disconnect() }
  }
  @JavascriptInterface fun fetchBase64(s:String):String = try {
   val c=safe(s).openConnection() as HttpURLConnection; c.connectTimeout=8000; c.readTimeout=10000
   val type=c.contentType ?: "image/jpeg"; require(type.startsWith("image/"))
   val bytes=c.inputStream.use { it.readNBytes(1200000) }; c.disconnect()
   Base64.encodeToString(bytes,Base64.NO_WRAP)
  } catch (_:Exception) { "" }
 }
 @Deprecated("Deprecated in Java")
 override fun onActivityResult(requestCode:Int,resultCode:Int,data:android.content.Intent?) {
  super.onActivityResult(requestCode,resultCode,data)
  if(requestCode==701){ val cb=chooser; chooser=null; cb?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode,data)) }
 }
 override fun onBackPressed(){ web.evaluateJavascript("document.querySelector('#player').classList.remove('open'); if(document.querySelector('#reader').classList.contains('active')){document.querySelectorAll('.view').forEach(x=>x.classList.remove('active'));document.querySelector('#library').classList.add('active')}",null) }
 override fun onDestroy(){ web.destroy(); super.onDestroy() }
}
