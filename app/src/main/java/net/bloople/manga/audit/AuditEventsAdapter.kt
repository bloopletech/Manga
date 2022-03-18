package net.bloople.manga.audit

import android.app.AppComponentFactory
import net.bloople.manga.CursorRecyclerAdapter
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.ImageButton
import android.content.Intent
import android.database.Cursor
import net.bloople.manga.ReadingActivity
import net.bloople.manga.IndexActivity
import android.view.LayoutInflater
import net.bloople.manga.R
import android.widget.PopupWindow
import android.view.ViewGroup
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import net.bloople.manga.LibraryService
import net.bloople.manga.Library
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*

internal class AuditEventsAdapter(cursor: Cursor?) : CursorRecyclerAdapter<AuditEventsAdapter.ViewHolder>(cursor) {
    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var event: AuditEvent? = null
        var whenView: TextView = view.findViewById(R.id.`when`)
        var actionView: TextView = view.findViewById(R.id.action)
        var imageView: ImageView = view.findViewById(R.id.image_view)
        var openResourceView: ImageButton = view.findViewById(R.id.open_resource)
        var resourceNameView: TextView = view.findViewById(R.id.resource_name)
        var detailView: TextView = view.findViewById(R.id.detail)

        init {
            imageView.setOnClickListener { openResource(event) }
            openResourceView.setOnClickListener { openResource(event) }
            resourceNameView.setOnClickListener {
                showFullResourceName(
                    event!!.resourceName
                )
            }
        }

        private fun openResource(event: AuditEvent?) {
            if(event!!.resourceType == ResourceType.BOOK && event.resourceContextType == ResourceType.LIBRARY) {
                openBook(event.resourceContextId, event.resourceId)
            }
            else if(event.resourceType == ResourceType.LIBRARY) {
                openLibrary(event.resourceId)
            }
        }

        private fun openBook(libraryId: Long, bookId: Long) {
            val intent = Intent(openResourceView.context, ReadingActivity::class.java)
            intent.putExtra("_id", bookId)
            intent.putExtra("resume", true)
            intent.putExtra("libraryId", libraryId)
            openResourceView.context.startActivity(intent)
        }

        private fun openLibrary(libraryId: Long) {
            val intent = Intent(openResourceView.context, IndexActivity::class.java)
            intent.putExtra("libraryId", libraryId)
            openResourceView.context.startActivity(intent)
        }

        private fun showFullResourceName(resourceName: String) {
            val popupView = LayoutInflater.from(resourceNameView.context).inflate(
                R.layout.audit_audit_event_resource_name_popup,
                null,
                false
            )

            val resourceNamePopupView = popupView.findViewById<TextView>(R.id.resource_name)
            resourceNamePopupView.text = resourceName
            val popupWidth =
                resourceNameView.width + resourceNamePopupView.paddingStart + resourceNamePopupView.paddingEnd
            val popup = PopupWindow(popupView, popupWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

            popup.isFocusable = true
            popup.isOutsideTouchable = true
            popup.elevation = 24f
            popupView.setOnClickListener { popup.dismiss() }
            popup.showAsDropDown(
                resourceNameView,
                -resourceNamePopupView.paddingStart,
                -resourceNameView.height,
                Gravity.TOP or Gravity.START
            )
        }
    }

    private val DATE_FORMAT = SimpleDateFormat(
        "d MMM yyyy h:mm a",
        Locale.getDefault()
    )

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.audit_audit_event,
            parent,
            false
        )
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, cursor: Cursor) {
        val event = AuditEvent(cursor)
        holder.event = event

        val age = DATE_FORMAT.format(Date(event.`when`))
        holder.whenView.text = age
        holder.actionView.text = event.action.toString()
        holder.resourceNameView.text = event.resourceName
        holder.detailView.text = event.detail

        if(event.resourceType == ResourceType.BOOK && event.resourceContextType == ResourceType.LIBRARY) {
            renderBook(holder, event);
            return
        }

        holder.openResourceView.visibility = View.VISIBLE
        holder.imageView.visibility = View.GONE

        Glide.with(holder.imageView.context).clear(holder.imageView)
    }

    private fun renderBook(holder: ViewHolder, event: AuditEvent) {
        val context = holder.imageView.context as AppCompatActivity
        context.lifecycleScope.launch {
            val library = LibraryService.ensureLibrary(context, event.resourceContextId)!!

            holder.openResourceView.visibility = View.GONE
            holder.imageView.visibility = View.VISIBLE

            val viewWidthToBitmapWidthRatio = holder.imageView.layoutParams.width.toDouble() / 197.0
            holder.imageView.layoutParams.height = (310.0 * viewWidthToBitmapWidthRatio).toInt()

            val glide = Glide.with(holder.imageView.context)

            val book = library.books[event.resourceId]

            if(book != null) glide.load(book.thumbnailUrl.toGlideUrl()).into(holder.imageView)
            else glide.clear(holder.imageView)
        }
    }
}