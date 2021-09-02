package com.ruslan.hlushan.android.extensions

import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.show(show: Boolean) =
        if (show) {
            this.show()
        } else {
            this.hide()
        }