package com.arduia.expense.feature.sync.di

import com.arduia.expense.feature.sync.DriveAuthManager
import com.arduia.expense.feature.sync.DriveAuthManagerImpl
import com.arduia.expense.feature.sync.DriveRemoteDataSource
import com.arduia.expense.feature.sync.EncryptedSyncTokenStore
import com.arduia.expense.feature.sync.KtorDriveRemoteDataSource
import com.arduia.expense.feature.sync.R
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Android-only bindings (Credential Manager, Ktor/OkHttp) for [syncModule]'s use cases. */
val syncPlatformModule =
    module {
        single { EncryptedSyncTokenStore(androidContext()) }
        single<DriveAuthManager> {
            DriveAuthManagerImpl(
                context = androidContext(),
                tokenStore = get(),
                serverClientId = androidContext().getString(R.string.sync_google_oauth_client_id),
            )
        }
        single<DriveRemoteDataSource> { KtorDriveRemoteDataSource(tokenStore = get()) }
    }
