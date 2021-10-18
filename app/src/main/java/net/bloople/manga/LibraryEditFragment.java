package net.bloople.manga;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class LibraryEditFragment extends DialogFragment {
    private Context context;
    private OnLibraryEditFinishedListener listener;
    private long libraryId;
    private EditText nameView;
    private EditText rootView;
    private EditText usernameView;
    private EditText passwordView;

    interface OnLibraryEditFinishedListener {
        void onLibraryEditFinished(Library library);
    }

    static LibraryEditFragment newInstance(long libraryId) {
        LibraryEditFragment fragment = new LibraryEditFragment();
        Bundle args = new Bundle();
        args.putLong("libraryId", libraryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Library");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.library_edit_fragment, null);
        builder.setView(view);

        nameView = view.findViewById(R.id.name);
        rootView = view.findViewById(R.id.root);
        usernameView = view.findViewById(R.id.username);
        passwordView = view.findViewById(R.id.password);

        Library library = Library.findById(context, libraryId);
        nameView.setText(library.getName());
        rootView.setText(library.getRoot());
        usernameView.setText(library.getUsername());
        passwordView.setText(library.getPassword());

        builder.setPositiveButton("Save", (dialog, which) -> update());
        builder.setNegativeButton("Cancel", (dialog, which) -> cancel());
        builder.setNeutralButton("Delete", (dialog, which) -> destroy());

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.listener = (OnLibraryEditFinishedListener)getParentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libraryId = getArguments().getLong("libraryId", -1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    private void cancel() {
        listener.onLibraryEditFinished(null);
    }

    private void update() {
        Library library = Library.findById(context, libraryId);
        library.setName(nameView.getText().toString());
        library.setRoot(rootView.getText().toString());
        library.setUsername(usernameView.getText().toString());
        library.setPassword(passwordView.getText().toString());
        library.save(context);
        listener.onLibraryEditFinished(library);
    }

    private void destroy() {
        Library library = Library.findById(context, libraryId);
        library.destroy(context);
        listener.onLibraryEditFinished(library);
    }
}
