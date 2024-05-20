import java.util.Properties
import java.io.FileInputStream


var properties = Properties()
properties.load(FileInputStream("local.properties"))

// Load local.properties file
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}
// Read the API key from local.properties
val kakaoMapApiKey: String = localProperties.getProperty("KAKAO_MAP_API_KEY") ?: "default_key"


plugins {
    id("com.android.application")
}

android {

    namespace = "com.school.bus_autosystem"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.school.bus_autosystem"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        //환경변수
        manifestPlaceholders["KAKAO_MAP_API_KEY"] = kakaoMapApiKey
        // Add API key to BuildConfig
        buildConfigField("String", "KAKAO_MAP_API_KEY", "\"$kakaoMapApiKey\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }


    buildTypes {
        
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }



}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.kakao.maps.open:android:2.9.5")
    implementation ("com.google.android.gms:play-services-location:21.1.0")
}