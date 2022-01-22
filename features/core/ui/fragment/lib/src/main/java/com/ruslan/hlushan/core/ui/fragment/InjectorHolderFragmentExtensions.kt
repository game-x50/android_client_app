package com.ruslan.hlushan.core.ui.fragment

import androidx.fragment.app.Fragment
import com.ruslan.hlushan.core.di.InjectorHolder
import com.ruslan.hlushan.core.ui.api.extensions.injectorHolder

val Fragment.injectorHolder: InjectorHolder get() = this.requireActivity().injectorHolder