import org.gradle.api.Project
import java.io.File

object GradleExtraArgs {

    @JvmStatic
    fun getRootProjectPath(project: Project): String = project.rootProject.projectDir.path

    @JvmStatic
    fun getGradleSupportFolder(project: Project): String = "${getRootProjectPath(project)}/gradle_support/"

    @JvmStatic
    fun getLintersConfigFolder(project: Project): String = "${getRootProjectPath(project)}/linters/"

    @JvmStatic
    fun getLocalizationFolder(project: Project): String = "${getRootProjectPath(project)}/localization/"

    @JvmStatic
    fun getProguardConfigsFolder(project: Project): String = "${getRootProjectPath(project)}/proguard_configs/"

    @JvmStatic
    fun getBaseKotlinLibrary(project: Project): String = "${getGradleSupportFolder(project)}base_kotlin_library.gradle"

    @JvmStatic
    fun getBaseAndroidResources(project: Project): String = "${getGradleSupportFolder(project)}base_android_resources.gradle"

    @JvmStatic
    fun getBaseAndroidExecutable(project: Project): String = "${getGradleSupportFolder(project)}base_android_executable.gradle"

    @JvmStatic
    fun getBaseAndroidResourcesLibrary(project: Project): String = "${getGradleSupportFolder(project)}base_android_resources_library.gradle"

    @JvmStatic
    fun getBaseAndroidLibrary(project: Project): String = "${getGradleSupportFolder(project)}base_android_library.gradle"

    @JvmStatic
    fun getBaseAndroidApp(project: Project): String = "${getGradleSupportFolder(project)}base_android_app.gradle"

    @JvmStatic
    fun getAndroidMinifyDisabled(project: Project): String = "${getGradleSupportFolder(project)}android_minify_disabled.gradle"

    @JvmStatic
    fun getApkSigning(project: Project): String = "${getGradleSupportFolder(project)}apk-signing.gradle"

    @JvmStatic
    fun getAndroidSingleBuildVariant(project: Project): String = "${getGradleSupportFolder(project)}android_single_build_variant.gradle"

    @JvmStatic
    fun getAndroidAllBuildVariants(project: Project): String = "${getGradleSupportFolder(project)}android_all_build_variants.gradle"

    @JvmStatic
    fun getApplicationBuildVariants(project: Project): String = "${getGradleSupportFolder(project)}application_build_variants.gradle"

    @JvmStatic
    fun getAndroidLinters(project: Project): String = "${getGradleSupportFolder(project)}android_linters.gradle"

    @JvmStatic
    fun getKapt(project: Project): String = "${getGradleSupportFolder(project)}kapt.gradle"

    @JvmStatic
    fun getDagger2Kapt(project: Project): String = "${getGradleSupportFolder(project)}dagger2_kapt.gradle"

    @JvmStatic
    fun getRoom(project: Project): String = "${getGradleSupportFolder(project)}room.gradle"

    @JvmStatic
    fun getViewBinding(project: Project): String = "${getGradleSupportFolder(project)}android_view_binding.gradle"

    @JvmStatic
    fun getAndroidLibraryViewBindingWithExtensions(project: Project): String = "${getGradleSupportFolder(project)}android_library_view_binding_with_extensions.gradle"

    @JvmStatic
    fun getAndroidAppViewBindingWithExtensions(project: Project): String = "${getGradleSupportFolder(project)}android_app_view_binding_with_extensions.gradle"

    @JvmStatic
    fun getKotlinxSerilization(project: Project): String = "${getGradleSupportFolder(project)}kotlinx_serilization.gradle"

    @JvmStatic
    fun getApplicationLeakCanaryTool(project: Project): String = "${getGradleSupportFolder(project)}application_leak_canary_tool.gradle"

    @JvmStatic
    fun getProjectPropertiesRead(project: Project): String = "${getGradleSupportFolder(project)}project_properties_read.gradle"

    @JvmStatic
    fun getLicenseValidation(project: Project): String = "${getGradleSupportFolder(project)}license_validation.gradle"

    @JvmStatic
    fun getCoverageReportPath(projectBuildDir: File): String = "${projectBuildDir}/reports/jacocoCoverage.xml"
}