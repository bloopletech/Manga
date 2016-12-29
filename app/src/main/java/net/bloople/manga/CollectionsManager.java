package net.bloople.manga;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bignerdranch.android.multiselector.MultiSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i on 30/12/2016.
 */

public class CollectionsManager {
    private IndexActivity activity;
    private MultiSelector multiSelector;
    private BooksAdapter adapter;
    private BookListAdapter bookListAdapter;
    private Button newCollection;
    private Button saveCollection;
    private Button editCollection;
    private Button destroyCollection;
    private Button editName;
    private ListView sidebar;
    private BookList list;

    public CollectionsManager(IndexActivity activity, MultiSelector multiSelector, BooksAdapter adapter) {
        this.activity = activity;
        this.multiSelector = multiSelector;
        this.adapter = adapter;
    }

    public void setup() {
        sidebar = (ListView)activity.findViewById(R.id.sidebar);
        bookListAdapter = new BookListAdapter(activity, null);
        sidebar.setAdapter(bookListAdapter);
        updateCursor();

        sidebar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) list = null;
                else list = BookList.findById(activity, parent.getItemIdAtPosition(position));

                activity.useList(list);

                view.setActivated(true);

                if(list == null) {
                    newCollection.setVisibility(View.VISIBLE);
                    editCollection.setVisibility(View.GONE);
                    editName.setVisibility(View.GONE);
                    destroyCollection.setVisibility(View.GONE);
                }
                else {
                    newCollection.setVisibility(View.GONE);
                    editCollection.setVisibility(View.VISIBLE);
                    editName.setVisibility(View.VISIBLE);
                    destroyCollection.setVisibility(View.VISIBLE);
                }
            }
        });

        newCollection = (Button)activity.findViewById(R.id.new_collection);
        newCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCollection();
            }
        });

        saveCollection = (Button)activity.findViewById(R.id.save_collection);
        saveCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCollection();
            }
        });

        editCollection = (Button)activity.findViewById(R.id.edit_collection);
        editCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editCollection();
            }
        });

        editName = (Button)activity.findViewById(R.id.edit_name);
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName();
            }
        });

        destroyCollection = (Button)activity.findViewById(R.id.destroy_collection);
        destroyCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyCollection();
            }
        });

        newCollection.setVisibility(View.VISIBLE);
        editCollection.setVisibility(View.GONE);
        editName.setVisibility(View.GONE);
        destroyCollection.setVisibility(View.GONE);
        saveCollection.setVisibility(View.GONE);
    }

    public void newCollection() {
        list = new BookList();
        list.name("New Collection");
        list.save(activity);

        updateCursor();

        multiSelector.setSelectable(true);

        newCollection.setVisibility(View.GONE);
        editCollection.setVisibility(View.GONE);
        editName.setVisibility(View.GONE);
        saveCollection.setVisibility(View.VISIBLE);
    }

    public void editCollection() {
        activity.useList(null);

        ArrayList<String> keys = list.bookKeys(activity);
        for(String key : keys) {
            multiSelector.setSelected(adapter.positionOf(key), 0, true);
        }

        multiSelector.setSelectable(true);

        newCollection.setVisibility(View.GONE);
        editCollection.setVisibility(View.GONE);
        editName.setVisibility(View.GONE);
        saveCollection.setVisibility(View.VISIBLE);
    }

    public void updateCollection() {
        List<Integer> positions = multiSelector.getSelectedPositions();

        ArrayList<String> keys = new ArrayList<>();
        for(int i : positions) keys.add(adapter.at(i).key());
        list.bookKeys(activity, keys);

        multiSelector.clearSelections();
        multiSelector.setSelectable(false);

        activity.useList(list);

        updateCursor();

        newCollection.setVisibility(View.GONE);
        editCollection.setVisibility(View.VISIBLE);
        editName.setVisibility(View.VISIBLE);
        saveCollection.setVisibility(View.GONE);
    }

    public void editName() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Edit Collection");
        builder.setMessage("Collection name:");

        final EditText userInput = new EditText(activity);
        userInput.setText(list.name());
        builder.setView(userInput);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                list.name(userInput.getText().toString());
                list.save(activity);
                updateCursor();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void destroyCollection() {
        list.destroy(activity);
        updateCursor();
    }

    public void updateCursor() {
        SQLiteDatabase db = DatabaseHelper.instance(activity);
        Cursor result = db.rawQuery("SELECT * FROM lists", new String[] {});
        bookListAdapter.changeCursor(result);
    }
}
