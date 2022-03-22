package net.bloople.manga

import android.widget.ImageButton
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Intent
import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.io.IOException

class DatabaseManagementFragment : Fragment() {
    private lateinit var importDatabaseButton: ImageButton
    private lateinit var exportDatabaseButton: ImageButton

    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.database_management_fragment, parent, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exportDatabaseButton = view.findViewById(R.id.export_database)
        exportDatabaseButton.setOnClickListener { startExport() }

        importDatabaseButton = view.findViewById(R.id.import_database)
        importDatabaseButton.setOnClickListener { startImport() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE_EXPORT && resultCode == Activity.RESULT_OK) completeExport(data)
        else if(requestCode == REQUEST_CODE_EXPORT_AUDIT && resultCode == Activity.RESULT_OK) completeExportAudit(
            data
        )
        else if(requestCode == REQUEST_CODE_IMPORT && resultCode == Activity.RESULT_OK) completeImport(data)
        else if(requestCode == REQUEST_CODE_IMPORT_AUDIT && resultCode == Activity.RESULT_OK) completeImportAudit(
            data
        )
    }

    private fun startExport() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/vnd.sqlite3"
        intent.putExtra(Intent.EXTRA_TITLE, "Manga.db")
        startActivityForResult(intent, REQUEST_CODE_EXPORT)
    }

    private fun completeExport(data: Intent?) {
        val uri = data!!.data

        try {
            val outputStream = requireContext().contentResolver.openOutputStream(uri!!)
            DatabaseHelper.exportDatabase(context, outputStream)
            Toast.makeText(context, "Database exported successfully", Toast.LENGTH_LONG).show()
            startExportAudit()
        }
        catch(e: IOException) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun startExportAudit() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/vnd.sqlite3"
        intent.putExtra(Intent.EXTRA_TITLE, "MangaAudit.db")
        startActivityForResult(intent, REQUEST_CODE_EXPORT_AUDIT)
    }

    private fun completeExportAudit(data: Intent?) {
        val uri = data!!.data

        try {
            val outputStream = requireContext().contentResolver.openOutputStream(uri!!)
            net.bloople.manga.audit.DatabaseHelper.exportDatabase(context, outputStream)
            Toast.makeText(context, "Audit Database exported successfully", Toast.LENGTH_LONG).show()
        }
        catch(e: IOException) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun startImport() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_CODE_IMPORT)
    }

    private fun completeImport(data: Intent?) {
        val uri = data!!.data

        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri!!)
            DatabaseHelper.importDatabase(context, inputStream)
            Toast.makeText(context, "Database imported successfully", Toast.LENGTH_LONG).show()
            startImportAudit()
        }
        catch(e: IOException) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
    }

    private fun startImportAudit() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, REQUEST_CODE_IMPORT_AUDIT)
    }

    private fun completeImportAudit(data: Intent?) {
        val uri = data!!.data

        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri!!)
            net.bloople.manga.audit.DatabaseHelper.importDatabase(context, inputStream)
            Toast.makeText(context, "Audit Database imported successfully", Toast.LENGTH_LONG).show()

            val activity: Activity? = activity
            activity?.recreate()
        }
        catch(e: IOException) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val REQUEST_CODE_EXPORT = 0
        private const val REQUEST_CODE_EXPORT_AUDIT = 1
        private const val REQUEST_CODE_IMPORT = 2
        private const val REQUEST_CODE_IMPORT_AUDIT = 3
    }
}