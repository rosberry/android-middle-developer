package ru.skillbranch.skillarticles

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_root.*
import ru.skillbranch.skillarticles.extensions.dpToIntPx

class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = if (toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null
        logo?.let {
            logo.scaleType = ImageView.ScaleType.CENTER_CROP
            (logo.layoutParams as? Toolbar.LayoutParams)?.apply {
                width = dpToIntPx(40)
                height = dpToIntPx(40)
                marginEnd = dpToIntPx(16)
                logo.layoutParams = this
            }
        }
    }
}
