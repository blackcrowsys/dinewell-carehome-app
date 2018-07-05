package com.blackcrowsys.di

import android.app.Application
import android.arch.persistence.room.Room
import android.preference.PreferenceManager
import com.blackcrowsys.api.ApiService
import com.blackcrowsys.api.util.HttpInterceptor
import com.blackcrowsys.exceptions.ErrorMapper
import com.blackcrowsys.exceptions.ExceptionTransformer
import com.blackcrowsys.persistence.CareHomeDatabase
import com.blackcrowsys.persistence.dao.AllergyDao
import com.blackcrowsys.persistence.dao.ResidentAllergyDao
import com.blackcrowsys.persistence.dao.ResidentDao
import com.blackcrowsys.persistence.dao.UserPermissionDao
import com.blackcrowsys.util.SchedulerProvider
import com.blackcrowsys.util.SharedPreferencesHandler
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideSchedulerProvider() = SchedulerProvider(Schedulers.io(), AndroidSchedulers.mainThread())

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("dd-MM-yyyy")
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHandler(application: Application): SharedPreferencesHandler {
        val preferences = PreferenceManager.getDefaultSharedPreferences(application)
        return SharedPreferencesHandler(RxSharedPreferences.create(preferences))
    }

    @Provides
    @Singleton
    fun provideHttpInterceptorBaseUrl(sharedPreferencesHandler: SharedPreferencesHandler): HttpInterceptor {
        return HttpInterceptor(sharedPreferencesHandler)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(application: Application, httpInterceptor: HttpInterceptor): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        val cacheDir = File(application.cacheDir, UUID.randomUUID().toString())
        // 10 MiB cache
        val cache = Cache(cacheDir, 10 * 1024 * 1024)

        return OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(httpInterceptor)
                .addInterceptor(interceptor)
                .build()
    }

    @Provides
    @Singleton
    fun provideApiService(gson: Gson, okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
                .baseUrl("http://localhost")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build().create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideExceptionTransformer(application: Application): ExceptionTransformer {
        val errorMapper = ErrorMapper(application)
        return ExceptionTransformer(errorMapper)
    }

    @Provides
    @Singleton
    fun provideDatabase(application: Application): CareHomeDatabase =
        Room.databaseBuilder(application, CareHomeDatabase::class.java, "carehome.db").build()

    @Provides
    @Singleton
    fun provideUserPermissionDao(careHomeDatabase: CareHomeDatabase): UserPermissionDao =
        careHomeDatabase.userPermissionDao()

    @Provides
    @Singleton
    fun provideResidentDao(careHomeDatabase: CareHomeDatabase): ResidentDao =
        careHomeDatabase.residentDao()

    @Provides
    @Singleton
    fun provideResidentAllergyDao(careHomeDatabase: CareHomeDatabase): ResidentAllergyDao =
            careHomeDatabase.residentAllergyDao()

    @Provides
    @Singleton
    fun provideAllergyDao(careHomeDatabase: CareHomeDatabase): AllergyDao =
            careHomeDatabase.allergyDao()
}