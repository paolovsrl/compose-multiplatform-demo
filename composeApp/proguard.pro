# to remove all the Warnings in the Standard output log
-dontwarn org.apache.logging.log4j.**
-dontwarn org.apache.logging.slf4j.**
-dontwarn io.github.oshai.kotlinlogging.logback.**

# Mandatory
-keep class org.omsi.** { *; }
# ViewModel
-keep class kotlinx.**
# Filekit
-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }