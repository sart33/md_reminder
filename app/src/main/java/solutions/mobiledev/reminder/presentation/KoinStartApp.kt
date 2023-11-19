package solutions.mobiledev.reminder.presentation

import android.app.Application
import android.content.Context
import solutions.mobiledev.reminder.presentation.widget.UpdatesHelper
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class KoinStartApp : Application() {

    companion object {
        private var isKoinInitialized = false

        fun initializeKoinOnce(context: Context) {
            if (!isKoinInitialized) {
                startKoin {
                    // настройки Koin
                    androidLogger()
                    androidContext(context.applicationContext)
                    modules(appModule)
                }
                isKoinInitialized = true
            }
        }

        private val appModule = module {
            // Здесь вы можете определить ваши зависимости
            single { UpdatesHelper(get()) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initializeKoinOnce(this)
    }
}