package ru.skillbranch.di.root

import dagger.Component
import ru.skillbranch.MainActivity
import javax.inject.Singleton


/**
 * @author neestell on 2019-12-08.
 */
@Singleton
@Component(modules = [NetworkModule::class])
interface RootComponent {

    fun inject(activity: MainActivity)
}