@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.material.extensions

import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.show(show: Boolean) =
        if (show) {
            this.show()
        } else {
            this.hide()
        }