package com.ruslan.hlushan.core.impl.tools.impl

import android.content.Context
import com.chuckerteam.chucker.api.Chucker
import com.ruslan.hlushan.core.api.tools.ChuckTool
import javax.inject.Inject

internal class ChuckToolStagingImpl
@Inject
constructor(
        private val appContext: Context
) : ChuckTool {

    override fun openScreen() = appContext.startActivity(Chucker.getLaunchIntent(appContext))
}