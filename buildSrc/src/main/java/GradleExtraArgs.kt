import java.io.File

object GradleExtraArgs {

    const val lintersConfigFolder = "lintersConfigFolder"
    const val localizationFolder = "localization"
    const val proguardConfigsFolder = "proguardConfigsFolder"

    const val baseKotlinLibrary = "gradle_support_base_kotlin_library"

    const val baseAndroidResources = "gradle_support_base_android_resources"
    const val baseAndroidExecutable = "gradle_support_base_android_executable"
    const val baseAndroidResourcesLibrary = "gradle_support_base_android_resources_library"
    const val baseAndroidLibrary = "gradle_support_base_android_library"
    const val baseAndroidApp = "gradle_support_base_android_app"

    const val androidMinifyDisabled = "gradle_support_android_minify_disabled"

    const val apkSigning = "gradle_support_apk_signing"

    const val androidSingleBuildVariant = "gradle_support_android_single_build_variant"
    const val androidAllBuildVariants = "gradle_support_android_all_build_variants"
    const val applicationBuildVariants = "gradle_support_application_build_variants"

    const val androidLinters = "gradle_support_android_linters"

    const val kapt = "gradle_support_kapt"
    const val dagger2Kapt = "gradle_support_dagger2_kapt"
    const val room = "gradle_support_room"

    const val viewBinding = "gradle_support_android_view_binding"
    const val androidLibraryViewBindingWithExtensions = "gradle_support_android_library_view_binding_with_extensions"
    const val androidAppViewBindingWithExtensions = "gradle_support_android_app_view_binding_with_extensions"

    const val kotlinxSerilization = "gradle_support_kotlinx_serilization"

    const val applicationLeakCanaryTool = "gradle_support_application_leak_canary_tool"

    const val projectPropertiesRead = "gradle_support_project_properties_read"

    @JvmStatic
    fun getCoverageReportPath(projectBuildDir: File): String = "${projectBuildDir}/reports/jacocoCoverage.xml"
}