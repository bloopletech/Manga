package net.bloople.manga

import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.bumptech.glide.load.model.GlideUrl
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader.PreloadModelProvider
import android.widget.TextView
import android.content.Intent
import android.view.LayoutInflater
import android.widget.ImageButton
import net.bloople.manga.audit.AuditEventsActivity
import android.widget.PopupWindow
import android.view.ViewGroup
import android.view.Gravity
import com.bumptech.glide.RequestBuilder
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import java.util.ArrayList

internal class BooksAdapter(requestManager: RequestManager, preloadSizeProvider: ViewPreloadSizeProvider<GlideUrl>) :
    RecyclerView.Adapter<BooksAdapter.ViewHolder>(), PreloadModelProvider<GlideUrl> {
    private val requestManager: RequestManager
    private val preloadSizeProvider: ViewPreloadSizeProvider<GlideUrl>
    private var books = ArrayList<Book>()
    private var booksMetadata = HashMap<Long, BookMetadata>();

    init {
        setHasStableIds(true)
        this.requestManager = requestManager
        this.preloadSizeProvider = preloadSizeProvider
    }

    override fun getItemId(position: Int): Long {
        return books[position].id
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return books.size
    }

    fun update(searchResults: SearchResults) {
        books = searchResults.books
        booksMetadata = searchResults.booksMetadata
        notifyDataSetChanged()
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var pageCountView: TextView = view.findViewById(R.id.page_count_view)
        var openedCountView: TextView = view.findViewById(R.id.opened_count_view)
        var textView: TextView = view.findViewById(R.id.text_view)
        var imageView: ImageView = view.findViewById(R.id.image_view)

        init {
            view.setOnClickListener { v: View ->
                val book = books[bindingAdapterPosition]
                openBook(book, true)
            }

            view.setOnLongClickListener {
                val book = books[bindingAdapterPosition]
                openBook(book, false)

                true
            }

            textView.setOnClickListener {
                val book = books[bindingAdapterPosition]
                showFullBookTitle(book)
            }

            textView.setOnLongClickListener {
                val book = books[bindingAdapterPosition]
                showBookTags(book)

                true
            }
        }

        private fun openBook(book: Book, resume: Boolean) {
            val intent = Intent(itemView.context, ReadingActivity::class.java)
            intent.putExtra("_id", book.id)
            intent.putExtra("resume", resume)
            intent.putExtra("libraryId", book.library.id)

            itemView.context.startActivity(intent)
        }

        private fun showFullBookTitle(book: Book) {
            val popupView = LayoutInflater.from(itemView.context).inflate(R.layout.index_book_title_popup, null, false)
            val bookTitleView: TextView = popupView.findViewById(R.id.book_title)
            bookTitleView.text = book.title

            val viewAuditEventsButton: ImageButton = popupView.findViewById(R.id.view_audit_events)
            viewAuditEventsButton.setOnClickListener {
                val intent = Intent(viewAuditEventsButton.context, AuditEventsActivity::class.java)
                intent.putExtra("resourceId", book.id)

                viewAuditEventsButton.context.startActivity(intent)
            }

            val popupWidth = textView.width + bookTitleView.paddingStart + bookTitleView.paddingEnd
            val popup = PopupWindow(popupView, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            popup.isFocusable = true
            popup.isOutsideTouchable = true
            popup.elevation = 24f

            popupView.setOnClickListener { popup.dismiss() }

            popup.showAsDropDown(textView, -bookTitleView.paddingStart, -textView.height, Gravity.TOP or Gravity.START)
        }

        private fun showBookTags(book: Book) {
            val indexActivity = itemView.context as IndexActivity

            val tagChooser = TagChooserFragment.newInstance(book.tags.toTypedArray())
            tagChooser.show(indexActivity.supportFragmentManager, "tag_chooser")
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.index_book_view, parent, false)

        val viewWidthToBitmapWidthRatio = parent.width.toDouble() / 4.0 / 197.0
        view.layoutParams.height = (310.0 * viewWidthToBitmapWidthRatio).toInt()

        preloadSizeProvider.setView(view)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        val metadata = booksMetadata[book.id]

        //holder.textView.setText(title.substring(0, Math.min(50, title.length())));
        holder.textView.text = book.title

        holder.pageCountView.text = String.format("%,d", book.pages)

        val openedCount = metadata?.openedCount ?: 0
        if(openedCount > 0) {
            holder.openedCountView.visibility = View.VISIBLE
            holder.openedCountView.text = String.format("%,d", openedCount)
        }
        else {
            holder.openedCountView.visibility = View.GONE
        }

        requestManager
            .load(book.thumbnailUrl.toGlideUrl())
            .into(holder.imageView)
    }

    override fun getPreloadItems(position: Int): List<GlideUrl> {
        val book = books[position]
        return listOf(book.thumbnailUrl.toGlideUrl())
    }

    override fun getPreloadRequestBuilder(url: GlideUrl): RequestBuilder<Drawable> {
        return requestManager.load(url)
    }
}