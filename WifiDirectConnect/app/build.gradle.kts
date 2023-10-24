plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {

    // 뷰 바인딩을 위함.
    buildFeatures{
        viewBinding = true
    }
    namespace = "com.example.wifidirectconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wifidirectconnect"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    
    // 프레그먼트 사용을 위함
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    // permission관련 dependency
    implementation("androidx.activity:activity-ktx:1.8.0")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}