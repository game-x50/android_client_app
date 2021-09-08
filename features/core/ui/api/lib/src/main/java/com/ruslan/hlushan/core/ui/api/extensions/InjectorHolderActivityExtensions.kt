package com.ruslan.hlushan.core.ui.api.extensions

import android.app.Activity
import com.ruslan.hlushan.core.api.di.InjectorHolder

val Activity.injectorHolder: InjectorHolder get() = (this.application as InjectorHolder)