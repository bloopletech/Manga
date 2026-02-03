package net.bloople.manga.audit

import android.content.ContentValues
import android.database.Cursor
import net.bloople.manga.db.DatabaseAdapter
import net.bloople.manga.get
import java.lang.IllegalArgumentException

class AuditEvent {
    var id = -1L
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
        id = result["_id"]
        `when` = result["when"]
        val actionText: String = result["action"]
        action = try {
            Action.valueOf(actionText)
        }
        catch(e: IllegalArgumentException) {
            Action.UNKNOWN
        }
        resourceContextType = ResourceType.valueOf(result["resource_context_type"])
        resourceContextId = result["resource_context_id"]
        resourceType = ResourceType.valueOf(result["resource_type"])
        resourceId = result["resource_id"]
        resourceName = result["resource_name"]
        detail = result["detail"]
    }

    fun save() {
        val values = ContentValues()
        values.put("\"when\"", `when`)
        values.put("\"action\"", action.toString())
        values.put("resource_context_type", resourceContextType.toString())
        values.put("resource_context_id", resourceContextId)
        values.put("resource_type", resourceType.toString())
        values.put("resource_id", resourceId)
        values.put("resource_name", resourceName)
        values.put("detail", detail)
        if(id == -1L) {
            id = dba.insert(values)
        }
        else {
            dba.update(values, id)
        }
    }

    companion object {
        private val dba: DatabaseAdapter
            get() = DatabaseAdapter(DatabaseHelper.instance(), "audit_events")

        var UNKNOWN_ID = -1L
    }
}