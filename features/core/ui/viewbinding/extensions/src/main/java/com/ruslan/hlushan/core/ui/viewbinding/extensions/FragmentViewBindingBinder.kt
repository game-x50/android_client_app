package com.ruslan.hlushan.core.ui.viewbinding.extensions

import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import kotlin.properties.ReadOnlyProperty

fun <F, V> F.bindViewBinding(
        viewBind: (View) -> V
): ReadOnlyProperty<F, V?>
        where F : Fragment, F : LifecyclePluginObserver.Owner, V : ViewBinding =
        FragmentViewBindingBinder<F, V>(viewBind)

private class FragmentViewBindingBinder<in F, out V>(
        viewBind: (View) -> V
) : AbstractViewBindingBinder<F, V>(viewBind)
        where F : Fragment, F : LifecyclePluginObserver.Owner, V : ViewBinding {

    override fun getOwnerView(owner: F): View? = owner.view
}