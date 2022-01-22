@file:Suppress("PackageNaming")

package com.ruslan.hlushan.third_party.androidx.permissions

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

fun <F> F.askPermissions(
        requestCode: Int,
        permissions: Array<String>
) where F : Fragment, F : PermissionResultListener =
        this.childFragmentManager.askPermissions(requestCode, permissions)

fun <A> A.askPermissions(
        requestCode: Int,
        permissions: Array<String>
) where A : FragmentActivity, A : PermissionResultListener =
        this.supportFragmentManager.askPermissions(requestCode, permissions)

private fun FragmentManager.askPermissions(requestCode: Int, permissions: Array<String>) {
    val fragmentTag = "PERMISSIONS_HANDLER_FRAGMENT_TAG"

    val fragmentInsideFragmentManager = (this.findFragmentByTag(fragmentTag) as? PermissionsHandlerFragment)

    val fragment = if (fragmentInsideFragmentManager != null) {
        fragmentInsideFragmentManager
    } else {
        val newCreatedFragment = PermissionsHandlerFragment()
        this.beginTransaction()
                .add(newCreatedFragment, fragmentTag)
                .commitNowAllowingStateLoss()
        newCreatedFragment
    }

    fragment.askPermissions(requestCode, permissions)
}

internal class PermissionsHandlerFragment : Fragment() {

    private val activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsLauncherResult ->
        val askedPermissionsList = permissionsLauncherResult.map { (singlePermission, granted) -> singlePermission }
        val askedPermissionsSet = askedPermissionsList.toSet()
        val requestCode = askedRequestCodes
                .entries
                .firstOrNull { (key, value) -> value.permissions == askedPermissionsSet }
                ?.key
        if (requestCode != null) {
            val separated = this.separatePermissions(permissions = askedPermissionsList)
            val result = PermissionResult.Response(requestCode = requestCode, permissions = separated)
            notifyPermissionResult(result)
        }
    }

    private val askedRequestCodes = mutableMapOf<Int, RequestCodeWithPermissions>()

    private val parentListener: PermissionResultListener?
        get() = ((parentFragment as? PermissionResultListener)
                 ?: (activity as? PermissionResultListener))

    @Suppress("MandatoryBracesIfStatements")
    fun askPermissions(requestCode: Int, permissions: Array<String>) {
        val alreadyAsked = askedRequestCodes[requestCode]

        return if ((alreadyAsked != null) && alreadyAsked.isPending) {
            alreadyAsked.isPending = false
            activityResultLauncher.launch(permissions)
        } else when (val separated = this.separatePermissions(permissions = permissions.toList())) {
            is SeparatedPermissions.AllGranted                              -> {
                notifyPermissionResult(
                        PermissionResult.Response(
                                requestCode = requestCode,
                                permissions = separated
                        )
                )
            }
            is SeparatedPermissions.AtLeastOneTemporallyDenied              -> {
                askedRequestCodes[requestCode] = RequestCodeWithPermissions(
                        isPending = true,
                        permissions = permissions.toSet()
                )
                notifyPermissionResult(
                        PermissionResult.ShowRationale(
                                requestCode = requestCode,
                                permissions = separated
                        )
                )
            }
            is SeparatedPermissions.DeniedJustPermanentlyAndMaybeAreGranted -> {
                askedRequestCodes[requestCode] = RequestCodeWithPermissions(
                        isPending = false,
                        permissions = permissions.toSet()
                )
                activityResultLauncher.launch(permissions)
            }
        }
    }

    private fun notifyPermissionResult(permissionResult: PermissionResult) {
        if (parentListener != null) {
            parentListener?.onPermissionResult(permissionResult)

            if (permissionResult.permissions is SeparatedPermissions.AllGranted) {
                parentFragmentManager.beginTransaction()
                        .remove(this)
                        .commitNowAllowingStateLoss()
            }
        }
    }
}

private fun Fragment.separatePermissions(permissions: List<String>): SeparatedPermissions {
    val grantedPermissions = permissions
            .filter { singlePermission ->
                val grantType = ContextCompat.checkSelfPermission(this.requireContext(), singlePermission)
                (grantType == PackageManager.PERMISSION_GRANTED)
            }

    val allDeniedPermissions = (permissions.toList() - grantedPermissions)

    val temporallyDeniedPermissions = allDeniedPermissions
            .filter { singlePermission -> this.shouldShowRequestPermissionRationale(singlePermission) }

    val permanentlyDeniedPermissions = (allDeniedPermissions - temporallyDeniedPermissions)

    return when {
        (grantedPermissions.size == permissions.size) -> {
            SeparatedPermissions.AllGranted(
                    grantedPermissions = grantedPermissions
            )
        }
        temporallyDeniedPermissions.isNotEmpty()      -> {
            SeparatedPermissions.AtLeastOneTemporallyDenied(
                    grantedPermissions = grantedPermissions,
                    temporallyDeniedPermissions = temporallyDeniedPermissions,
                    permanentlyDeniedPermissions = permanentlyDeniedPermissions
            )
        }
        else                                          -> {
            SeparatedPermissions.DeniedJustPermanentlyAndMaybeAreGranted(
                    grantedPermissions = grantedPermissions,
                    permanentlyDeniedPermissions = permanentlyDeniedPermissions
            )
        }
    }
}

private class RequestCodeWithPermissions(
        val permissions: Set<String>,
        var isPending: Boolean
)