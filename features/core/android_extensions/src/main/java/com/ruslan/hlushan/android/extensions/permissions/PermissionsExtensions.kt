package com.ruslan.hlushan.android.extensions.permissions

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

fun <F> F.askPermissions(
        requestCode: Int,
        vararg permissions: String
) where F : Fragment, F : PermissionResultListener =
        this.childFragmentManager.askPermissions(requestCode, *permissions)

fun <A> A.askPermissions(
        requestCode: Int,
        vararg permissions: String
) where A : FragmentActivity, A : PermissionResultListener =
        this.supportFragmentManager.askPermissions(requestCode, *permissions)

private fun FragmentManager.askPermissions(requestCode: Int, vararg permissions: String) {
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

    fragment.askPermissions(requestCode, *permissions)
}

internal class PermissionsHandlerFragment : Fragment() {

    private val askedRequestCodes = mutableListOf<Int>()

    private val parentListener: PermissionResultListener?
        get() = ((parentFragment as? PermissionResultListener)
                 ?: (activity as? PermissionResultListener))

    override fun onRequestPermissionsResult(
            requestCode: Int,
            askedPermissions: Array<out String>,
            grantResults: IntArray
    ) {
        val separated = this.separatePermissions(permissions = askedPermissions)
        val result = PermissionResult.Response(requestCode = requestCode, permissions = separated)
        notifyPermissionResult(result)
    }

    @Suppress("MandatoryBracesIfStatements")
    fun askPermissions(requestCode: Int, vararg permissions: String) =
            if (askedRequestCodes.remove(element = requestCode)) {
                requestPermissions(permissions, requestCode)
            } else when (val separated = this.separatePermissions(permissions = permissions)) {
                is SeparatedPermissions.AllGranted                              -> {
                    notifyPermissionResult(PermissionResult.Response(
                            requestCode = requestCode,
                            permissions = separated
                    ))
                }
                is SeparatedPermissions.AtLeastOneTemporallyDenied              -> {
                    askedRequestCodes.add(requestCode)
                    notifyPermissionResult(PermissionResult.ShowRationale(
                            requestCode = requestCode,
                            permissions = separated
                    ))
                }
                is SeparatedPermissions.DeniedJustPermanentlyAndMaybeAreGranted -> {
                    requestPermissions(permissions, requestCode)
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

private fun Fragment.separatePermissions(permissions: Array<out String>): SeparatedPermissions {
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