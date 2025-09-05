### Module Graph

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
  :cmp-desktop --> :core:common
  :cmp-desktop --> :core:data
  :cmp-desktop --> :core:model
  :cmp-desktop --> :core:datastore
  :cmp-desktop --> :mifospay-shared
```
# :cmp-desktop module
## Dependency graph
![Dependency graph](../docs/images/graphs-kmp/dep_graph_mifospay_desktop.svg)
