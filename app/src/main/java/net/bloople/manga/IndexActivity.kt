package net.bloople.manga

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import net.bloople.manga.LibrariesFragment.OnLibrarySelectedListener
import net.bloople.manga.audit.LibrariesAuditor
import androidx.recyclerview.widget.GridLayoutManager
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import android.view.inputmethod.EditorInfo
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import android.content.DialogInterface
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.launch

class IndexActivity : AppCompatActivity(), OnLibrarySelectedListener {
    private lateinit var model: IndexViewModel
    private lateinit var librariesFragment: LibrariesFragment
    private lateinit var auditor: LibrariesAuditor
    private lateinit var adapter: BooksAdapter
    private lateinit var booksLayoutManager: GridLayoutManager
    private lateinit var searchField: AutoCompleteTextView
    private lateinit var searchResultsToolbar: TextView

    private lateinit var queryService: QueryService

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_index)

        model = ViewModelProvider(this)[IndexViewModel::class.java]

        librariesFragment = supportFragmentManager.findFragmentById(R.id.libraries_fragment) as LibrariesFragment

        auditor = LibrariesAuditor(applicationContext)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        searchResultsToolbar = findViewById(R.id.search_results_toolbar)

        model.sorterDescription.observe(this) { description: String? -> searchResultsToolbar.text = description }

        searchField = findViewById(R.id.search_field)

        searchField.setOnEditorActionListener { _: TextView?, actionId: Int, event: KeyEvent? ->
            var handled = false
            if(actionId == EditorInfo.IME_ACTION_SEARCH || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchField.windowToken, 0)
                searchField.clearFocus()

                onSearch(searchField.text.toString())

                handled = true
            }
            handled
        }

        searchField.setOnTouchListener { _: View?, event: MotionEvent ->
            val DRAWABLE_RIGHT = 2

            if(event.action == MotionEvent.ACTION_UP) {
                val clickIndex = searchField.right -
                    searchField.compoundDrawables[DRAWABLE_RIGHT].bounds.width()

                if(event.rawX >= clickIndex) {
                    searchField.setText("")
                    onSearch("")

                    return@setOnTouchListener true
                }
            }
            false
        }

        val booksView = findViewById<RecyclerView>(R.id.books_view)
        booksLayoutManager = GridLayoutManager(this, 4)
        booksView.layoutManager = booksLayoutManager

        val requestManager = Glide.with(this).applyDefaultRequestOptions(
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
        val sizeProvider = ViewPreloadSizeProvider<GlideUrl>()

        adapter = BooksAdapter(requestManager, sizeProvider)
        booksView.adapter = adapter

        val preloader = RecyclerViewPreloader(
            requestManager, adapter, sizeProvider, 12
        )

        booksView.addOnScrollListener(preloader)

        val collections = CollectionsManager(this, adapter)
        collections.setup()

        queryService = QueryService(this, searchField)

        model.searchResults.observe(this) { searchResults: SearchResults ->
            adapter.update(searchResults)
        }

        val intent = intent
        val intentLibraryId = intent.getLongExtra("libraryId", -1)

        if(intentLibraryId != -1L) loadLibrary(intentLibraryId)
        else if(savedInstanceState == null) loadLibrary(-1L)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val sortMethod = savedInstanceState.getString("sortMethod")
        val sortDirectionAsc = savedInstanceState.getBoolean("sortDirectionAsc")
        if(sortMethod != null) model.setSort(BooksSortMethod.valueOf(sortMethod), sortDirectionAsc)
        loadLibrary(savedInstanceState.getLong("libraryId"))
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        val library = model.getLibrary()
        if(library != null) savedInstanceState.putLong("libraryId", library.id)
        savedInstanceState.putString("sortMethod", model.sortMethod.toString())
        savedInstanceState.putBoolean("sortDirectionAsc", model.sortDirectionAsc)
        super.onSaveInstanceState(savedInstanceState)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _: DialogInterface?, _: Int -> finish() }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        val inflater = menuInflater
        inflater.inflate(R.menu.list_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        val sortMethod = model.sortMethod
        var newSortMethod = sortMethod

        when(menuItem.itemId) {
            R.id.sort_alphabetic -> {
                newSortMethod = BooksSortMethod.SORT_ALPHABETIC
            }
            R.id.sort_age -> {
                newSortMethod = BooksSortMethod.SORT_AGE
            }
            R.id.sort_size -> {
                newSortMethod = BooksSortMethod.SORT_LENGTH
            }
            R.id.sort_last_opened -> {
                newSortMethod = BooksSortMethod.SORT_LAST_OPENED
            }
            R.id.sort_opened_count -> {
                newSortMethod = BooksSortMethod.SORT_OPENED_COUNT
            }
            R.id.sort_random -> {
                newSortMethod = BooksSortMethod.SORT_RANDOM
            }
        }

        var sortDirectionAsc = model.sortDirectionAsc
        if(sortMethod == newSortMethod) sortDirectionAsc = !sortDirectionAsc
        model.setSort(newSortMethod, sortDirectionAsc)
        scrollToTop()

        return true
    }

    private fun loadLibrary(libraryId: Long) {
        lifecycleScope.launch {
            val library = LibraryService.ensureLibrary(this@IndexActivity, libraryId) ?: return@launch

            librariesFragment.setCurrentLibraryId(library.id)
            auditor.selected(library)
            model.setLibrary(library)
        }
    }

    private fun scrollToTop() {
        booksLayoutManager.scrollToPositionWithOffset(0, 0)
    }

    private fun onSearch(text: String) {
        model.setSearchText(text)
        scrollToTop()
        queryService.onSearch(text)
    }

    fun useList(list: BookList?) {
        model.useList(list)
        scrollToTop()
    }

    override fun onLibrarySelected(libraryId: Long) {
        loadLibrary(libraryId)
        scrollToTop()
    }

    fun useTag(tag: String) {
        val text = "\"" + tag + "\""
        searchField.setText(text)
        onSearch(text)
    }
}