import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.publish)
    alias(libs.plugins.dokka)
}

subprojects {
    apply<SpotlessPlugin>()

    extensions.configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            val ktlintVersion = libs.versions.ktlint.get()
            targetExclude("**/build/**/*.kt")
            ktlint(ktlintVersion)
                .editorConfigOverride(
                    mapOf(
                        "ktlint_code_style" to "android",
                        "ij_kotlin_allow_trailing_comma" to true,
                        "ij_kotlin_allow_trailing_comma_on_call_site" to true,
                    ),
                )
        }
    }
}

tasks.dokkaHtmlMultiModule {
    moduleVersion.set("1.0.0-alpha01")
    outputDirectory.set(rootDir.resolve("docs"))
}
