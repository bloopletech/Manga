package net.bloople.manga;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LibraryRootEditFragment extends DialogFragment {
    private Context context;
    private OnLibraryRootEditFinishedListener listener;
    private long libraryRootId;
    private EditText nameView;
    private EditText rootView;

    public interface OnLibraryRootEditFinishedListener {
        void onLibraryRootEditFinished(LibraryRoot libraryRoot);
    }

    public static LibraryRootEditFragment newInstance(long libraryRootId) {
        LibraryRootEditFragment fragment = new LibraryRootEditFragment();
        Bundle args = new Bundle();
        args.putLong("libraryRootId", libraryRootId);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.library_root_edit_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameView = view.findViewById(R.id.name);
        rootView = view.findViewById(R.id.root);

        LibraryRoot libraryRoot = LibraryRoot.findById(context, libraryRootId);
        nameView.setText(libraryRoot.name());
        rootView.setText(libraryRoot.root());

        Button cancelButton = view.findViewById(R.id.cancel);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        Button saveButton = view.findViewById(R.id.save);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        Button destroyButton = view.findViewById(R.id.destroy);

        destroyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroy();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context = null;
    }

    private void cancel() {
        listener.onLibraryRootEditFinished(null);
        dismiss();
    }

    private void update() {
        LibraryRoot libraryRoot = LibraryRoot.findById(context, libraryRootId);
        libraryRoot.name(nameView.getText().toString());
        libraryRoot.root(rootView.getText().toString());
        libraryRoot.save(context);
        listener.onLibraryRootEditFinished(libraryRoot);
        dismiss();
    }

    private void destroy() {
        LibraryRoot libraryRoot = LibraryRoot.findById(context, libraryRootId);
        libraryRoot.destroy(context);
        listener.onLibraryRootEditFinished(libraryRoot);
        dismiss();
    }
}
