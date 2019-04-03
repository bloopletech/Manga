package net.bloople.manga;

import android.content.Context;
import android.database.Cursor;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.TextView;


class LibrariesAdapter extends CursorAdapter {
    private LibrariesManager librariesManager;

    LibrariesAdapter(Context context, Cursor cursor, LibrariesManager librariesManager) {
        super(context, cursor, 0);
        this.librariesManager = librariesManager;

    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.sidebar_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        final TextView nameView = (TextView)view.findViewById(R.id.name);
        final EditText editNameView = (EditText)view.findViewById(R.id.edit_name);

        final long libraryId = cursor.getLong(cursor.getColumnIndex("_id"));
        final String root = cursor.getString(cursor.getColumnIndex("root"));
        nameView.setText(root);

        nameView.setVisibility(View.VISIBLE);
        editNameView.setVisibility(View.GONE);

        editNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId != EditorInfo.IME_ACTION_DONE) return false;

                InputMethodManager in = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(editNameView.getWindowToken(), 0);
                editNameView.clearFocus();

                Library library = Library.findById(context, libraryId);
                library.root(editNameView.getText().toString());
                library.save(context);

                nameView.setText(editNameView.getText().toString());
                nameView.setVisibility(View.VISIBLE);
                editNameView.setVisibility(View.GONE);
                librariesManager.onDoneEditing();

                return true;
            }
        });

        editNameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    int clickIndex = editNameView.getRight() -
                            editNameView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();

                    if(event.getX() < clickIndex) return false;

                    InputMethodManager in = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editNameView.getWindowToken(), 0);
                    editNameView.clearFocus();

                    nameView.setVisibility(View.VISIBLE);
                    editNameView.setVisibility(View.GONE);
                    librariesManager.onDoneEditing();

                    return true;
                }

                return false;
            }
        });
    }

}
