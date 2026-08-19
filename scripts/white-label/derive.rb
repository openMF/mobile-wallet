#!/usr/bin/env ruby
# frozen_string_literal: true
#
# white-label-derive.rb — emit gradle/fork.properties FROM app-profile (the single fork SoT).
#
# app-profile/{app.yaml, platforms/**/*.yaml} is the SINGLE source of truth for fork identity. The flat
# gradle/fork.properties that syncForkConfig + Gradle read is a DERIVED, gitignored artifact — never
# hand-authored. This script reuses config.rb's AppProfile module + its MAP (the flat-key ↔ app-profile
# dotted-path table the deploy layer already resolves), iterated in reverse to WRITE fork.properties. So
# the derivation can never drift from the mapping the rest of the toolchain uses.
#
# Usage:
#   ruby scripts/white-label/derive.rb [--check] [--out <path>]
#     (no flag)  writes gradle/fork.properties from app-profile
#     --check    prints the derived content + any UNRESOLVED (placeholder/blank) SoT keys; exits 3 if any
#                required key is missing (so `white-label-doctor --check` can gate on it). Writes nothing.
#
# Exit: 0 ok · 2 app-profile missing/unparseable · 3 required SoT keys unfilled (--check)

require "yaml"

REPO_ROOT = File.expand_path("../..", __dir__)
CONFIG_RB = File.join(REPO_ROOT, "deployment", "_shared", "config.rb")
FORK_PROPS = File.join(REPO_ROOT, "gradle", "fork.properties")

abort("white-label-derive: app-profile/app.yaml missing — not a white-label fork") \
  unless File.exist?(File.join(REPO_ROOT, "app-profile", "app.yaml"))

# config.rb defines module AppProfile (+ MAP + .get) then the platform config modules. Loading it here
# reuses the exact resolver the deploy layer uses. It is fail-soft on missing secrets (BuildSecrets#value
# returns nil), so a require during setup — before secrets exist — does not abort.
begin
  require CONFIG_RB
rescue => e
  abort("white-label-derive: could not load config.rb (#{e.class}: #{e.message.lines.first&.strip})")
end

unless defined?(AppProfile) && AppProfile.const_defined?(:MAP)
  abort("white-label-derive: AppProfile::MAP not found in config.rb — the derivation map moved; update this script")
end

check_mode = ARGV.include?("--check")
out_idx = ARGV.index("--out")
out_path = out_idx ? ARGV[out_idx + 1] : FORK_PROPS

# Keys that MUST resolve for a deployable fork (identity + signing + at least one distribution id).
# Everything else is optional (a fork may not ship every platform). Placeholder values (the template
# defaults) count as UNFILLED.
REQUIRED = %w[app.id org.name org.email].freeze
PLACEHOLDER = /\A\s*\z|YOUR_|your(company|app|org)|com\.yourcompany|example\.com|000000000000|Your (Organization|Legal)/i

resolved = {}
unfilled = []
AppProfile::MAP.keys.sort.each do |k|
  v = begin
    AppProfile.get(k)
  rescue StandardError
    nil
  end
  s = v.to_s
  if s.strip.empty? || s.match?(PLACEHOLDER)
    unfilled << k if REQUIRED.include?(k)
    next
  end
  resolved[k] = s
end

if check_mode
  puts "# derived gradle/fork.properties (from app-profile SoT) — #{resolved.size} keys"
  resolved.each { |k, v| puts "#{k}=#{v}" }
  unless unfilled.empty?
    warn "\n❌ white-label-derive: #{unfilled.size} REQUIRED SoT key(s) unfilled in app-profile: #{unfilled.join(', ')}"
    warn "   Fill them in app-profile/app.yaml (or platforms/*/*.yaml), then re-run."
    exit 3
  end
  puts "\n✅ all required SoT keys resolved"
  exit 0
end

unless unfilled.empty?
  warn "❌ white-label-derive: refusing to write — #{unfilled.size} required SoT key(s) unfilled: #{unfilled.join(', ')}"
  exit 3
end

header = "# gradle/fork.properties — DERIVED from app-profile by scripts/white-label/derive.rb. DO NOT EDIT.\n" \
         "# The single fork SoT is app-profile/{app.yaml,platforms/**}. Edit there + re-run `white-label-doctor --apply`.\n"
File.write(out_path, header + resolved.map { |k, v| "#{k}=#{v}" }.join("\n") + "\n")
puts "✅ white-label-derive: wrote #{resolved.size} keys → #{out_path.sub(REPO_ROOT + '/', '')}"
