# Mifos Pay: Core Data Module

The `:core:data` module is the central repository layer of the Mifos Pay application. It coordinates data flow between external network sources and local persistence.

## Key Responsibilities
* **Repository Pattern:** Provides a clean API for accessing data.
* **Security & Encryption:** Ensures sensitive payment data is handled securely.
* **Data Synchronization:** Manages logic between network and local storage.

## Dependency Graph
```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :core
    :core:data["data"]
    :core:common["common"]
    :core:datastore["datastore"]
    :core:model["model"]
    :core:network["network"]
    :core:analytics["analytics"]
  end
  :core:data --> :core:common
  :core:data --> :core:datastore
  :core:data --> :core:model
  :core:data --> :core:network
  :core:data --> :core:analytics
