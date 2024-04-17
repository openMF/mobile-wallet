package org.mifospay.core.network.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SelfServiceApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FineractApi
