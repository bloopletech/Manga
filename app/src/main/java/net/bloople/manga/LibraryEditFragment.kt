package net.bloople.manga

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import net.bloople.manga.Library.Companion.findById
import android.widget.EditText
import android.os.Bundle
import android.content.DialogInterface
import androidx.fragment.app.DialogFragment

class LibraryEditFragment : DialogFragment() {
    private var listener: OnLibraryEditFinishedListener? = null
    private var libraryId: Long = 0
    private lateinit var nameView: EditText
    private lateinit var rootView: EditText
    private lateinit var usernameView: EditText
    private lateinit var passwordView: EditText

    internal interface OnLibraryEditFinishedListener {
        fun onLibraryEditFinished(library: Library?)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Edit Library")

        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.library_edit_fragment, null)
        builder.setView(view)

        nameView = view.findViewById(R.id.name)
        rootView = view.findViewById(R.id.root)
        usernameView = view.findViewById(R.id.username)
        passwordView = view.findViewById(R.id.password)

        val library = findById(libraryId)
        nameView.setText(library!!.name)
        rootView.setText(library.root)
        usernameView.setText(library.username)
        passwordView.setText(library.password)

        builder.setPositiveButton("Save") { _: DialogInterface?, _: Int -> update() }
        builder.setNegativeButton("Cancel") { _: DialogInterface?, _: Int -> cancel() }
        builder.setNeutralButton("Delete") { _: DialogInterface?, _: Int -> destroy() }

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = parentFragment as OnLibraryEditFinishedListener?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryId = requireArguments().getLong("libraryId", -1)
    }

    private fun cancel() {
        listener!!.onLibraryEditFinished(null)
    }

    private fun update() {
        val library = findById(libraryId)
        library!!.name = nameView.text.toString()
        library.root = rootView.text.toString()
        library.username = usernameView.text.toString()
        library.password = passwordView.text.toString()
        library.save()
        listener!!.onLibraryEditFinished(library)
    }

    private fun destroy() {
        val library = findById(libraryId)
        library!!.destroy()
        listener!!.onLibraryEditFinished(library)
    }

    companion object {
        fun newInstance(libraryId: Long): LibraryEditFragment {
            val fragment = LibraryEditFragment()
            val args = Bundle()
            args.putLong("libraryId", libraryId)
            fragment.arguments = args
            return fragment
        }
    }
}