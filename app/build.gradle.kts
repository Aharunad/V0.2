plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
 namespace = "org.pages.reader"
 compileSdk = 35
 defaultConfig { applicationId = "org.pages.reader"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
 androidResources { noCompress += listOf("onnx", "wasm", "bin", "npy", "model") }
}
dependencies { implementation("androidx.webkit:webkit:1.12.1") }
