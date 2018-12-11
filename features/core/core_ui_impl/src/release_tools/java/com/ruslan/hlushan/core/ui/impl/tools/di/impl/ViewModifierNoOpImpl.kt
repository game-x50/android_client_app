package com.ruslan.hlushan.core.ui.impl.tools.di.impl

import android.view.View
import com.ruslan.hlushan.core.ui.api.utils.ViewModifier

internal class ViewModifierNoOpImpl : ViewModifier {

    override fun modify(view: View): View = view
}