import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * Registers the `syncForkConfig` task on whichever project applies this plugin.
 *
 * Single source of truth:
 *   gradle/fork.properties    — deployment identity + store metadata (gitignored)
 *   gradle/libs.versions.toml — 6 build-time Gradle values (appId, appDisplayName,
 *                               baseNamespace, desktopAppName, projectName, iosTeamId)
 *
 * Propagates ALL fork identity to:
 *   - cmp-ios/Configuration/Config.xcconfig
 *   - local.properties          (Fastlane bridge — gitignored)
 *   - gradle.properties
 *   - All store metadata .txt files (App Store iOS, App Store macOS, Play Store)
 *   - Platform app icons (from branding/icons/)
 *
 * Apply from root build.gradle.kts:
 *   plugins { id("org.convention.fork.sync-config") }
 *
 * Then run:
 *   ./gradlew syncForkConfig
 */
class SyncForkConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register<SyncForkConfigTask>("syncForkConfig") {
            group       = "fork"
            description = "Sync fork identity from fork.properties + libs.versions.toml to all platform config files."
            // Always re-run — reads gitignored fork.properties, not tracked by Gradle.
            outputs.upToDateWhen { false }
            projectRootDir.set(target.layout.projectDirectory)
            iconBrandingDir.set(target.layout.projectDirectory.dir("branding/icons"))
            iosAppIconDir.set(target.layout.projectDirectory.dir("cmp-ios/iosApp/Assets.xcassets/AppIcon.appiconset"))
            jsResourcesDir.set(target.layout.projectDirectory.dir("cmp-web/src/jsMain/resources"))
            wasmJsResourcesDir.set(target.layout.projectDirectory.dir("cmp-web/src/wasmJsMain/resources"))
            desktopIconsDir.set(target.layout.projectDirectory.dir("cmp-desktop/icons"))
            androidResDir.set(target.layout.projectDirectory.dir("cmp-android/src/main/res"))
        }
    }
}

@DisableCachingByDefault(because = "Reads gitignored fork.properties; writes local.properties and metadata files")
abstract class SyncForkConfigTask : DefaultTask() {

    @get:Internal abstract val projectRootDir:     DirectoryProperty
    @get:Internal abstract val iconBrandingDir:    DirectoryProperty
    @get:Internal abstract val iosAppIconDir:      DirectoryProperty
    @get:Internal abstract val jsResourcesDir:     DirectoryProperty
    @get:Internal abstract val wasmJsResourcesDir: DirectoryProperty
    @get:Internal abstract val desktopIconsDir:    DirectoryProperty
    @get:Internal abstract val androidResDir:      DirectoryProperty

