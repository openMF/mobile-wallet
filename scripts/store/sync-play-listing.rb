#!/usr/bin/env ruby
# scripts/store/sync-play-listing.rb
#
# Upload Play Store store listing metadata + screenshots + feature graphic
# directly via the Google Play Developer API (edits.listings / edits.images).
#
# Does NOT require an existing release in the track — works on a brand-new app.
# Uses google-apis-androidpublisher_v3 which ships with Fastlane.
#
# Usage:
#   bundle exec ruby scripts/store/sync-play-listing.rb
#   bundle exec ruby scripts/store/sync-play-listing.rb --dry-run

require "json"
require "pathname"

# ── Gems (bundled with Fastlane) ─────────────────────────────────────────────
begin
  require "google/apis/androidpublisher_v3"
  require "googleauth"
rescue LoadError => e
  warn "[ERROR] Missing gem: #{e.message}"
  warn "        Run: bundle install"
  exit 1
end

# ── Config ────────────────────────────────────────────────────────────────────

require_relative "../../deployment/_shared/lib/build_secrets"  # secrets path resolver (LAYOUT SoT)
REPO_ROOT     = Pathname.new(__FILE__).dirname.parent
JSON_KEY      = REPO_ROOT / BuildSecrets.for.path(:play_service_account)
METADATA_ROOT = REPO_ROOT / "deployment/android/metadata"
DRY_RUN       = ARGV.include?("--dry-run")

# Read package name from libs.versions.toml
toml_path = REPO_ROOT / "gradle/libs.versions.toml"
PACKAGE_NAME = File.readlines(toml_path)
  .find { |l| l.match?(/^\s*appId\s*=/) }
  &.then { |l| l.match(/"([^"]+)"/)[1] } || "org.mifos.kmp.template"

# ── Preflight ─────────────────────────────────────────────────────────────────

abort "[ERROR] Service account not found: #{JSON_KEY}" unless JSON_KEY.exist?
abort "[ERROR] Metadata not found: #{METADATA_ROOT}" unless METADATA_ROOT.directory?

puts
puts "━" * 72
puts "  Play Store Listing Sync (Direct API)"
puts "  Package:  #{PACKAGE_NAME}"
puts "  Metadata: #{METADATA_ROOT.relative_path_from(REPO_ROOT)}"
puts "  Title:    #{(METADATA_ROOT / "en-US/title.txt").read.strip rescue "(missing)"}"
puts "━" * 72
puts

if DRY_RUN
  puts "[DRY RUN] Would upload listing — exiting without API calls."
  exit 0
end

# ── Authenticate ──────────────────────────────────────────────────────────────

SCOPE = Google::Apis::AndroidpublisherV3::AUTH_ANDROIDPUBLISHER
auth  = Google::Auth::ServiceAccountCredentials.make_creds(
  json_key_io: JSON_KEY.open,
  scope: SCOPE
)

service = Google::Apis::AndroidpublisherV3::AndroidPublisherService.new
service.authorization = auth

puts "[1/5] Authenticating…"
auth.fetch_access_token!
puts "      OK"

# ── Open edit ─────────────────────────────────────────────────────────────────

puts "[2/5] Opening Play Store edit…"
edit = service.insert_edit(PACKAGE_NAME)
edit_id = edit.id
puts "      Edit ID: #{edit_id}"

# ── Upload listing metadata per locale ───────────────────────────────────────

puts "[3/5] Uploading listing metadata…"

locales_uploaded = 0
(METADATA_ROOT.children.select(&:directory?)).each do |locale_dir|
  lang = locale_dir.basename.to_s
  next if lang.start_with?(".")
  next if lang == "images"

  title_file       = locale_dir / "title.txt"
  short_desc_file  = locale_dir / "short_description.txt"
  full_desc_file   = locale_dir / "full_description.txt"

  next unless title_file.exist?

  listing = Google::Apis::AndroidpublisherV3::Listing.new(
    language:          lang,
    title:             title_file.read.strip,
    short_description: short_desc_file.exist? ? short_desc_file.read.strip : "",
    full_description:  full_desc_file.exist?  ? full_desc_file.read.strip  : "",
  )

  service.update_edit_listing(PACKAGE_NAME, edit_id, lang, listing)
  puts "      #{lang}: #{listing.title}"
  locales_uploaded += 1
end

abort "[ERROR] No locale directories found under #{METADATA_ROOT}" if locales_uploaded.zero?

# ── Upload screenshots + feature graphic ─────────────────────────────────────

puts "[4/5] Uploading images…"

IMAGE_TYPE_MAP = {
  "phoneScreenshots"     => "phoneScreenshots",
  "sevenInchScreenshots" => "sevenInchScreenshots",
  "tenInchScreenshots"   => "tenInchScreenshots",
  "tvScreenshots"        => "tvScreenshots",
  "wearScreenshots"      => "wearScreenshots",
}.freeze

images_dir    = METADATA_ROOT / "images"
images_uploaded = 0

# Mirror images to every locale that has a listing — Play Console shows images
# per-locale, so a listing in en-GB with no images appears blank even when
# en-US images are present.
locales_with_listings = (METADATA_ROOT.children.select(&:directory?))
  .map { |d| d.basename.to_s }
  .reject { |l| l.start_with?(".") || l == "images" }

if images_dir.directory?
  locales_with_listings.each do |lang|
    IMAGE_TYPE_MAP.each do |folder, image_type|
      screenshots_dir = images_dir / folder
      next unless screenshots_dir.directory?
      pngs = screenshots_dir.glob("*.png").sort
      next if pngs.empty?

      begin
        service.deleteall_edit_image(PACKAGE_NAME, edit_id, lang, image_type)
      rescue Google::Apis::ClientError; end

      pngs.each do |png|
        service.upload_edit_image(
          PACKAGE_NAME, edit_id, lang, image_type,
          upload_source: png.to_s, content_type: "image/png"
        )
        puts "      #{lang}/#{image_type}: #{png.basename}"
        images_uploaded += 1
      end
    end

    feature_graphic = images_dir / "featureGraphic.png"
    if feature_graphic.exist?
      begin
        service.deleteall_edit_image(PACKAGE_NAME, edit_id, lang, "featureGraphic")
      rescue Google::Apis::ClientError; end
      service.upload_edit_image(
        PACKAGE_NAME, edit_id, lang, "featureGraphic",
        upload_source: feature_graphic.to_s, content_type: "image/png"
      )
      puts "      #{lang}/featureGraphic: featureGraphic.png"
      images_uploaded += 1
    end
  end
end

puts "      #{images_uploaded} image(s) uploaded across #{locales_with_listings.size} locale(s)"

# ── Commit edit ───────────────────────────────────────────────────────────────

puts "[5/5] Committing edit…"
service.commit_edit(PACKAGE_NAME, edit_id)
puts "      Committed ✓"

puts
puts "✓ Play Store listing updated for #{PACKAGE_NAME}"
puts "  View: https://play.google.com/console/developers"
