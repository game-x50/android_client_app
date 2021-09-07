@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.permissions

sealed class SeparatedPermissions {

    class AllGranted(
            val grantedPermissions: List<String>
    ) : SeparatedPermissions()

    class AtLeastOneTemporallyDenied(
            val grantedPermissions: List<String>,
            val temporallyDeniedPermissions: List<String>,
            val permanentlyDeniedPermissions: List<String>
    ) : SeparatedPermissions()

    class DeniedJustPermanentlyAndMaybeAreGranted(
            val permanentlyDeniedPermissions: List<String>,
            val grantedPermissions: List<String>
    ) : SeparatedPermissions()
}