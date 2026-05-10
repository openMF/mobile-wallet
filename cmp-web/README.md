# Mifos Pay Web Module

This module represents the web interface for the Mifos Pay application, built using **Compose Multiplatform (KMP)**.

## Architecture & Dependencies
The web module is integrated with the core system to ensure consistent business logic across platforms. As shown in the module graph below:

* **:core:common**: Shared utility functions.
* **:core:model**: Data models used across the app.
* **:core:data**: Repository and network logic.
* **:core:datastore**: Local storage management.
* **:mifospay-shared**: Shared resources and utility components used across the web and mobile platforms.

## Module Graph
```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :core
    :core:common["common"]
    :core:data["data"]
    :core:model["model"]
    :core:datastore["datastore"]
  end
  :mifospay-web --> :mifospay-shared
  :mifospay-web --> :core:common
  :mifospay-web --> :core:data
  :mifospay-web --> :core:model
  :mifospay-web --> :core:datastore
