# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/naman/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Rules for: uCrop - Image Cropping Library for Android
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

-dontwarn com.sun.tools.javac.code.Symbol$ClassSymbol
-dontwarn com.sun.tools.javac.code.Symbol$PackageSymbol
-dontwarn com.sun.tools.javac.code.Symbol
-dontwarn com.sun.tools.javac.tree.TreeScanner
-dontwarn com.sun.tools.javac.util.Name
-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.Modifier
-dontwarn javax.lang.model.type.TypeMirror
-dontwarn javax.lang.model.type.TypeVisitor
-dontwarn javax.lang.model.util.SimpleTypeVisitor7
-dontwarn groovy.lang.GroovyObject
-dontwarn groovy.lang.MetaClass
-dontwarn java.lang.management.ManagementFactory
-dontwarn javax.management.InstanceNotFoundException
-dontwarn javax.management.MBeanRegistrationException
-dontwarn javax.management.MBeanServer
-dontwarn javax.management.MalformedObjectNameException
-dontwarn javax.management.ObjectInstance
-dontwarn javax.management.ObjectName
-dontwarn javax.naming.Context
-dontwarn javax.naming.InitialContext
-dontwarn javax.naming.NamingException
-dontwarn javax.servlet.ServletContainerInitializer
-dontwarn org.codehaus.groovy.reflection.ClassInfo
-dontwarn org.codehaus.groovy.runtime.BytecodeInterface8
-dontwarn org.codehaus.groovy.runtime.ScriptBytecodeAdapter
-dontwarn org.codehaus.groovy.runtime.callsite.CallSite
-dontwarn org.codehaus.groovy.runtime.callsite.CallSiteArray
-dontwarn org.codehaus.janino.ClassBodyEvaluator
-dontwarn sun.reflect.Reflection