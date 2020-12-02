package net.bloople.manga;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.AutoCompleteTextView;

public class QueryService {
    public static final int FILTER_QUERIES_LIMIT = 100;

    private Context context;
    private AutoCompleteTextView searchField;
    private QueryAdapter adapter;

    public QueryService(Context context, AutoCompleteTextView searchField) {
        this.context = context;
        this.searchField = searchField;

        adapter = new QueryAdapter(context, null);
        adapter.setFilterQueryProvider(constraint -> {
            SQLiteDatabase db = DatabaseHelper.instance(context);
            return db.rawQuery(
                "SELECT _id, text FROM queries WHERE text LIKE ? ORDER BY last_used_at DESC LIMIT " + FILTER_QUERIES_LIMIT,
                new String[] { constraint + "%" }
            );
        });

        searchField.setAdapter(adapter);
        searchField.setOnDismissListener(() -> adapter.getCursor().close());
        updateCursor();
    }

    public void updateCursor() {
        SQLiteDatabase db = DatabaseHelper.instance(context);
        Cursor result = db.rawQuery(
            "SELECT _id, text FROM queries ORDER BY last_used_at DESC LIMIT " + FILTER_QUERIES_LIMIT,
            new String[] {}
        );
        adapter.changeCursor(result);
    }

    public void onSearch(String text) {
        long now = System.currentTimeMillis();
        Query existing = Query.findByText(context, text);
        if(existing == null) {
            existing = new Query();
            existing.text(text);
            existing.createdAt(now);
        }
        existing.lastUsedAt(now);
        existing.usedCount(existing.usedCount() + 1);
        existing.save(context);

        updateCursor();
    }
}
