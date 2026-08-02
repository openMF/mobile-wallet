### Module Graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :core
    :core:datastore["datastore"]
    :core:model["model"]
    :core:common["common"]
  end
  :core:datastore --> :core:model
  :core:datastore --> :core:common
```