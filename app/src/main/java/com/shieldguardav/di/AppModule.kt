package com.shieldguardav.di

import android.content.Context
import androidx.room.Room
import com.shieldguardav.data.local.dao.*
import com.shieldguardav.data.local.database.ShieldGuardDatabase
import com.shieldguardav.data.repository.*
import com.shieldguardav.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ShieldGuardDatabase {
        return Room.databaseBuilder(
            context,
            ShieldGuardDatabase::class.java,
            "shieldguard_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideScanResultDao(database: ShieldGuardDatabase): ScanResultDao {
        return database.scanResultDao()
    }

    @Provides
    fun provideMalwareSignatureDao(database: ShieldGuardDatabase): MalwareSignatureDao {
        return database.malwareSignatureDao()
    }

    @Provides
    fun provideScanHistoryDao(database: ShieldGuardDatabase): ScanHistoryDao {
        return database.scanHistoryDao()
    }

    @Provides
    fun provideQuarantineDao(database: ShieldGuardDatabase): QuarantineDao {
        return database.quarantineDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAppRepository(impl: AppRepositoryImpl): AppRepository

    @Binds
    @Singleton
    abstract fun bindSecurityRepository(impl: SecurityRepositoryImpl): SecurityRepository

    @Binds
    @Singleton
    abstract fun bindMalwareDatabaseRepository(impl: MalwareDatabaseRepositoryImpl): MalwareDatabaseRepository

    @Binds
    @Singleton
    abstract fun bindAntivirusRepository(impl: AntivirusRepositoryImpl): AntivirusRepository
}