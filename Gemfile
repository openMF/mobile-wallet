source "https://rubygems.org"

ruby '~> 3.3'

# Add compatibility gems for Ruby 3.3+
gem "abbrev"
gem "base64"
gem "mutex_m"
gem "bigdecimal"

gem "fastlane", "~> 2.235.0"
gem "cocoapods", "~> 1.16"

# x86_64-linux added to lockfile PLATFORMS for GHA ubuntu-latest runners.

plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
eval_gemfile(plugins_path) if File.exist?(plugins_path)
