package net.bloople.manga

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.bloople.manga.audit.AuditEventsActivity
import java.util.ArrayList

internal class BooksAdapter : RecyclerView.Adapter<BooksAdapter.ViewHolder>() {
    private var books = ArrayList<Book>()
    private var selectedBookIds = ArrayList<Long>()
    private var selectable = false

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return books[position].id
    }

    fun isSelectable(): Boolean {
        return selectable
    }

    fun setSelectable(isSelectable: Boolean) {
        selectable = isSelectable
        notifyDataSetChanged()
    }

    fun getSelectedBookIds(): ArrayList<Long> {
        return selectedBookIds
    }

    fun setSelectedBookIds(selectedBookIds: ArrayList<Long>) {
        this.selectedBookIds = selectedBookIds
        notifyDataSetChanged()
    }

    fun clearSelectedBookIds() {
        setSelectedBookIds(ArrayList())
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return books.size
    }

    fun update(inBooks: ArrayList<Book>) {
        books = inBooks
        notifyDataSetChanged()
    }

    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pageCountView: TextView = view.findViewById(R.id.page_count_view)
        val textView: TextView = view.findViewById(R.id.text_view)
        val imageView: ImageView = view.findViewById(R.id.image_view)
        val selectableView: ImageView = view.findViewById(R.id.selectable)

        init {
            view.setOnClickListener { v: View ->
                val book = books[bindingAdapterPosition]
                val bookId = book.id

                if(selectable) {
                    if(selectedBookIds.contains(bookId)) {
                        selectedBookIds.remove(bookId)
                        v.isActivated = false
                    }
                    else {
                        selectedBookIds.add(bookId)
                        v.isActivated = true
                    }
                    notifyItemChanged(bindingAdapterPosition)
                }
                else {
                    openBook(book, true)
                }
            }

            view.setOnLongClickListener {
                if(selectable) return@setOnLongClickListener false
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
            val metadata = BookMetadata.findOrCreateByBookId(itemView.context, book.id)
            val popupView: View =
                LayoutInflater.from(itemView.context).inflate(R.layout.index_book_title_popup, null, false)
            val bookTitleView: TextView = popupView.findViewById(R.id.book_title)

            bookTitleView.text = """
            ${book.title}
            Opened Count: ${metadata.openedCount()}
            """.trimIndent()

            val viewAuditEventsButton: ImageButton = popupView.findViewById(R.id.view_audit_events)
            viewAuditEventsButton.setOnClickListener {
                val intent = Intent(viewAuditEventsButton.context, AuditEventsActivity::class.java)
                intent.putExtra("resourceId", book.id)
                viewAuditEventsButton.context.startActivity(intent)
            }

            val popupWidth: Int = textView.width + bookTitleView.paddingStart + bookTitleView.paddingEnd
            val popup = PopupWindow(popupView, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
            popup.isFocusable = true
            popup.isOutsideTouchable = true
            popup.elevation = 24f
            popupView.setOnClickListener { popup.dismiss() }
            popup.showAsDropDown(
                textView,
                -bookTitleView.paddingStart,
                -textView.height,
                Gravity.TOP or Gravity.START
            )
        }

        private fun showBookTags(book: Book) {
            val indexActivity = itemView.context as IndexActivity
            val tagChooser = TagChooserFragment.newInstance(book.tags.toTypedArray())
            tagChooser.show(indexActivity.supportFragmentManager, "tag_chooser")
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.index_book_view, parent, false)
        val viewWidthToBitmapWidthRatio = parent.width.toDouble() / 4.0 / 197.0
        view.layoutParams.height = (310.0 * viewWidthToBitmapWidthRatio).toInt()
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]

        holder.selectableView.visibility = if(selectable) View.VISIBLE else View.INVISIBLE
        if(selectable) holder.itemView.isActivated = selectedBookIds.contains(book.id)

        //holder.textView.setText(title.substring(0, Math.min(50, title.length())));
        holder.textView.text = book.title
        holder.pageCountView.text = String.format("%,d", book.pages)

        book.thumbnailUrl.load(holder.imageView)
    }
}
