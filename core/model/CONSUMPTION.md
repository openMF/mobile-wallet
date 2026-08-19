# Consuming `core/model` in a feature

> The domain vocabulary — plain, serializable value types shared across every layer. NO Room, NO
> Ktorfit, NO Compose, NO DI. These are the types a store emits, a repository returns, a use-case
> computes over, and a screen renders. The ONE shared shape that keeps DTO / entity / UI in sync.

## Call sequence

1. **Define the domain type** as a `data class` (+ `enum`/`sealed` for closed sets) of pure Kotlin /
   `kotlinx.serialization` fields — mirror `Loan(id, name, kind: LoanKind, principal, ...)`,
   `BillReminder`, `AmortizationRow`, `LoanCalcScenario`.
2. **Keep it storage/transport-agnostic** — no `@Entity`, no `@Serializable`-only-for-one-API quirks.
   The network DTO (`core/network`) maps INTO this; the Room entity (`core/database`) maps FROM this.
   The mappers (`toDomain()` / `toEntity()` / `fromDto()`) live in the store/data layer, not here.
3. **Every other layer imports this type**: the `Store<Key, Loan>` emits it, `LoanRepository` returns
   `ScreenDataStream<List<Loan>>`, `EmiCalculator` computes over its fields, the screen renders it.
4. **Evolve fields here first** — a new field added to the domain model then cascades to DTO, entity,
   and UI (the compiler surfaces every consumer).

## Notes

- If a value only exists in transport, it belongs in a DTO (`core/network`); if only in storage, an
  entity (`core/database`). `core/model` is strictly the SHARED cross-layer shape.

Canonical example: feature/loans (`Loan`, `LoanKind`), feature/bills (`BillReminder`), feature/amortization (`AmortizationRow`).

Symbols: Loan, LoanKind, BillReminder, AmortizationRow, LoanCalcScenario
