# Driving the loop

This reference is the procedure `/allium` follows when you hand it a goal: it drives the Allium loop to convergence on your behalf. For the conceptual model and worked walkthroughs, see [recommended loops](./recommended-loops.md).

Drive a goal to convergence by running the Allium loop yourself: **gather context → take action → verify → repeat**, until the spec, tests and code agree. You orchestrate; each phase is an existing skill (`elicit`, `distill`, `propagate`, `tend`, `weed`) plus ordinary implementation. What makes the loop trustworthy is the verification signal — you stop when behaviour is proven against intent, not when the code merely runs.

## 1. Detect the entry point — announce, then proceed

Choose the starting mode from the project state **and the goal's intent**, then **announce the chosen path in one line, name the override, and proceed — do not wait for confirmation.** The user can interrupt and redirect if it's wrong; the entry choice is not a gate.

- No spec and no code → **spec-first**: start with `elicit`.
- No spec, code exists, goal captures/verifies existing behaviour → **code-first**: start with `distill`.
- No spec, code exists, goal adds **new** behaviour → **spec-first**: `elicit` the new behaviour (don't distill — distilling captures what's there, not what you're adding).
- Spec exists, goal **changes** behaviour → start with `tend`.
- Spec exists, code may have **drifted** from it → start with `weed`.

State answers "is there a spec / code?"; the **goal's intent** answers capture-vs-add (`distill` vs `elicit`) and change-vs-reconcile (`tend` vs `weed`) — so read the goal, not just the file tree. If the user gives an explicit entry (`/allium distill <area>`, or just "tend the spec"), use it and skip detection.

Announce like: *"No spec here, code present, goal reads as new behaviour → starting with elicit. (Say 'distill' or 'tend' to switch.)"* This announce-and-proceed applies to the **entry path only** — genuine blocking open questions still pause and escalate (§5).

## 2. Run the loop (one tick)

Announce each phase as it begins with a one-line marker (shown in parentheses below) so the run stays legible across ticks. Let the harness show the underlying commands — don't narrate every command, just the phase boundaries.

1. **Gather context** *(`→ Gather: elicit/distill/tend the spec`)* — run the entry skill (or `tend`) only if the spec needs to change this tick. Treat elicitation as an *inner loop*: keep asking the user questions until the spec covers the edge cases, then continue. Distillation may take several passes. The spec is CLI-checked on every edit (the hook / LSP run `allium check`); **resolve any reported issues before propagating** — tests are generated from the spec, so it must be valid first.
2. **Take action** *(`→ Act: propagate tests, then implement`)* — `propagate` to (re)generate tests when the spec changed, then implement.
   - **Spec-first: confirm the new tests FAIL before implementing.** A generated test that is already green is already covered (reference it, don't duplicate) or vacuous (fix the spec or test).
   - Never edit a generated test to make it pass.
3. **Verify** *(`→ Verify: run tests → weed → allium analyse`)* — actually run it: the project's test command, then `weed` for spec↔code alignment, then `allium analyse` for semantic gaps (dead ends, unreachable states). The spec's *structure* is already validated on every edit (§2.1), so verify adds the behavioural checks: tests, drift, and semantic analysis. Parse the results; never narrate a pass you didn't execute.
4. **Route the outcome:**
   - test fails → fix the code;
   - a test is wrong → `tend` the spec, then `propagate` again;
   - `weed` says the spec is wrong → `tend` the spec;
   - open question → classify and handle (§5).
5. **Record state** in the ledger (§8) and print a one-line summary: `tick n · tests x/y · weed clean/dirty · openQ blocking k / parked m`.

## 3. Convergence (when to stop)

Stop when **all** hold:

- tests pass,
- `weed` reports no divergence,
- no blocking open questions remain (only parked, non-blocking ones),
- (code-first) a fresh `distill` pass finds nothing new.

## 4. Stop conditions & safety

- **Hard cap** — stop after **6** iterations.
- **No-progress cap** — stop after **2** iterations with no change in tests / weed verdict / open-question count (catches thrashing against a test you can't satisfy).
- **Escalate** on a blocking open question (§5).
- **Anti-cheat (non-negotiable)** — never weaken or edit a generated test to pass; honour `config` (no magic numbers in code the spec parameterises).
- On hitting a cap or an unrecoverable error, **stop and report** — don't spin.

Caps default to 6 / 2 and may be overridden per invocation or via a `config` block.

## 5. Open questions: park or escalate

Classify every question the loop surfaces:

- **Blocking / direction-changing** — the answer reshapes the spec, and therefore the tests and code. → **Escalate to the user now**, before doing dependent work. Deferring these creates throwaway.
- **Non-blocking / peripheral** — doesn't affect what's already built or what's next. → **Park** it (spec `open questions` section + ledger) and continue; batch all parked questions into the final report.

Rule: a question is *blocking* iff the next unit of work depends on its answer. Do everything independent of unresolved questions first. If you must proceed past a parked question, log the assumption and prefer cheap-to-revise work — never expensive or irreversible work that hangs off an unresolved structural question.

## 6. Large goals: decompose, then integrate

If the goal spans more than one independent behavioural slice, decompose along the spec's seams — one sub-goal per **entity lifecycle**, **surface**, or **independent rule / data-flow chain**. Order sub-goals topologically by the data-flow / trigger graph (producers before consumers). Run each sub-goal as its own loop.

After the slices converge, run a **whole-spec integration pass** — cross-entity / data-flow / reachability tests plus a full `weed` — so the seams *between* slices converge too. Run this autonomously without blocking to confirm the plan, and produce one consolidated summary at the end.

## 7. Run phases in isolation

Where the harness allows, run each phase as an isolated sub-agent (`tend` and `weed` are already agents) so this orchestrator holds only the loop state and each phase gets a clean context — that is what keeps a long run within budget. The shared interface between phases is the on-disk artefacts (spec, tests, code) plus the ledger; do not rely on in-memory state surviving between phases.

## 8. The ledger

Keep loop state in `.allium-loop/<goal-slug>.json`: goal, mode, tick count, active inner loop, last verdicts, completed sub-goals, and parked (non-blocking) open questions. This makes the loop resumable — a fresh run reads it and continues where it left off.

Git-ignore it: resolve the repo root (`git rev-parse --show-toplevel`; skip if not a git repo), then ensure `.allium-loop/` is ignored there — create `.gitignore` if absent, append if missing, no-op if already ignored (`git check-ignore` first). Best-effort: if it can't be written, continue and say so. Mention it once; don't prompt.

## 9. Verification must be real

The loop is only as good as its verification. Discover the project's test command (framework, runner, test paths — reuse the `propagate` discovery checklist). If the `allium` CLI is not on PATH, run with the reduced signal you have and say so. If verification cannot actually run, **degrade loudly to assisted mode** — tell the user; never claim a pass you did not execute.

## 10. Report

End with: what converged, per–sub-goal status, tests and weed verdict, anything escalated, and all parked questions consolidated.
