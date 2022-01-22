package com.ruslan.hlushan.core.ui.impl.tools.di.impl

import android.view.View
import com.ruslan.hlushan.core.ui.api.utils.ViewModifier
import javax.inject.Inject

internal class ViewModifierNoOpImpl @Inject constructor(): ViewModifier {

    override fun modify(view: View): View = view
}