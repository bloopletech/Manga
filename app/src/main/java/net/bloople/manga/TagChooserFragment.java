package net.bloople.manga;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class TagChooserFragment extends DialogFragment {
    static TagChooserFragment newInstance(String[] tags) {
        TagChooserFragment fragment = new TagChooserFragment();
        Bundle args = new Bundle();
        args.putStringArray("tags", tags);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        final String[] tags = arguments.getStringArray("tags");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Search for tag")
                .setItems(tags, (dialog, which) -> {
                    IndexActivity activity = (IndexActivity)getActivity();
                    activity.useTag(tags[which]);
                });
        return builder.create();
    }
}
