import java.util.Properties

plugins {
    id("com.android.application")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

android {
    namespace = "com.example.d4_3a04"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.d4_3a04"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GPT_API_KEY", "\"${localProperties["GPT_API_KEY"]}\"")
        buildConfigField("String", "DB_USER", "\"${localProperties["DB_USER"]}\"")
        buildConfigField("String", "DB_PASSWORD", "\"${localProperties["DB_PASSWORD"]}\"")
        buildConfigField("String", "DB_CONNECTION", "\"${localProperties["DB_CONNECTION"]}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}


dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation ("com.squareup.retrofit2:converter-jackson:2.7.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.3")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.3")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.3")


    implementation(files("libs/mariadb-java-client-3.3.3.jar"))
    implementation(files("libs/mysql-connector-java-5.1.49.jar"))

//    compileOnly("org.igniterealtime.smack:smack-android-extensions:4.2.0-alpha3")
//    compileOnly("org.igniterealtime.smack:smack-tcp:4.2.0-alpha3")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}