    @TaskAction
    fun sync() {
        val root = projectRootDir.get().asFile

        // ── 1. Load gradle/fork.properties ──────────────────────────────────
        val fork = java.util.Properties()
        val forkFile = File(root, "gradle/fork.properties")
        if (forkFile.exists()) {
            forkFile.reader(Charsets.UTF_8).use(fork::load)
        } else {
            logger.warn(
                "syncForkConfig: gradle/fork.properties not found.\n" +
                "  Copy: cp gradle/fork.properties.template gradle/fork.properties\n" +
                "  Fill in your values, then re-run ./gradlew syncForkConfig"
            )
        }

        // ── 2. Load 6 build-time fields from gradle/libs.versions.toml ──────
        val toml = parseTomlVersions(File(root, "gradle/libs.versions.toml"))

        // Priority: ENV > fork.properties > TOML > ""
        fun get(forkKey: String, envKey: String? = null, tomlKey: String? = null): String {
            envKey?.let { System.getenv(it)?.takeIf(String::isNotBlank) }?.let { return it }
            (fork.getProperty(forkKey))?.takeIf(String::isNotBlank)?.let { return it }
            tomlKey?.let { toml[it]?.takeIf(String::isNotBlank) }?.let { return it }
            return ""
        }

        // Build-time fields (TOML-primary — always in the committed file)
        val appId          = get("app.id",           "APP_BUNDLE_ID",   "appId")
        val appDisplayName = get("app.display.name", "APP_DISPLAY_NAME","appDisplayName")
        val projectName    = get("project.name",     "PROJECT_NAME",    "projectName")

        // Apple
        val appleTeamId   = get("apple.team.id",     "APPLE_TEAM_ID",   "iosTeamId")
        val matchGitUrl   = get("apple.match.git.url","MATCH_GIT_URL")
        val tfGroups      = get("apple.tf.groups",    "TESTFLIGHT_GROUPS")

        // Firebase
        val fbAndroidProd = get("firebase.android.prod.app.id","FIREBASE_ANDROID_PROD_APP_ID")
        val fbAndroidDemo = get("firebase.android.demo.app.id","FIREBASE_ANDROID_DEMO_APP_ID")
        val fbIos         = get("firebase.ios.app.id",         "FIREBASE_IOS_APP_ID")
        val fbGroups      = get("firebase.groups",             "FIREBASE_GROUPS")

        // Org
        val orgName      = get("org.name",         "ORGANIZATION_NAME")
        val orgEmail     = get("org.email",        "APPSTORE_REVIEW_EMAIL")
        val orgFirstName = get("org.first.name",   "APPSTORE_REVIEW_FIRST_NAME")
        val orgLastName  = get("org.last.name",    "APPSTORE_REVIEW_LAST_NAME")
        val orgPhone     = get("org.phone",        "APPSTORE_REVIEW_PHONE")
        val marketingUrl = get("org.marketing.url","APP_MARKETING_URL")
        val privacyUrl   = get("org.privacy.url",  "APP_PRIVACY_URL")
        val supportUrl   = get("org.support.url")

        // Web
        val cloudflareProject = get("web.cloudflare.project","CLOUDFLARE_PAGES_PROJECT_NAME")

        // Store — shared
        val storeTitle         = get("store.title")
        val storeSubtitle      = get("store.subtitle")
        val storeDescription   = get("store.description")
        val storePromoText     = get("store.promotional.text")
        val storeReleaseNotes  = get("store.release.notes")
        val storeCopyright     = get("store.copyright")
        val storeReviewNotes   = get("store.review.notes")
        val reviewDemoUser     = get("store.review.demo.user")
        val reviewDemoPassword = get("store.review.demo.password")
        val iosAgeRating       = get("store.ios.age.rating").ifBlank { "4+" }

        // Store — Android
        val androidChangelog  = get("store.android.changelog")
        val androidShortDesc  = get("store.android.short.description")
        val androidCategory   = get("store.android.category")
        val androidVideoUrl   = get("store.android.video.url")

        // Store — iOS
        val iosKeywords           = get("store.ios.keywords")
        val iosCategory           = get("store.ios.category")
        val iosSecondaryCategory  = get("store.ios.secondary.category")
        val iosAppleTvPrivacyUrl  = get("store.ios.apple.tv.privacy.url")

        // Store — macOS
        val macosKeywords          = get("store.macos.keywords")
        val macosCategory          = get("store.macos.category")
        val macosSecondaryCategory = get("store.macos.secondary.category")

        // Store — Windows / Microsoft Store
        val msAppId      = get("store.windows.ms.app.id", "MS_APP_ID")
        val msPublishMode = get("store.windows.ms.publish.mode").ifBlank { "Manual" }
        val msVisibility  = get("store.windows.ms.visibility").ifBlank { "Public" }

        // ── 3. iOS xcconfig ───────────────────────────────────────────────────
        val xcconfigFile = File(root, "cmp-ios/Configuration/Config.xcconfig")
        if (xcconfigFile.parentFile.exists()) {
            xcconfigFile.writeText(
                "// Fork identity — generated by ./gradlew syncForkConfig\n" +
                "// To change: edit gradle/fork.properties (or libs.versions.toml for app.id),\n" +
                "// then re-run ./gradlew syncForkConfig\n" +
                "APP_BUNDLE_ID = $appId\n" +
                "APP_NAME = $appDisplayName\n" +
                "TEAM_ID = $appleTeamId\n"
            )
            logger.lifecycle("syncForkConfig: wrote cmp-ios/Configuration/Config.xcconfig")
        }
        // NOTE: cmp-ios/Configs/{variant}.xcconfig files are generated by the
        // :cmp-shared:generateIosFlavorXcconfigs task (KMPFlavorsConventionPlugin),
        // which reads the live kmpFlavors DSL. They auto-regenerate on every iOS build.
        // syncForkConfig only owns Config.xcconfig (APP_BUNDLE_ID / APP_NAME / TEAM_ID).

        // ── 4. local.properties (Fastlane bridge — gitignored) ───────────────
        // project_config.rb reads these as a fallback when fork.properties is absent.
        val lp    = File(root, "local.properties")
        val props = java.util.Properties()
        if (lp.exists()) lp.inputStream().use(props::load)
        // Write every fork.properties key with fork.* prefix
        fork.forEach { k, v -> props["fork.$k"] = v }
        // Ensure the 4 TOML-sourced keys are always present
        if (appId.isNotBlank())          props["fork.app.id"]           = appId
        if (appDisplayName.isNotBlank()) props["fork.app.display.name"] = appDisplayName
        if (projectName.isNotBlank())    props["fork.project.name"]     = projectName
        if (appleTeamId.isNotBlank())    props["fork.apple.team.id"]    = appleTeamId
        lp.outputStream().use { props.store(it, "Generated by syncForkConfig — do not edit manually") }
        logger.lifecycle("syncForkConfig: updated local.properties (${props.size} entries)")

        // ── 5. gradle.properties (settings.gradle.kts bridge) ────────────────
        val gp     = File(root, "gradle.properties")
        val gpText = if (gp.exists()) gp.readText() else ""
        gp.writeText(
            if (gpText.contains("fork.project.name="))
                gpText.replace(Regex("fork\\.project\\.name=.*"), "fork.project.name=$projectName")
            else
                gpText.trimEnd() + "\nfork.project.name=$projectName\n"
        )
        logger.lifecycle("syncForkConfig: updated gradle.properties fork.project.name=$projectName")

        // ── 6. Store metadata files ───────────────────────────────────────────
        var written = 0

        // Write only when value is non-blank (skip optional fields that aren't set)
        fun writeIfPresent(rel: String, value: String) {
            if (value.isBlank()) return
            val f = File(root, rel)
            if (!f.parentFile.exists()) return
            f.writeText(value)
            logger.lifecycle("syncForkConfig: wrote $rel")
            written++
        }

        // Always write — clears file when value is blank (used for optional/empty fields
        // that must exist so Fastlane / Deliver don't fall back to stale content)
        fun writeAlways(rel: String, value: String) {
            val f = File(root, rel)
            if (!f.parentFile.exists()) return
            f.writeText(value)
            logger.lifecycle("syncForkConfig: wrote $rel")
            written++
        }

        // Generate App Store / Mac App Store age-rating JSON from store.ios.age.rating
        fun writeRatingConfig(rel: String) {
            val f = File(root, rel)
            if (!f.parentFile.exists()) return
            f.writeText("""{
  "v1_0": {
    "ratings": {
      "violenceCartoonOrFantasy": "NONE",
      "violenceRealistic": "NONE",
      "violenceRealisticProlongedGraphicOrSadistic": "NONE",
      "profanityOrCrudeHumor": "NONE",
      "matureOrSuggestiveThemes": "NONE",
      "horrorOrFearThemesForChildren": "NONE",
      "medicalOrTreatmentInformation": "NONE",
      "alcoholTobaccoOrDrugUseOrReferences": "NONE",
      "gamblingAndContests": "NONE",
      "sexualContentOrNudity": "NONE",
      "sexualContentGraphicAndNudity": "NONE"
    },
    "booleans": {
      "ageRatingProcess": false,
      "gamblingAllowed": false,
      "unrestrictedWebAccess": false,
      "kidsAgeBand": false
    }
  }
}""")
            logger.lifecycle("syncForkConfig: wrote $rel (age rating: $iosAgeRating)")
            written++
        }

        // ── iOS App Store ─────────────────────────────────────────────────────
        writeIfPresent("deployment/ios/appstore/metadata/en-GB/name.txt",             storeTitle)
        writeIfPresent("deployment/ios/appstore/metadata/en-GB/subtitle.txt",         storeSubtitle)
        writeIfPresent("deployment/ios/appstore/metadata/en-GB/description.txt",      storeDescription)
        writeIfPresent("deployment/ios/appstore/metadata/en-GB/keywords.txt",         iosKeywords)
        writeIfPresent("deployment/ios/appstore/metadata/en-GB/promotional_text.txt", storePromoText)
        writeIfPresent("deployment/ios/appstore/metadata/en-GB/release_notes.txt",    storeReleaseNotes)
        writeIfPresent("deployment/ios/appstore/metadata/en-GB/marketing_url.txt",    marketingUrl)
        writeIfPresent("deployment/ios/appstore/metadata/en-GB/privacy_url.txt",      privacyUrl)
        writeIfPresent("deployment/ios/appstore/metadata/en-GB/support_url.txt",      supportUrl)
        writeIfPresent("deployment/ios/appstore/metadata/copyright.txt",              storeCopyright)
        writeIfPresent("deployment/ios/appstore/metadata/primary_category.txt",       iosCategory)
        writeAlways("deployment/ios/appstore/metadata/primary_first_sub_category.txt",  "")
        writeAlways("deployment/ios/appstore/metadata/primary_second_sub_category.txt", "")
        writeAlways("deployment/ios/appstore/metadata/secondary_category.txt",          iosSecondaryCategory)
        writeAlways("deployment/ios/appstore/metadata/secondary_first_sub_category.txt",  "")
        writeAlways("deployment/ios/appstore/metadata/secondary_second_sub_category.txt", "")
        writeIfPresent("deployment/ios/appstore/metadata/review_information/email_address.txt", orgEmail)
        writeIfPresent("deployment/ios/appstore/metadata/review_information/first_name.txt",    orgFirstName)
        writeIfPresent("deployment/ios/appstore/metadata/review_information/last_name.txt",     orgLastName)
        writeIfPresent("deployment/ios/appstore/metadata/review_information/phone_number.txt",  orgPhone)
        writeIfPresent("deployment/ios/appstore/metadata/review_information/notes.txt",         storeReviewNotes)
        writeAlways("deployment/ios/appstore/metadata/review_information/demo_user.txt",     reviewDemoUser)
        writeAlways("deployment/ios/appstore/metadata/review_information/demo_password.txt", reviewDemoPassword)
        writeAlways("deployment/ios/appstore/metadata/en-GB/apple_tv_privacy_policy.txt",   iosAppleTvPrivacyUrl)
        writeRatingConfig("deployment/ios/appstore/metadata/app_store_rating_config.json")

        // ── macOS App Store ───────────────────────────────────────────────────
        writeIfPresent("deployment/desktop/mac-app-store/metadata/en-GB/name.txt",             storeTitle)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/en-GB/subtitle.txt",         storeSubtitle)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/en-GB/description.txt",      storeDescription)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/en-GB/keywords.txt",         macosKeywords)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/en-GB/promotional_text.txt", storePromoText)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/en-GB/release_notes.txt",    storeReleaseNotes)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/en-GB/marketing_url.txt",    marketingUrl)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/en-GB/privacy_url.txt",      privacyUrl)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/en-GB/support_url.txt",      supportUrl)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/copyright.txt",              storeCopyright)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/primary_category.txt",       macosCategory)
        writeAlways("deployment/desktop/mac-app-store/metadata/primary_first_sub_category.txt",  "")
        writeAlways("deployment/desktop/mac-app-store/metadata/primary_second_sub_category.txt", "")
        writeAlways("deployment/desktop/mac-app-store/metadata/secondary_category.txt",          macosSecondaryCategory)
        writeAlways("deployment/desktop/mac-app-store/metadata/secondary_first_sub_category.txt",  "")
        writeAlways("deployment/desktop/mac-app-store/metadata/secondary_second_sub_category.txt", "")
        writeIfPresent("deployment/desktop/mac-app-store/metadata/review_information/email_address.txt", orgEmail)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/review_information/first_name.txt",    orgFirstName)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/review_information/last_name.txt",     orgLastName)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/review_information/phone_number.txt",  orgPhone)
        writeIfPresent("deployment/desktop/mac-app-store/metadata/review_information/notes.txt",         storeReviewNotes)
        writeAlways("deployment/desktop/mac-app-store/metadata/review_information/demo_user.txt",     reviewDemoUser)
        writeAlways("deployment/desktop/mac-app-store/metadata/review_information/demo_password.txt", reviewDemoPassword)
        writeRatingConfig("deployment/desktop/mac-app-store/metadata/app_store_rating_config.json")

        // ── Android Play Store ────────────────────────────────────────────────
        writeIfPresent("deployment/android/metadata/en-US/title.txt",              storeTitle)
        writeIfPresent("deployment/android/metadata/en-US/short_description.txt",  androidShortDesc)
        writeIfPresent("deployment/android/metadata/en-US/full_description.txt",   storeDescription)
        writeIfPresent("deployment/android/metadata/en-US/changelogs/default.txt", androidChangelog)
        writeAlways("deployment/android/metadata/en-US/video.txt", androidVideoUrl)

        // ── Windows / Microsoft Store ─────────────────────────────────────────
        val storeBrokerFile = File(root, "deployment/desktop/microsoft-store/StoreBroker.config.json")
        if (storeBrokerFile.parentFile.exists()) {
            val resolvedAppId = msAppId.ifBlank { "\${ENV:MS_APP_ID}" }
            val msixName = if (projectName.isNotBlank()) "$projectName.msix" else "app.msix"
            storeBrokerFile.writeText("""{
  "_comment": "StoreBroker submission config — generated by ./gradlew syncForkConfig. Edit gradle/fork.properties to change.",
  "appId": "$resolvedAppId",
  "flightId": null,
  "packagePath": "cmp-desktop/build/compose/binaries/main-release/msix/$msixName",
  "targetPublishMode": "$msPublishMode",
  "visibility": "$msVisibility"
}""")
            logger.lifecycle("syncForkConfig: wrote deployment/desktop/microsoft-store/StoreBroker.config.json")
            written++
        }

        logger.lifecycle("syncForkConfig: wrote $written store metadata files")

        // ── 6b. Fork-unique database filename (core/database/DatabaseConfig.kt) ─
        // Derive a per-fork on-disk DB name from appId (dots → underscores) so two
        // forks never collide on desktop/web storage. Uses appId (NOT baseNamespace).
        if (appId.isNotBlank()) {
            val dbName = appId.lowercase().replace(Regex("[^a-z0-9]"), "_") + ".db"
            val dbConfig = File(root, "core/database/src/commonMain/kotlin/kpt/core/database/DatabaseConfig.kt")
            if (dbConfig.parentFile.exists()) {
                dbConfig.writeText(
                    "/*\n" +
                        " * Copyright 2026 Mifos Initiative\n" +
                        " *\n" +
                        " * This Source Code Form is subject to the terms of the Mozilla Public\n" +
                        " * License, v. 2.0. If a copy of the MPL was not distributed with this\n" +
                        " * file, You can obtain one at https://mozilla.org/MPL/2.0/.\n" +
                        " *\n" +
                        " * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE\n" +
                        " */\n" +
                        "package kpt.core.database\n\n" +
                        "/** Fork-unique on-disk DB filename (appId-derived) — generated by syncForkConfig. */\n" +
                        "object DatabaseConfig {\n" +
                        "    const val NAME = \"$dbName\"\n" +
                        "}\n",
                )
                logger.lifecycle("syncForkConfig: wrote core/database/.../DatabaseConfig.kt (NAME=$dbName)")
            }
        }

        // ── 7. Fork icons ─────────────────────────────────────────────────────
        copyForkIcons(root)
    }

    private fun parseTomlVersions(toml: File): Map<String, String> {
        if (!toml.exists()) return emptyMap()
        val result = mutableMapOf<String, String>()
        toml.readLines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.startsWith("#") || !trimmed.contains("=")) return@forEach
            val eq = trimmed.indexOf('=')
            val key = trimmed.substring(0, eq).trim()
            // Drop inline TOML comment first, then strip quotes
            val raw = trimmed.substring(eq + 1).trim().substringBefore(" #").trim()
            result[key] = raw.removePrefix("\"").removeSuffix("\"").trim()
        }
        return result
    }

    private fun copyForkIcons(root: File) {
        val src = iconBrandingDir.get().asFile
        if (!src.exists()) {
            logger.lifecycle("syncForkConfig: branding/icons/ not present — skipping icon copy (template defaults preserved)")
            return
        }

        val mappings = listOf(
            Triple("ios.png",             iosAppIconDir.get().asFile,      "AppIcon.png"),
            Triple("web-favicon.ico",     jsResourcesDir.get().asFile,     "favicon.ico"),
            Triple("web-favicon.ico",     wasmJsResourcesDir.get().asFile, "favicon.ico"),
            Triple("desktop-macos.icns",  desktopIconsDir.get().asFile,    "ic_launcher.icns"),
            Triple("desktop-windows.ico", desktopIconsDir.get().asFile,    "ic_launcher.ico"),
            Triple("desktop-linux.png",   desktopIconsDir.get().asFile,    "ic_launcher.png"),
        )
        var copied = 0
        for ((srcName, dstDir, dstName) in mappings) {
            val from = File(src, srcName)
            if (!from.exists()) continue
            dstDir.mkdirs()
            val to = File(dstDir, dstName)
            from.copyTo(to, overwrite = true)
            logger.lifecycle("syncForkConfig: copied branding/icons/$srcName → ${to.relativeTo(root)}")
            copied++
        }

        val androidSrc = File(src, "android")
        if (androidSrc.isDirectory && androidSrc.list()?.isNotEmpty() == true) {
            val androidDst = androidResDir.get().asFile
            androidSrc.copyRecursively(androidDst, overwrite = true)
            logger.lifecycle("syncForkConfig: copied branding/icons/android/ → ${androidDst.relativeTo(root)}/ (recursive)")
            copied++
        } else {
            logger.lifecycle("syncForkConfig: branding/icons/android/ not present — use Android Studio Image Asset Studio (one-time per fork, commit the result).")
        }

        if (copied == 0) {
            logger.lifecycle("syncForkConfig: branding/icons/ contained no recognised files — template defaults preserved")
        }
    }
}
