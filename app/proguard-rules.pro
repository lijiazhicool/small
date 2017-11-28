# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Mtime/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes Exceptions,InnerClasses
-keepattributes EnclosingMethod
-keepattributes InnerClasses


-keep class com.google.android.gms.internal.** { *; }
-keep class com.facebook.ads.** { *; }
-dontwarn com.facebook.ads.**

-keep public class com.av.ringtone.R$*{
    public static final int *;
}

-keep class com.av.ringtone.model.** { *; }

# 第三方包 jl1.0.1.jar
-dontwarn javazoom.jl.**
-keep class javazoom.jl.** { *;}

#google ad
-dontwarn com.google.firebase.**
-keep public class com.google.firebase.** { *; }
-dontwarn com.google.android.gms.**
-keep public class com.google.android.gms.** { *; }