package com.ruslan.hlushan.core.impl.tools.impl

import android.content.Context
import android.content.Intent
import com.github.moduth.blockcanary.BlockCanary
import com.github.moduth.blockcanary.ui.DisplayActivity
import com.ruslan.hlushan.core.api.tools.BlockCanaryTool
import javax.inject.Inject

/**
 * @author Ruslan Hlushan on 2019-07-18
 */
internal class BlockCanaryToolStagingImpl
@Inject
constructor(
        private val appContext: Context
) : BlockCanaryTool {

    override fun openScreen() {
        val intent = Intent(appContext, DisplayActivity::class.java)
        intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        appContext.startActivity(intent)
    }

    override fun start() = BlockCanary.get().start()

    override fun stop() = BlockCanary.get().stop()
}