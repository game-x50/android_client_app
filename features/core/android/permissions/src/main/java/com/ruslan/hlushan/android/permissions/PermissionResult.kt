package com.ruslan.hlushan.android.permissions

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