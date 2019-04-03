package net.bloople.manga;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

public class TagChooserFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        long bookId = arguments.getLong("_id");
        final Book book = Mango.current.books().get(bookId);

        final ArrayList<String> tagStrings = new ArrayList<>();
        for(Tag tag : book.tags()) tagStrings.add(tag.tag());

        final String[] tagStringsArray = tagStrings.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Search for tag")
                .setItems(tagStringsArray, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        IndexActivity activity = (IndexActivity)getActivity();
                        activity.useTag(book.tags().get(which));
                    }
                });
        return builder.create();
    }
}
