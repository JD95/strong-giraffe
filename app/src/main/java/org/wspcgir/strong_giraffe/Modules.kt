package org.wspcgir.strong_giraffe

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.wspcgir.strong_giraffe.repository.AppDatabase
import org.wspcgir.strong_giraffe.repository.AppRepository
import org.wspcgir.strong_giraffe.repository.MIGRATION_1_2
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppRepositoryModule {
    @Singleton
    @Provides
    fun provideAppRepository(
        @ApplicationContext context: Context
    ): AppRepository {
        val db =
            Room.databaseBuilder(context, AppDatabase::class.java, "data.db")
                .addMigrations(MIGRATION_1_2)
                .build()
        val dao = db.dao()
        return AppRepository(dao)
    }
}
