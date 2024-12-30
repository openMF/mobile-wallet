### Module Graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :core
    :core:ui["ui"]
    :core:analytics["analytics"]
    :core:designsystem["designsystem"]
    :core:model["model"]
    :core:common["common"]
  end
  :core:ui --> :core:analytics
  :core:ui --> :core:designsystem
  :core:ui --> :core:model
  :core:ui --> :core:common
```