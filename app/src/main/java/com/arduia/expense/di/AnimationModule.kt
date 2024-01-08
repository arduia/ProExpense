package com.arduia.expense.di

import androidx.navigation.NavOptions
import com.arduia.expense.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object AnimationModule {

    @Provides
    @LefSideNavOption
    fun provideLeftSlideNavOption(): NavOptions =
        NavOptions.Builder()
            //For Transaction Fragment
            .setEnterAnim(R.anim.expense_enter_left)
            .setPopExitAnim(R.anim.expense_exit_right)
            //For Home Fragment
            .setExitAnim(R.anim.nav_default_exit_anim)
            .setPopEnterAnim(R.anim.nav_default_enter_anim)
            .setLaunchSingleTop(true)
            .build()

    @Provides
    @TopDropNavOption
    fun provideTopDropNavOption(): NavOptions =
        NavOptions.Builder()
            //For Entry Fragment
            .setEnterAnim(R.anim.pop_down_up)
            .setPopExitAnim(R.anim.pop_up_down)
            //For Home Fragment
            .setExitAnim(android.R.anim.fade_out)
            .setPopEnterAnim(R.anim.nav_default_enter_anim)
            .setLaunchSingleTop(true)
            .build()

}

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class TopDropNavOption

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class LefSideNavOption
