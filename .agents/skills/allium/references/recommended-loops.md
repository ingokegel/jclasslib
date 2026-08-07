# Recommended loops

Allium keeps three artefacts in agreement: the **spec** (intended behaviour), the **tests** (the executable contract), and the **code** (the implementation). Productive work is driving those three to convergence and keeping them there as things change. This is a loop, not a pipeline.

There are two entry points — `/elicit` works *forward* from intent, `/distill` works *backward* from existing code — but both run the same convergence loop afterwards.

## The loop: gather context → take action → verify → repeat

An autonomous agent works a task as a recursive goal: it **gathers context**, **takes action**, **verifies** the result, and **repeats** until the goal is met. The phase that decides quality is *verification* — a feedback signal the loop can trust. Allium earns its place by strengthening the two hardest phases, context and verification:

| Phase | What you do | What Allium contributes |
|---|---|---|
| **Gather context** | `/elicit` (forward) or `/distill` (backward) → the spec | The spec *is* the context, and it persists across sessions — no drift, unlike re-reading the code each time |
| **Take action** | `/propagate` → tests, then implement the code | Tests are generated from the spec as the contract; you write code against them |
| **Verify** | run the tests (they must pass), then `/weed` for spec↔code alignment, then the CLI structural checks | A *behavioural* pass/fail signal, not merely "unit tests are green" |
| **Repeat** | iterate until the goal (convergence) is met | The convergence invariant below *is* the goal |

The point isn't the command order — it's that an autonomous loop is only as good as the signal it verifies against, and Allium supplies a behaviour-level signal: tests projected from intent, plus alignment and structural checks. That is the verification feedback that makes a self-driving loop trustworthy.

## The convergence invariant

The loop is "done" for a unit of work when:

- **tests pass** — the implementation satisfies the spec's obligations;
- **`/weed` reports no divergence** — spec and code agree about behaviour;
- **no open questions remain** — the spec's `open questions` section is empty (ambiguities were resolved, not silently guessed);
- **(code-first only) a fresh `/distill` pass surfaces nothing new** — the spec has caught up with the code.

While any of these is false, there is work left. Treat that as the loop's exit condition, not a vibe.

**Convergence governs the whole loop, not the verify phase alone.** Ordinary test-driven coding has a tight inner arc — implement ↔ run tests — that drives *code* toward a contract held fixed. Allium adds an outer arc: the verify phase can feed back into *context*, not just action. When `/weed` shows the **spec** (not the code) is wrong, or a test reveals the intent was ambiguous, or an open question surfaces, you re-enter at gather-context — `/tend` the spec (or ask the human), then re-`/propagate`. So "repeat" can re-enter at any phase: the inner arc converges code → contract, the outer arc converges contract → true intent. This mirrors the software cycle at large — building and testing a thing is what surfaces gaps in what was actually wanted, so requirements themselves iterate; the waterfall assumption that intent is fixed up front is exactly what this avoids. Convergence is reached only when both arcs are still: code matches the contract, and the contract has stopped moving.

## The skills as loop operators

Each skill moves one artefact relative to another:

| Skill | Moves | Direction |
|---|---|---|
| `/elicit` | intent → spec | write spec (forward) |
| `/distill` | code → spec | write spec (backward) |
| `/propagate` | spec → tests | project spec into the contract |
| *(implement)* | tests → code | ordinary coding — **not an Allium skill** |
| `/weed` | code ↔ spec | reconcile divergence either direction |
| `/tend` | edits spec | re-enter the loop after a change |

The implementation step is plain LLM-and-human coding. Allium has no codegen for the application itself; it produces the spec and the tests, and those hold the hand-written code to the specified behaviour.

## Loop A — spec-first (forward, from intent)

**When:** a new feature, or greenfield work where little or no code exists yet.

```
        ┌──────────────────────────────────────────────────────────┐
        │  until (tests pass ∧ weed clean ∧ no open questions)       │
        │                                                            │
  ┌────▶│  1. /elicit  (first pass)  ·or·  /tend  (later passes)     │
  │     │       → spec; CLI surfaces structural issues — resolve     │
  │     │  2. /propagate          → tests for the new behaviour      │
  │     │  3. run the new tests — CONFIRM THEY FAIL (red)            │
  │     │       a test that passes with no new code is a signal:     │
  │     │       • already covered → don't duplicate, reference it    │
  │     │       • vacuous / mis-asserting → /tend spec or fix test   │
  │     │  4. implement  (prompt with spec + failing tests)          │
  │     │  5. run tests                                              │
  │     │       ├─ failing  → fix the CODE, back to 4                │
  │     │       └─ a test looks WRONG → it encodes the spec:         │
  │     │            /tend the spec, then /propagate (back to 2)     │
  │     │  6. /weed  → check spec ↔ code alignment                   │
  │     │       ├─ divergence → reconcile; if intent changed, /tend  │
  └─────┤       └─ open question raised → ask the human, then /tend  │
        └──────────────────────────────────────────────────────────┘
                              │
                              ▼
              spec, tests and code converged
```

Step 3 is the "red" check, and it does double duty. It confirms each generated test actually exercises the new behaviour — a test that is *already green* before you implement means the behaviour is already covered (reference the existing test rather than duplicating it) or the test is vacuous (fix it). It also guarantees you see the tests transition red → green, so a later pass is meaningful rather than accidental.

The other key discipline: when the implementation and a test disagree, decide *which* is wrong. A genuine bug means fix the code (step 5 left branch). A test that asserts the wrong thing means the **spec** is wrong — fix it with `/tend` and re-`/propagate`. Never hand-edit a generated test to make it pass; that severs the contract.

## Loop B — code-first (backward, from existing code)

**When:** an existing or unfamiliar codebase you want to capture, verify, or safely change.

