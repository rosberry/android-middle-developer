package ru.skillbranch.di

import android.content.Context
import ru.skillbranch.di.root.RootComponent


/**
 * @author neestell on 2019-12-08.
 */
object DI {

    private var rootComponent: RootComponent? = null

    fun openRootScope(context: Context): RootComponent {
        return rootComponent.thisOrNew {
            DaggerRootComponent.builder()
                .application(context.applicationContext)
                .build()
        }
            .also { rootComponent -> this.rootComponent = rootComponent }
    }

    private inline fun <T> T?.thisOrNew(creator: () -> T): T {
        return this ?: creator.invoke()
    }
}