package com.ruslan.hlushan.core.ui.viewbinding.extensions

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.viewbinding.ViewBinding
import com.ruslan.hlushan.core.ui.lifecycle.LifecyclePluginObserver
import com.ruslan.hlushan.extensions.lazyUnsafe
import kotlin.properties.ReadOnlyProperty

fun <V : View?> Activity.bindViewById(@IdRes viewResId: Int): Lazy<V> = lazyUnsafe { findViewById<V>(viewResId) }

fun <A, V> A.bindViewBinding(
        viewBind: (View) -> V,
        @IdRes viewBindingRootId: Int
): ReadOnlyProperty<A, V?>
        where A : Activity, A : LifecyclePluginObserver.Owner, V : ViewBinding =
        ActivityViewBindingBinder<A, V>(viewBind, viewBindingRootId)

private class ActivityViewBindingBinder<in A, out V>(
        viewBind: (View) -> V,
        @IdRes private val viewBindingRootId: Int
) : AbstractViewBindingBinder<A, V>(viewBind)
        where A : Activity, A : LifecyclePluginObserver.Owner, V : ViewBinding {

    override fun getOwnerView(owner: A): View? = owner.findViewById(viewBindingRootId)
}