```
        ┌──────────────────────────────────────────────────────────┐
        │  until (distill finds nothing new ∧ failures triaged       │
        │         ∧ weed clean)                                      │
        │                                                            │
  ┌────▶│  1. /distill <area>                                        │
  │     │       → candidate spec: what the code actually does        │
  │     │  2. review the spec                                        │
  │     │       → separate INTENDED behaviour from ACCIDENTAL;       │
  │     │         don't bless bugs as requirements                   │
  │     │  3. /propagate  → tests                                    │
  │     │  4. run tests against the EXISTING code                    │
  │     │       ├─ pass → that behaviour is now captured             │
  │     │       └─ fail → classify the failure:                      │
  │     │            • code bug   → fix the code                     │
  │     │            • spec wrong → /tend spec, then /propagate      │
  │     │  5. /weed  → reconcile any remaining divergence            │
  └─────┤  6. expand: next area / deepen the spec (another pass)     │
        └──────────────────────────────────────────────────────────┘
                              │
                              ▼
        the area's behaviour is specified, tested and reconciled
```

Note the expectation is **inverted** from the spec-first loop: there the new tests should fail until you implement (the "red" check); here they run against code that already exists, so *passing* is the good outcome — it confirms the behaviour is captured. A failing test is *information*, not a defect to suppress: it means the spec and the code disagree, and triaging which is right is the whole point. The equivalent meaningfulness check is catching a test that can't fail for any input (vacuous) during the step-2 review. Distillation of a large codebase usually takes several passes (step 6) — keep going until a pass surfaces nothing new, in the spirit of the [Ralph Wiggum loop](https://ghuntley.com/ralph/).

## Running the loop autonomously (for the LLM and the agents)

Both loops can be driven autonomously — `/tend` and `/weed` already ship as standalone agents. When running unattended, treat the loop as an explicit control loop:

**Per tick:**
1. Advance the spec (`/elicit` or `/distill` on the first tick, `/tend` thereafter only if needed).
2. `/propagate` to refresh tests if the spec changed this tick.
3. Implement / fix code toward green (spec-first) or triage failures (code-first).
4. `/weed` to check alignment.
5. Re-evaluate the convergence invariant.

**Exit conditions — stop when either holds:**
- the convergence invariant is satisfied; or
- a bounded iteration budget is exhausted (don't spin forever).

**Guardrails (do not violate, even to reach green):**
- **Confirm new tests fail before implementing (spec-first).** A generated test that is green before any new code is written is a signal — already-covered or vacuous — not success. Resolve it first; don't carry it into the implement step.
- **Never weaken or edit a generated test to pass.** If a test seems wrong, fix the spec and re-propagate.
- **Escalate ambiguity; don't guess.** A real open question goes to the human and into the spec's `open questions` section — silently picking an interpretation is the exact failure Allium exists to prevent.
- **No magic numbers in code that the spec puts in `config`.** Honour the spec's parameters.
- **Fix the code, not the contract**, when code and spec disagree and the spec is right.

**State worth tracking across ticks:** tests status (pass/fail counts), `/weed` verdict, count of open questions, and — for code-first — whether the last `/distill` pass found anything new. Convergence is all four trending to zero/clean.

## Driving the loop with one prompt

You don't have to invoke the skills one at a time. Hand a single agentic session the goal, the entry point and the stop conditions, and let it run the phases itself. There is no separate "loop mode" to enable — this is a well-structured prompt; Allium supplies the spec, the tests and the verification signal the loop iterates against.

**Spec-first:**

> Goal: `<feature>`. Run the Allium spec-first loop (see this reference).
> 1. `/elicit` to capture the behaviour as a spec. Surface open questions to me before proceeding — don't guess.
> 2. `/propagate` tests, then confirm they fail before implementing; flag any that pass as already-covered or vacuous.
> 3. Implement against the spec + tests. Do not edit tests to make them pass.
> 4. Run tests → `/weed` → `allium` checks.
> 5. Repeat until tests pass, `/weed` is clean, and no open questions remain.
> Stop and ask me if a decision is needed, after **6 iterations**, or after **2 iterations with no progress**. Print a one-line state summary each iteration (tests, weed verdict, open questions).

**Code-first:** replace step 1 with `/distill <area>` followed by a review separating intended from accidental behaviour, and add "a fresh `/distill` finds nothing new" to the exit condition in step 5.

**Stop conditions matter as much as the steps.** Three keep an autonomous run timely and honest:

- **Hard cap** — stop after **6 iterations**.
- **No-progress cap** — stop after **2 iterations** with no measurable change (test pass count, weed verdict, open-question count). This catches thrashing against a test the agent can't satisfy.
- **Escalate on open question** — a decision goes to the human, never a silent guess.

The loop can also be driven by the autonomous `tend` and `weed` agents, or by a harness loop primitive (for example a self-paced `/loop` in Claude Code). Those supply the "keep going" mechanism; the procedure and the exit conditions above are unchanged.

## The "produce the code" prompt

There is no skill for implementation. Point the model at the spec and the propagated tests:

> Implement the behaviour described in `<spec>.allium`. The generated tests in `<tests>` are the contract — make them pass. Follow the spec's rules (`when` / `requires` / `ensures`) for behaviour; the implementation approach is yours, since the spec deliberately doesn't prescribe *how*. Do not weaken or edit the tests to go green — if a test looks wrong, stop and flag it so we revisit the spec.

## Related

- [Assessing specs](./assessing-specs.md) — gauge spec maturity before propagating (a coarse spec yields only thin tests).
- [Actioning findings](./actioning-findings.md) — the `/weed` and `allium analyse` finding taxonomy used in steps 4–5.
- [Test generation](./test-generation.md) — what `/propagate` produces.
