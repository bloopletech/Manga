package net.bloople.manga;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseManagementFragment extends Fragment {
    private static final int REQUEST_CODE_EXPORT = 0;
    private static final int REQUEST_CODE_EXPORT_AUDIT = 1;
    private static final int REQUEST_CODE_IMPORT = 2;
    private static final int REQUEST_CODE_IMPORT_AUDIT = 3;

    private Context context;

    private ImageButton importDatabaseButton;
    private ImageButton exportDatabaseButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.database_management_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exportDatabaseButton = view.findViewById(R.id.export_database);
        exportDatabaseButton.setOnClickListener(v -> {
            startExport();
        });

        importDatabaseButton = view.findViewById(R.id.import_database);
        importDatabaseButton.setOnClickListener(v -> {
            startImport();
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_EXPORT && resultCode == Activity.RESULT_OK) completeExport(data);
        else if(requestCode == REQUEST_CODE_EXPORT_AUDIT && resultCode == Activity.RESULT_OK) completeExportAudit(data);
        else if(requestCode == REQUEST_CODE_IMPORT && resultCode == Activity.RESULT_OK) completeImport(data);
        else if(requestCode == REQUEST_CODE_IMPORT_AUDIT && resultCode == Activity.RESULT_OK) completeImportAudit(data);
    }

    private void startExport() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.sqlite3");
        intent.putExtra(Intent.EXTRA_TITLE, "Manga.db");
        startActivityForResult(intent, REQUEST_CODE_EXPORT);
    }

    private void completeExport(Intent data) {
        Uri uri = data.getData();

        try {
            OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
            DatabaseHelper.exportDatabase(context, outputStream);
            Toast.makeText(context, "Database exported successfully", Toast.LENGTH_LONG).show();
            startExportAudit();
        }
        catch(IOException e) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
        }
    }

    private void startExportAudit() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.sqlite3");
        intent.putExtra(Intent.EXTRA_TITLE, "MangaAudit.db");
        startActivityForResult(intent, REQUEST_CODE_EXPORT_AUDIT);
    }

    private void completeExportAudit(Intent data) {
        Uri uri = data.getData();

        try {
            OutputStream outputStream = getContext().getContentResolver().openOutputStream(uri);
            net.bloople.manga.audit.DatabaseHelper.exportDatabase(context, outputStream);
            Toast.makeText(context, "Audit Database exported successfully", Toast.LENGTH_LONG).show();
        }
        catch(IOException e) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
        }
    }

    private void startImport() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_IMPORT);
    }

    private void completeImport(Intent data) {
        Uri uri = data.getData();

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            DatabaseHelper.importDatabase(context, inputStream);
            Toast.makeText(context, "Database imported successfully", Toast.LENGTH_LONG).show();
            startImportAudit();
        }
        catch(IOException e) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
        }
    }

    private void startImportAudit() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_IMPORT_AUDIT);
    }

    private void completeImportAudit(Intent data) {
        Uri uri = data.getData();

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            net.bloople.manga.audit.DatabaseHelper.importDatabase(context, inputStream);
            Toast.makeText(context, "Audit Database imported successfully", Toast.LENGTH_LONG).show();

            Activity activity = getActivity();
            if(activity != null) activity.recreate();
        }
        catch(IOException e) {
            Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
        }
    }
}