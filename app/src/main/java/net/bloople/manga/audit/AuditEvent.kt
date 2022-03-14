package net.bloople.manga.audit

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import java.lang.IllegalArgumentException

internal class AuditEvent {
    var _id = -1L
    var `when`: Long
    var action: Action? = null
    var resourceContextType: ResourceType
    var resourceContextId: Long
    var resourceType: ResourceType
    var resourceId: Long
    var resourceName: String
    var detail: String

    constructor(
        `when`: Long,
        action: Action?,
        resourceContextType: ResourceType,
        resourceContextId: Long,
        resourceType: ResourceType,
        resourceId: Long,
        resourceName: String,
        detail: String
    ) {
        this.`when` = `when`
        this.action = action
        this.resourceContextType = resourceContextType
        this.resourceContextId = resourceContextId
        this.resourceType = resourceType
        this.resourceId = resourceId
        this.resourceName = resourceName
        this.detail = detail
    }

    constructor(result: Cursor) {
        _id = result.getLong(result.getColumnIndex("_id"))
        `when` = result.getLong(result.getColumnIndex("when"))
        val actionText = result.getString(result.getColumnIndex("action"))
        action = try {
            Action.valueOf(actionText)
        }
        catch(e: IllegalArgumentException) {
            Action.UNKNOWN
        }
        resourceContextType = ResourceType.valueOf(result.getString(result.getColumnIndex("resource_context_type")))
        resourceContextId = result.getLong(result.getColumnIndex("resource_context_id"))
        resourceType = ResourceType.valueOf(result.getString(result.getColumnIndex("resource_type")))
        resourceId = result.getLong(result.getColumnIndex("resource_id"))
        resourceName = result.getString(result.getColumnIndex("resource_name"))
        detail = result.getString(result.getColumnIndex("detail"))
    }

    fun save(context: Context) {
        val values = ContentValues()
        values.put("\"when\"", `when`)
        values.put("\"action\"", action.toString())
        values.put("resource_context_type", resourceContextType.toString())
        values.put("resource_context_id", resourceContextId)
        values.put("resource_type", resourceType.toString())
        values.put("resource_id", resourceId)
        values.put("resource_name", resourceName)
        values.put("detail", detail)
        val db = DatabaseHelper.instance(context)
        if(_id == -1L) {
            _id = db.insertOrThrow("audit_events", null, values)
        }
        else {
            db.update("audit_events", values, "_id=?", arrayOf(_id.toString()))
        }
    }

    companion object {
        var UNKNOWN_ID = -1L
        fun findById(context: Context, id: Long): AuditEvent? {
            val db = DatabaseHelper.instance(context)
            db.rawQuery("SELECT * FROM audit_events WHERE _id=?", arrayOf(id.toString())).use {
                it.moveToFirst()
                return if (it.count > 0) AuditEvent(it) else null
            }
        }
    }
}