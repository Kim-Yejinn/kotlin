import kotlin.script.experimental.jvm.util.classpathFromClass

buildscript{
    dependencies{
        classpath("io.realm:realm-gradle-plugin:10.7.0")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("io.realm.kotlin") version "1.11.1" apply false

}