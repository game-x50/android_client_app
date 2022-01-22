package com.ruslan.hlushan.core.ui.impl.tools

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.annotation.LayoutRes
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.ruslan.hlushan.core.ui.api.utils.ViewModifier

class DrawerViewModifierImpl(
        @LayoutRes private val layoutResId: Int
) : ViewModifier {

    override fun modify(view: View): View {
        val drawerLayout = DrawerLayout(view.context)

        drawerLayout.addView(view, DrawerLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

        val developerView = LayoutInflater.from(view.context).inflate(layoutResId, drawerLayout, false)
        val layoutParams = DrawerLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        layoutParams.gravity = GravityCompat.END
        drawerLayout.addView(developerView, layoutParams)

        return drawerLayout
    }
}