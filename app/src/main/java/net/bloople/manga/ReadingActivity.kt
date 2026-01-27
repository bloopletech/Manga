package net.bloople.manga

import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import net.bloople.manga.ThrottledOnClickListener.Companion.wrap


class ReadingActivity : AppCompatActivity() {
    private lateinit var session: ReadingSession
    private lateinit var pager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reading)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        pager = findViewById(R.id.pager)

        val prev10 = findViewById<ImageView>(R.id.prev_10)
        prev10.setOnClickListener(wrap { session.go(-10) })

        val next10 = findViewById<ImageView>(R.id.next_10)
        next10.setOnClickListener(wrap { session.go(10) })

        val intent = intent

        val libraryId = intent.getLongExtra("libraryId", -1)

        lifecycleScope.launch {
            val library = LibraryService.ensureLibrary(this@ReadingActivity, libraryId) ?: return@launch

            val bookId = intent.getLongExtra("_id", -1)
            val book = library.books[bookId] ?: return@launch

            session = ReadingSession(library, book)
            session.bind(this@ReadingActivity, pager)

            if(intent.getBooleanExtra("resume", false)) session.resume()
            if(savedInstanceState != null) {
                val pageFromBundle = savedInstanceState.getInt("page", -1)
                if(pageFromBundle != -1) session.page(pageFromBundle)
            }

            session.start()
        }
    }

    public override fun onRestart() {
        super.onRestart()
        if(::session.isInitialized) session.start()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("page", session.page())
        super.onSaveInstanceState(outState)
    }

    public override fun onStop() {
        if(::session.isInitialized) session.finish()
        super.onStop()
    }

    override fun onConfigurationChanged(newConfiguration: Configuration) {
        super.onConfigurationChanged(newConfiguration)
        val currentItem = pager.currentItem
        pager.adapter = pager.adapter
        pager.setCurrentItem(currentItem, false)
    }
}