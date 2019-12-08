package ru.skillbranch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.skillbranch.di.DI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DI.openRootScope(this)

        if (savedInstanceState == null) removeSplashWithDelay()
    }

    private fun removeSplashWithDelay() {
        supportFragmentManager.fragments.firstOrNull()
            ?.let { splash ->
                window.decorView.postDelayed({
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(0, android.R.anim.fade_out)
                        .remove(splash)
                        .commit()
                }, 5000)
            }
    }
}
