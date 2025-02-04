source "https://rubygems.org"

begin
  ruby File.read(".ruby-version").strip
rescue Errno::ENOENT
  ruby "3.3.5"  # Default version if .ruby-version doesn't exist
end

gem "fastlane"

plugins_path = File.join(File.dirname(__FILE__), "fastlane", "Pluginfile")
eval_gemfile(plugins_path) if File.exist?(plugins_path)