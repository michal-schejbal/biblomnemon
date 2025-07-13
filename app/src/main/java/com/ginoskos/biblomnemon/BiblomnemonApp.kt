package com.ginoskos.biblomnemon

import android.app.Application
import com.ginoskos.biblomnemon.app.Modules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class BiblomnemonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BiblomnemonApp)
            modules(Modules.items)
        }
    }
}