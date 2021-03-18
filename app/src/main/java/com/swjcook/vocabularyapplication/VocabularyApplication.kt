package com.swjcook.vocabularyapplication

import android.app.Application
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.swjcook.vocabularyapplication.data.LocalDatabase
import com.swjcook.vocabularyapplication.network.Network
import com.swjcook.vocabularyapplication.repositories.VocabularyListRepository
import com.swjcook.vocabularyapplication.ui.vocabularylistdetail.VocabularyListDetailViewModel
import com.swjcook.vocabularyapplication.ui.vocabularylistsoverview.VocabularyListsOverviewViewModel
import com.swjcook.vocabularyapplication.ui.vocabularypractice.VocabularyPracticeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class VocabularyApplication: MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        val myModule = module {
            viewModel {
                VocabularyListsOverviewViewModel(get())
            }
            viewModel {
                VocabularyListDetailViewModel(get(), get())
            }
            viewModel {
                VocabularyPracticeViewModel(get())
            }

            single {
                VocabularyListRepository(get(), get())
            }
            single {
                Network
            }
            single {
                LocalDatabase.getDatabase(this@VocabularyApplication)
            }
        }

        startKoin {
            androidContext(this@VocabularyApplication)
            modules(listOf(myModule))
        }
    }
}