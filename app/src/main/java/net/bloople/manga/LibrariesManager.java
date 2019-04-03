package net.bloople.manga;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

class LibrariesManager {
    private IndexActivity activity;
    private LibrariesAdapter librariesAdapter;
    private ImageButton newLibrary;
    private ImageButton destroyLibrary;
    private ListView librariesSidebar;
    private Library library;

    LibrariesManager(IndexActivity activity) {
        this.activity = activity;
    }

    void setup() {
        librariesSidebar = (ListView)activity.findViewById(R.id.libraries_sidebar);
        librariesAdapter = new LibrariesAdapter(activity, null, this);
        librariesSidebar.setAdapter(librariesAdapter);
        updateCursor();

        librariesSidebar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                library = Library.findById(activity, parent.getItemIdAtPosition(position));

                activity.useRoot(library.root());

                view.setActivated(true);

                destroyLibrary.setVisibility(View.VISIBLE);
            }
        });

        librariesSidebar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView nameView = (TextView)view.findViewById(R.id.name);
                final EditText editNameView = (EditText)view.findViewById(R.id.edit_name);

                if(nameView == null) return false;

                editNameView.setText(nameView.getText());
                nameView.setVisibility(View.GONE);
                editNameView.setVisibility(View.VISIBLE);

                editNameView.requestFocusFromTouch();
                InputMethodManager in = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                in.showSoftInput(editNameView, 0);

                destroyLibrary.setVisibility(View.VISIBLE);

                return true;
            }
        });

        newLibrary = (ImageButton)activity.findViewById(R.id.new_library);
        newLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newLibrary();
            }
        });

        destroyLibrary = (ImageButton)activity.findViewById(R.id.destroy_library);
        destroyLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyLibrary();
            }
        });

        destroyLibrary.setVisibility(View.GONE);
    }

    void onDoneEditing() {
        destroyLibrary.setVisibility(View.GONE);
    }

    private void newLibrary() {
        library = new Library();
        library.root("http://example.com/");
        library.save(activity);

        updateCursor();
    }

    private void destroyLibrary() {
        library.destroy(activity);
        updateCursor();
    }

    private void updateCursor() {
        SQLiteDatabase db = DatabaseHelper.instance(activity);
        Cursor result = db.rawQuery("SELECT * FROM libraries", new String[] {});
        librariesAdapter.changeCursor(result);
    }
}
