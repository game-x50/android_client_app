package com.ruslan.hlushan.core.impl.tools.impl

import android.content.Context
import android.content.Intent
import com.github.pedrovgs.lynx.LynxActivity
import com.github.pedrovgs.lynx.LynxConfig
import com.ruslan.hlushan.core.api.tools.LynxTool
import javax.inject.Inject

internal class LynxToolStagingImpl
@Inject
constructor(
        private val appContext: Context
) : LynxTool {

    override fun openScreen() {
        @SuppressWarnings("MagicNumber")
        val maxNumberOfTracesToShow = 4_000
        val lynxConfig = LynxConfig()
                .setMaxNumberOfTracesToShow(maxNumberOfTracesToShow)

        val lynxActivityIntent = LynxActivity.getIntent(appContext, lynxConfig)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(lynxActivityIntent)
    }
}