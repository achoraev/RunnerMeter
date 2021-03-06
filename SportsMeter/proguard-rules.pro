# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:/Program Files (x86)/Android/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-keepclassmembers class com.dom925.xxxx {
#   public *;
#}
-dontwarn com.google.android.gms.internal.**
#-dontwarn com.parse.ParseApacheHttpClient
#-dontwarn com.parse.ParseOkHttpClient
#-dontwarn com.parse.ParseApacheHttpClient$ParseApacheHttpEntity
#-dontwarn com.parse.ParseOkHttpClient$1
#-dontwarn com.parse.ParseOkHttpClient$1$1
#-dontwarn com.parse.ParseOkHttpClient$1$2
#-dontwarn com.parse.ParseOkHttpClient$ParseOkHttpRequestBody
#-dontwarn com.parse.twitter.Twitter
#-dontwarn com.parse.internal.signpost.**
#-dontwarn com.parse.NotificationCompat$NotificationCompatImplBase
