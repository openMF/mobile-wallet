# deployment/desktop/macos-dmg-unsigned/lane.rb
# Imported by fastlane/Fastfile via `import` directive.
# Tier-1 unsigned macOS DMG — no code signing required.
# Users will see a Gatekeeper warning on first launch; for a notarized build see
# deployment/desktop/dmg-notarized/.
#
# Lane: buildMacDmg
#   Builds the DMG via Gradle's packageReleaseDmg task, then uploads to the
#   GitHub Release for the current git tag.
#
# Env vars (optional):
#   GIT_TAG   — GitHub Release tag to upload to (defaults to `git describe --tags --abbrev=0`)
#   FLAVOR    — Build flavor passed to Gradle (default: prod)
#   STAGE     — prerelease | beta | stable (default: stable). Direct-distro promotion ladder
#               flag passed to deployment/_shared/scripts/gh-release-stage.sh after upload.

platform :mac do
  desc "Build unsigned macOS DMG and upload to GitHub Release (direct-distro Stage 1/2/3 via STAGE env)"
  lane :buildMacDmg do |options|
    options   = sanitize_options(options)
    repo_root = DEPLOYMENT_REPO_ROOT
    gradlew   = File.join(repo_root, "gradlew")
    flavor    = options[:flavor] || ENV["FLAVOR"] || "prod"
    stage     = (options[:stage] || ENV["STAGE"] || "stable").to_s.downcase
    UI.user_error!("Invalid STAGE: #{stage} (expected: prerelease | beta | stable)") unless %w[prerelease beta stable].include?(stage)

    tag = options[:tag] ||
          ENV["GIT_TAG"] ||
          sh("git -C '#{repo_root}' describe --tags --abbrev=0 2>/dev/null", log: false).strip

    UI.message("🔨 Building macOS DMG (flavor: #{flavor})...")

    sh(
      gradlew, "-p", repo_root,
      ":cmp-desktop:packageReleaseDmg",
      "-PflavorDimension=#{flavor}",
    )

    dmg_dir  = File.join(repo_root, "cmp-desktop/build/compose/binaries/main-release/dmg")
    dmg_path = Dir[File.join(dmg_dir, "*.dmg")].first
    UI.user_error!("DMG not found in #{dmg_dir} after build") unless dmg_path && File.exist?(dmg_path)

    dmg_size_mb = (File.size(dmg_path) / 1_048_576.0).round(1)
    UI.success("Built DMG: #{dmg_path} (#{dmg_size_mb} MB)")

    if tag && !tag.empty?
      UI.message("📤 Uploading to GitHub Release #{tag} (stage=#{stage})...")
      sh("gh release upload '#{tag}' '#{dmg_path}' --clobber")
      sh("bash #{File.join(repo_root, 'deployment/_shared/scripts/gh-release-stage.sh').shellescape} #{tag.shellescape} #{stage.shellescape}")
      UI.success("✅ #{File.basename(dmg_path)} uploaded to GitHub Release #{tag} (stage=#{stage})")
    else
      UI.important("⚠️  GIT_TAG not set — DMG built at #{dmg_path} but not uploaded to any release.")
      UI.important("    Run: gh release upload <tag> '#{dmg_path}' --clobber")
    end

    dmg_path
  end
end
