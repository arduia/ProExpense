package com.arduia.expense.feature.sync.di

import com.arduia.expense.feature.sync.ConnectDriveAccountUseCase
import com.arduia.expense.feature.sync.DisconnectDriveAccountUseCase
import org.koin.dsl.module

/** [com.arduia.expense.feature.sync.DriveAuthManager] and [com.arduia.expense.feature.sync.DriveRemoteDataSource]
 * bindings live in the androidMain-only `syncPlatformModule` (Credential Manager/Ktor are Android-only). */
val syncModule =
    module {
        factory { ConnectDriveAccountUseCase(get(), get()) }
        factory { DisconnectDriveAccountUseCase(get(), get()) }
    }
