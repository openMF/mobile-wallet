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
  :cmp-web --> :cmp-shared
  :cmp-web --> :core:common
  :cmp-web --> :core:data
  :cmp-web --> :core:model
  :cmp-web --> :core:datastore
```