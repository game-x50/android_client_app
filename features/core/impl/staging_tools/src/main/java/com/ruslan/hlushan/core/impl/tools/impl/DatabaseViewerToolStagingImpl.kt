package com.ruslan.hlushan.core.impl.tools.impl

import android.content.Context
import android.content.Intent
import com.ruslan.hlushan.core.api.tools.DatabaseViewerTool
import com.ruslan.hlushan.third_party.androidx.room.utils.DatabaseViewInfo
import com.wajahatkarim3.roomexplorer.RoomExplorerActivity
import javax.inject.Inject

internal class DatabaseViewerToolStagingImpl
@Inject
constructor(
        private val appContext: Context
) : DatabaseViewerTool {

    override fun showDatabase(database: DatabaseViewInfo) {

        val roomExplorerIntent = Intent(appContext, RoomExplorerActivity::class.java)
                .putExtra(RoomExplorerActivity.DATABASE_CLASS_KEY, database.clazz)
                .putExtra(RoomExplorerActivity.DATABASE_NAME_KEY, database.name)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        appContext.startActivity(roomExplorerIntent)
    }
}