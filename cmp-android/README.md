### Module Graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :core
    :core:data["data"]
    :core:ui["ui"]
  end
  :cmp-android --> :mifospay-shared
  :cmp-android --> :core:data
  :cmp-android --> :core:ui
```
# :cmp-android module
## Dependency graph
![Dependency graph](../docs/images/graphs-kmp/dep_graph_mifospay_android.svg)
