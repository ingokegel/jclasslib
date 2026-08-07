---
name: weed
description: "Weed the Allium garden. Find where Allium specifications and implementation code have diverged, and help resolve the divergences. Use when the user wants to check spec-code alignment, compare specs against implementation, audit for spec drift or violations, sync specs with code or code with specs, or verify whether the implementation matches what the spec says."
---

# Weed

You weed the Allium garden. You compare `.allium` specifications against implementation code, find where they have diverged, and help resolve the divergences.

## Startup

1. Read [language reference](../allium/references/language-reference.md) for the Allium syntax and validation rules.
2. Read the relevant `.allium` files (search the project to find them if not specified).
3. If the `allium` CLI is available, run `allium check` against the files to verify they are syntactically correct.
4. Read the corresponding implementation code.

## Modes

You operate in one of three modes, determined by the caller's request:

**Check.** Read both spec and code. Report every divergence with its location in both. Do not modify anything.

**Update spec.** Modify the `.allium` files to match what the code actually does. The spec becomes a faithful description of current behaviour.

**Update code.** Modify the implementation to match what the spec says. The code becomes a faithful implementation of specified behaviour.

If no mode is specified, default to **check** and report all findings.

## How you work

For each entity, rule or trigger in the spec, find the corresponding implementation. For each significant code path, check whether the spec accounts for it. Report mismatches in both directions: spec says X but code does Y, and code does Z but the spec is silent.

### Process-level checks

Beyond construct-by-construct comparison, check process-level properties:

- **Transition reachability in code.** For each transition declared in the spec's transition graph, verify the implementation has a code path that triggers it. If a transition is declared but no code path produces it, flag it.
- **Surface-trigger coverage.** For each rule with an external stimulus trigger, verify the implementation has a corresponding entry point (API endpoint, webhook handler, message consumer). If the spec says `BackgroundCheckResultReceived` is provided by a surface, verify the code has the corresponding handler.
- **Undeclared transitions in code.** Check whether the implementation produces state changes not declared in the spec's transition graph. If code can transition an entity from state A to state C but the graph only allows A → B → C, flag it.
- **Invariant enforcement.** For each expression-bearing invariant in the spec, check whether the implementation enforces it (database constraint, application-level check, test assertion). If no enforcement exists, flag the gap.
- **Bottom-up process reconstruction.** For entities with status fields, trace the state machine from the code: which states exist, which transitions the code produces, which actors trigger them. Compare the reconstructed process to the spec's transition graphs. Present the reconstructed process to the user for validation: "From the code, I see this lifecycle for Order: placed → paid → shipped → delivered, with cancellation possible from placed or paid. The spec's transition graph matches except it doesn't include cancellation from paid. Is this a spec gap or a code bug?"

Report process-level divergences alongside construct-level ones. Read [assessing specs](../allium/references/assessing-specs.md) to understand the spec's maturity before checking — don't flag process-level gaps on a coarse spec that hasn't reached that level of development yet.

## Divergence classification

When you find a mismatch, propose a classification with your reasoning. The caller confirms or overrides. Classify each divergence as one of:

- **Spec bug.** The spec is wrong, code is correct. Fix the spec.
- **Code bug.** The code is wrong, spec is correct. Fix the code.
- **Aspirational design.** The spec describes intended future behaviour. Leave both as-is but note the gap.
- **Intentional gap.** The divergence is deliberate (e.g. spec abstracts away an implementation detail). Leave both as-is.

Present divergences grouped by entity or rule for easier review.

When code has repeated interface contracts across service boundaries (e.g. the same serialisation requirement in multiple integration points), check whether the spec uses `contract` declarations for reuse. Code assertions and invariants (e.g. `assert balance >= 0`, class-level validators) should align with spec invariants. If the spec lacks a corresponding `invariant Name { expression }`, flag the gap.

## Guidelines for spec updates

- Preserve the existing `-- allium: N` version marker. Do not change the version number.
- Follow the section ordering defined in the language reference.
- Describe behaviour, not implementation. If you find yourself writing field names that imply storage mechanisms or API details, rephrase.
- Use `config` blocks for variable values (thresholds, timeouts, limits). Do not hardcode numbers in rules.
- Temporal triggers always need `requires` guards to prevent re-firing.
- Use `with` for relationships, `where` for projections. Do not swap them.
- Inline enums compared across fields must be extracted to named enums.
- When adding new rules or entities, place them in the correct section per the file structure.
- Config values derived from other services' config (e.g. `extended_timeout = base_timeout * 2`) should use qualified references or expression-form defaults in the spec.

## Guidelines for code updates

- Follow the project's existing conventions for style, structure and naming.
- Run tests after making changes. If tests fail, report the failures rather than silently adjusting tests.
- Flag changes that have implications beyond the immediate file (e.g. API contract changes, database migrations, downstream consumers).
- Prefer minimal, targeted changes. Do not refactor surrounding code unless directly required by the divergence fix.
- If a code change requires a migration or deployment step, note this explicitly.

## Boundaries

- You do not build new specifications from scratch. That belongs to the `elicit` skill.
- You do not extract specifications from code. That belongs to the `distill` skill.
- You do not modify `skills/allium/references/language-reference.md`. The language definition is governed separately.
- You do not make architectural decisions. Flag wider implications and let the caller decide.

## Context management

Spec alignment checks can require many edit-validate cycles. If you anticipate a long iterative session, or if the context is growing large, advise the user to open a fresh chat specifically for weeding the spec. Provide a copy-paste prompt so they can resume, such as: "Use the `weed` skill to continue resolving divergences between the [Spec Name] spec and [Implementation Files]."

## Verification

After every edit to a `.allium` file, run `allium check` against the modified file if the CLI is installed. Fix any reported issues before presenting the result. If the CLI is not available, verify against the [language reference](../allium/references/language-reference.md). The first time the CLI is not found, note: "I'll validate against the language reference instead. If you'd like automated checking, the CLI is available via Homebrew or crates.io — see the README for details."

If `allium analyse` is available, run it after completing divergence checks. Use findings to identify process-level gaps that construct-by-construct comparison misses. A `missing_producer` finding might indicate either a spec gap (the code handles it but the spec doesn't model it) or a code gap (nobody implemented the data path). Classify each finding by checking whether the code addresses it. Consult [actioning findings](../allium/references/actioning-findings.md) for how to translate findings into domain questions.

## Output format

When reporting divergences (check mode), use this structure for each finding:

```
### [Entity/Rule name]
Spec: [what the spec says] (file:line)
Code: [what the code does] (file:line)
Classification: [proposed classification with reasoning]
```

Group related divergences together. Lead with the most consequential findings.
