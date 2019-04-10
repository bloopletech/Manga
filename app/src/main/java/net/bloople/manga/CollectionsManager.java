package net.bloople.manga;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

class CollectionsManager {
    private IndexActivity activity;
    private BooksAdapter adapter;
    private BookListAdapter bookListAdapter;
    private ImageButton newCollection;
    private Button saveCollection;
    private ImageButton editCollection;
    private ImageButton destroyCollection;
    private ListView sidebar;
    private BookList list;

    CollectionsManager(IndexActivity activity, BooksAdapter adapter) {
        this.activity = activity;
        this.adapter = adapter;
    }

    void setup() {
        sidebar = activity.findViewById(R.id.sidebar);
        bookListAdapter = new BookListAdapter(activity, null);
        sidebar.setAdapter(bookListAdapter);
        updateCursor();

        sidebar.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 0) list = null;
            else list = BookList.findById(activity, parent.getItemIdAtPosition(position));

            activity.useList(list);

            view.setActivated(true);

            if(list == null) {
                adapter.clearSelectedBookIds();
                adapter.setSelectable(false);

                newCollection.setVisibility(View.VISIBLE);
                editCollection.setVisibility(View.GONE);
                destroyCollection.setVisibility(View.GONE);
                saveCollection.setVisibility(View.GONE);
            }
            else {
                newCollection.setVisibility(View.GONE);
                editCollection.setVisibility(View.VISIBLE);
                destroyCollection.setVisibility(View.VISIBLE);
            }
        });

        sidebar.setOnItemLongClickListener((parent, view, position, id) -> {
            final TextView nameView = view.findViewById(R.id.name);
            final EditText editNameView = view.findViewById(R.id.edit_name);

            if(nameView == null) return false;

            editNameView.setText(nameView.getText());
            nameView.setVisibility(View.GONE);
            editNameView.setVisibility(View.VISIBLE);

            editNameView.requestFocusFromTouch();
            InputMethodManager in = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.showSoftInput(editNameView, 0);

            return true;
        });

        newCollection = activity.findViewById(R.id.new_collection);
        newCollection.setOnClickListener(v -> newCollection());

        saveCollection = activity.findViewById(R.id.save_collection);
        saveCollection.setOnClickListener(v -> updateCollection());

        editCollection = activity.findViewById(R.id.edit_collection);
        editCollection.setOnClickListener(v -> editCollection());

        destroyCollection = activity.findViewById(R.id.destroy_collection);
        destroyCollection.setOnClickListener(v -> destroyCollection());

        newCollection.setVisibility(View.VISIBLE);
        editCollection.setVisibility(View.GONE);
        destroyCollection.setVisibility(View.GONE);
        saveCollection.setVisibility(View.GONE);
    }

    private void newCollection() {
        list = new BookList();
        list.name("New Collection");
        list.save(activity);

        updateCursor();

        adapter.clearSelectedBookIds();
        adapter.setSelectable(true);

        newCollection.setVisibility(View.GONE);
        editCollection.setVisibility(View.GONE);
        saveCollection.setVisibility(View.VISIBLE);
    }

    private void editCollection() {
        activity.useList(null);

        adapter.setSelectedBookIds(list.bookIds(activity));
        adapter.setSelectable(true);

        newCollection.setVisibility(View.GONE);
        editCollection.setVisibility(View.GONE);
        saveCollection.setVisibility(View.VISIBLE);
    }

    private void updateCollection() {
        list.bookIds(activity, adapter.getSelectedBookIds());

        adapter.clearSelectedBookIds();
        adapter.setSelectable(false);

        activity.useList(list);

        updateCursor();

        newCollection.setVisibility(View.GONE);
        editCollection.setVisibility(View.VISIBLE);
        saveCollection.setVisibility(View.GONE);
    }

    private void destroyCollection() {
        list.destroy(activity);
        updateCursor();
    }

    private void updateCursor() {
        SQLiteDatabase db = DatabaseHelper.instance(activity);
        Cursor result = db.rawQuery("SELECT * FROM lists", new String[] {});
        bookListAdapter.changeCursor(result);
    }
}
