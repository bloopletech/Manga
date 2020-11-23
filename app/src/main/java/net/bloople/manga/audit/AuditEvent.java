package net.bloople.manga.audit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class AuditEvent {
    static long UNKNOWN_ID = -1L;

    private long _id = -1L;
    private long when;
    private Action action;
    private ResourceType resourceContextType;
    private long resourceContextId;
    private ResourceType resourceType;
    private long resourceId;
    private String resourceName;
    private String detail;

    static AuditEvent findById(Context context, long id) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM audit_events WHERE _id=?", new String[] { String.valueOf(id) });
        result.moveToFirst();

        if(result.getCount() > 0) {
            AuditEvent event = new AuditEvent(result);
            result.close();
            return event;
        }
        else {
            return null;
        }
    }

    AuditEvent(
        long when,
        Action action,
        ResourceType resourceContextType,
        long resourceContextId,
        ResourceType resourceType,
        long resourceId,
        String resourceName,
        String detail
    ) {
        this.when = when;
        this.action = action;
        this.resourceContextType = resourceContextType;
        this.resourceContextId = resourceContextId;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.detail = detail;
    }

    AuditEvent(Cursor result) {
        _id = result.getLong(result.getColumnIndex("_id"));
        when = result.getLong(result.getColumnIndex("when"));
        String actionText = result.getString(result.getColumnIndex("action"));
        try {
            action = Action.valueOf(actionText);
        }
        catch(IllegalArgumentException e) {
            action = Action.UNKNOWN;
        }
        resourceContextType = ResourceType.valueOf(result.getString(result.getColumnIndex("resource_context_type")));
        resourceContextId = result.getLong(result.getColumnIndex("resource_context_id"));
        resourceType = ResourceType.valueOf(result.getString(result.getColumnIndex("resource_type")));
        resourceId = result.getLong(result.getColumnIndex("resource_id"));
        resourceName = result.getString(result.getColumnIndex("resource_name"));
        detail = result.getString(result.getColumnIndex("detail"));
    }

    long id() {
        return _id;
    }

    long when() {
        return when;
    }

    Action action() {
        return action;
    }

    ResourceType resourceContextType() {
        return resourceContextType;
    }

    long resourceContextId() {
        return resourceContextId;
    }

    ResourceType resourceType() {
        return resourceType;
    }

    long resourceId() {
        return resourceId;
    }

    String resourceName() {
        return resourceName;
    }

    String detail() {
        return detail;
    }

    void save(Context context) {
        ContentValues values = new ContentValues();
        values.put("\"when\"", when);
        values.put("\"action\"", action.toString());
        values.put("resource_context_type", resourceContextType.toString());
        values.put("resource_context_id", resourceContextId);
        values.put("resource_type", resourceType.toString());
        values.put("resource_id", resourceId);
        values.put("resource_name", resourceName);
        values.put("detail", detail);

        SQLiteDatabase db = DatabaseHelper.instance(context);

        if(_id == -1L) {
            _id = db.insertOrThrow("audit_events", null, values);
        }
        else {
            db.update("audit_events", values, "_id=?", new String[] { String.valueOf(_id) });
        }
    }
}
