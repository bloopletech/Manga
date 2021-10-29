package net.bloople.manga

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.content.DialogInterface
import androidx.fragment.app.DialogFragment

class TagChooserFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val arguments = arguments
        val tags = arguments!!.getStringArray("tags")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Search for tag")
            .setItems(tags) { _: DialogInterface?, which: Int ->
                val activity = activity as IndexActivity?
                activity!!.useTag(tags!![which])
            }
        return builder.create()
    }

    companion object {
        @JvmStatic
        fun newInstance(tags: Array<String>): TagChooserFragment {
            val fragment = TagChooserFragment()
            val args = Bundle()
            args.putStringArray("tags", tags)
            fragment.arguments = args
            return fragment
        }
    }
}