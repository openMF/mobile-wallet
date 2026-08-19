# fastlane-config/project_config.rb — compatibility shim.
#
# deployment/Appfile requires this path (legacy convention).
# Real config lives at deployment/_shared/project_config.rb.
# This shim keeps deployment/Appfile working without modification.
require File.expand_path("../deployment/_shared/project_config", __dir__)
