package org.mifospay.core.common


// JVM and Android implementation
actual fun createPlatformFileUtils(): FileUtils = CommonFileUtils()