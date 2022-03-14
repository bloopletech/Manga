package net.bloople.manga

import net.bloople.manga.ThrottledOnClickListener.Companion.wrap
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import android.os.Bundle
import android.content.res.Configuration
import android.view.View
import android.widget.ImageView

class ReadingActivity : AppCompatActivity() {
    private lateinit var session: ReadingSession
    private lateinit var pager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reading)

        pager = findViewById(R.id.pager)

        val prev10 = findViewById<ImageView>(R.id.prev_10)
        prev10.setOnClickListener(wrap { session.go(-10) })

        val next10 = findViewById<ImageView>(R.id.next_10)
        next10.setOnClickListener(wrap { session.go(10) })

        val intent = intent

        val libraryId = intent.getLongExtra("libraryId", -1)

        LibraryService.ensureLibrary(this, libraryId) { library: Library? ->
            if(library == null) return@ensureLibrary

            val bookId = intent.getLongExtra("_id", -1)
            val book = library.books[bookId] ?: return@ensureLibrary

            session = ReadingSession(applicationContext, library, book)
            session.bind(this, pager)

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
        session.start()
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        // Enables "sticky immersive" mode
        val decorView = window.decorView
        decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}