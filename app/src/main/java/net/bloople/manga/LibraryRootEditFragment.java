package net.bloople.manga;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class LibraryRootEditFragment extends DialogFragment {
    private Context context;
    private OnLibraryRootEditFinishedListener listener;
    private long libraryRootId;
    private EditText nameView;
    private EditText rootView;

    interface OnLibraryRootEditFinishedListener {
        void onLibraryRootEditFinished(LibraryRoot libraryRoot);
    }

    static LibraryRootEditFragment newInstance(long libraryRootId) {
        LibraryRootEditFragment fragment = new LibraryRootEditFragment();
        Bundle args = new Bundle();
        args.putLong("libraryRootId", libraryRootId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Library");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.library_root_edit_fragment, null);
        builder.setView(view);

        nameView = view.findViewById(R.id.name);
        rootView = view.findViewById(R.id.root);

        LibraryRoot libraryRoot = LibraryRoot.findById(context, libraryRootId);
        nameView.setText(libraryRoot.name());
        rootView.setText(libraryRoot.root());

        builder.setPositiveButton("Save", (dialog, which) -> update());
        builder.setNegativeButton("Cancel", (dialog, which) -> cancel());
        builder.setNeutralButton("Delete", (dialog, which) -> destroy());

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.listener = (OnLibraryRootEditFinishedListener)getParentFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        libraryRootId = getArguments().getLong("libraryRootId", -1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    private void cancel() {
        listener.onLibraryRootEditFinished(null);
    }

    private void update() {
        LibraryRoot libraryRoot = LibraryRoot.findById(context, libraryRootId);
        libraryRoot.name(nameView.getText().toString());
        libraryRoot.root(rootView.getText().toString());
        libraryRoot.save(context);
        listener.onLibraryRootEditFinished(libraryRoot);
    }

    private void destroy() {
        LibraryRoot libraryRoot = LibraryRoot.findById(context, libraryRootId);
        libraryRoot.destroy(context);
        listener.onLibraryRootEditFinished(libraryRoot);
    }
}
