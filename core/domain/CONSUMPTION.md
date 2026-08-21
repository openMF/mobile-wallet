# Consuming `core/domain` in a feature

> Pure business logic — deterministic calculators / processors / transformers over `core/model`
> types. NO Store5, NO repositories, NO DI of data, NO platform APIs, NO coroutines-required I/O.
> Pure `kotlin.*` (+ `kotlinx.datetime`). This is where computation the app does itself lives.

## Call sequence

1. **Author a pure function or use-case** taking domain inputs and returning a domain result — mirror
   `EmiCalculator.computeEmi(principal, annualRatePercent, tenureMonths): EmiResult`,
   `amortizationSchedule(...): List<AmortizationRow>`, or `AffordabilityCalculator.maxAffordableLoan(...)`.
   Wrap a multi-step flow in a use-case type (`CalculateEmiUseCase.calculateEmi(...)`).
2. **Keep it stateless + total** — no injected repos; if it needs data, the ViewModel fetches via
   `core/data` and passes plain domain values in. This keeps the calc unit-testable with zero mocks.
3. **The ViewModel calls the use-case** on its already-loaded `ScreenState.Content` data (or on user
   input from an action) and folds the result into UI state — the calc never reaches back into data.
4. **Test it directly** (`EmiCalculatorTest`, `AffordabilityCalculatorTest`) — pure in / pure out.

## Notes

- Domain output types shared with the UI (`EmiResult`, `AmortizationRow`) belong to computation;
  persisted shapes stay in `core/model` + `core/database`.
- If logic needs a Store read, it is NOT domain — put the orchestration in `core/data` and keep the
  pure math here.

Canonical example: feature/emi-calculator (`CalculateEmiUseCase`), feature/calculators + feature/amortization (`EmiCalculator`, `AffordabilityCalculator`).

Symbols: EmiCalculator, computeEmi, amortizationSchedule, AffordabilityCalculator, maxAffordableLoan, CalculateEmiUseCase, AmortizationRow, AffordabilityResult
