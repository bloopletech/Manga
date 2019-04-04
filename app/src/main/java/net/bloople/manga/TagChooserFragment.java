package net.bloople.manga;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class TagChooserFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        long bookId = arguments.getLong("_id");
        final Book book = LibraryService.current.books().get(bookId);

		final String[] tags = book.tags().toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Search for tag")
                .setItems(tags, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        IndexActivity activity = (IndexActivity)getActivity();
                        activity.useTag(tags[which]);
                    }
                });
        return builder.create();
    }
}
