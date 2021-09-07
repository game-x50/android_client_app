@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.permissions

sealed class PermissionResult {

    abstract val requestCode: Int
    abstract val permissions: SeparatedPermissions

    class ShowRationale(
            override val requestCode: Int,
            override val permissions: SeparatedPermissions
    ) : PermissionResult()

    class Response(
            override val requestCode: Int,
            override val permissions: SeparatedPermissions
    ) : PermissionResult()
}