# deployment/_shared/before_all.rb — regenerate the derived store listing before ANY lane.
#
# app-profile/ (app.yaml + platforms/<p>/*.yaml + platforms/<p>/media) is the SINGLE source of truth
# for all store-listing text + media. deployment/**/metadata is DERIVED (customization-surface
# owner:generated, gitignored) — fastlane's `metadata_path` reads that dir, so it MUST be materialized
# from app-profile via `./gradlew syncForkConfig` BEFORE deliver/supply run, or a fresh clone / CI
# deploy would upload EMPTY listings.
#
# Imported by BOTH deployment/Fastfile (CI entry) and deployment/fastlane/Fastfile (local) so it runs
# whichever Fastfile fastlane loads. Runs once per invocation (SYNC_FORK_DONE latch — /idea-deploy
# STEP 1.7.0 already sets it when it pre-syncs). Fail-soft: a gradle hiccup never blocks a deploy.
# Files fastlane's match / gym / version steps write back into the TRACKED source tree during a build:
# the resolved signing identity into cmp-ios project.pbxproj (PRODUCT_BUNDLE_IDENTIFIER +
# PROVISIONING_PROFILE_SPECIFIER = "match AppStore <id>") and the computed version into Info.plist /
# the podspec. Committing those RE-HARDCODES the template bundle id into every fork's Xcode project
# (breaking white-label) and trips scripts/product-health/checks/ios-pbxproj-identity.sh. A deploy must
# NEVER dirty tracked source, so restore this allow-list to its committed state after every lane
# (success AND error) — the deploy already built/uploaded from the mutated files; we just clean the
# tree so `git add -A` (git-session-commit) can't sweep the writeback in. (2026-08-09.)
DEPLOY_MUTATED_SOURCES = [
  "cmp-ios/iosApp.xcodeproj/project.pbxproj",
  "cmp-ios/iosApp/Info.plist",
  "cmp-shared/cmp_shared.podspec",
].freeze

def _deploy_repo_root
  root = Dir.pwd
  20.times do
    break if File.exist?(File.join(root, "app-profile", "app.yaml"))
    parent = File.dirname(root)
    break if parent == root
    root = parent
  end
  root
end

def restore_deploy_mutated_sources
  root = _deploy_repo_root
  DEPLOY_MUTATED_SOURCES.each do |rel|
    next unless File.exist?(File.join(root, rel))
    # tracked-only, restore to HEAD: discards the deploy's signing/version writeback. No-op if unchanged.
    next unless system("git", "-C", root, "ls-files", "--error-unmatch", "--", rel, out: File::NULL, err: File::NULL)
    system("git", "-C", root, "checkout", "--", rel, out: File::NULL, err: File::NULL)
  end
end

before_all do
  # START-CLEAN: remove any fastlane_tmp keychain left DEFAULT by a prior run that died before
  # delete_keychain (build error / hard kill), and restore the developer's login keychain as default —
  # otherwise unrelated apps keep prompting for fastlane_tmp. Idempotent, no-op on CI / Android.
  cleanup_ci_keychain rescue nil

  # Locate repo root (dir holding app-profile/app.yaml) by walking up from cwd — robust to whatever
  # cwd fastlane sets for the loaded Fastfile (deployment/ for CI, repo root for local).
  repo_root = Dir.pwd
  20.times do
    break if File.exist?(File.join(repo_root, "app-profile", "app.yaml"))
    parent = File.dirname(repo_root)
    break if parent == repo_root
    repo_root = parent
  end

  if ENV["SYNC_FORK_DONE"].nil? && File.exist?(File.join(repo_root, "app-profile", "app.yaml"))
    UI.message("app-profile → deployment/**/metadata: materializing store listing via ./gradlew syncForkConfig…")
    begin
      Dir.chdir(repo_root) { sh("./gradlew", "-q", "syncForkConfig") }
    rescue => e
      UI.important("syncForkConfig failed (#{e.message}) — deployment/metadata may be stale; using existing files")
    end
    ENV["SYNC_FORK_DONE"] = "1"
  end
end

# END-CLEAN: guaranteed fastlane_tmp keychain teardown after EVERY lane — on success (after_all) AND on
# failure/exception (error). Pairs with the setup_ci create_keychain so a local run never leaves
# fastlane_tmp as the macOS default keychain. (A hard SIGKILL still can't run these — the before_all
# start-clean above catches that leftover on the next run.) Idempotent, no-op on CI / Android.
after_all do |_lane, _options|
  cleanup_ci_keychain rescue nil
  restore_deploy_mutated_sources rescue nil
end

error do |_lane, _exception, _options|
  cleanup_ci_keychain rescue nil
  restore_deploy_mutated_sources rescue nil
end
