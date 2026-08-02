# deployment/_shared/lib/version_helpers.rb
# Shared version-generation helpers extracted from legacy fastlane/Fastfile.
# Consumed by every lane.rb that needs to compute VERSION / VERSION_CODE.

module VersionHelpers
  module_function

  # Read the gradle-generated version.txt (produced by `./gradlew versionFile`).
  # Prefers the absolute repo-root path (the task writes there); falls back to the legacy
  # relative paths, then "1.0.0". The relative `../version.txt` broke when the fastlane CWD
  # wasn't the repo parent, silently yielding 1.0.0. (2026-06-22 fix)
  def gradle_version
    root = defined?(DEPLOYMENT_REPO_ROOT) ? DEPLOYMENT_REPO_ROOT : nil
    candidates = [root && File.join(root, "version.txt"), "../version.txt", "version.txt"].compact
    path = candidates.find { |p| File.exist?(p) && !File.read(p).strip.empty? }
    path ? File.read(path).strip : "1.0.0"
  rescue StandardError
    "1.0.0"
  end

  # Sanitize a gradle semver (e.g. "2026.1.1-beta.0.9+abc123") to App-Store
  # compatible MAJOR.MINOR.PATCH form (e.g. "2026.1.9"). The final integer is
  # the commit-count extracted from the pre-release suffix.
  def appstore_sanitize(full_version)
    base = full_version.split("-")[0].split("+")[0]
    parts = base.split(".")
    commit_count = "0"
    if full_version.include?("-")
      pre = full_version.split("-")[1].split("+")[0]
      commit_count = pre.split(".").last || "0"
    end
    return full_version unless parts.length >= 2
    "#{parts[0]}.#{parts[1]}.#{commit_count}"
  end
end
