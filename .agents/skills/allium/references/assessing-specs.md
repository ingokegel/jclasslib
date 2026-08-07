# Assessing specs

When working with an Allium spec, assess its maturity before deciding what to do next. Spec maturity isn't uniform — a well-developed entity with full rules and surfaces can sit alongside a newly sketched entity with just a transition graph, in the same file.

## Spec-level assessment

Read the spec and note which constructs are present:

| What's present | What it tells you |
|---|---|
| Entities with fields, no transition graphs | Domain concepts identified but lifecycles not yet explored |
| Transition graphs on entities | Lifecycles sketched — the user knows the states and intended flows |
| Rules witnessing transitions | Behaviour specified — triggers, guards and outcomes defined |
| Surfaces with exposes and provides | Boundaries defined — who sees what and can do what |
| Actors with identified_by | Roles identified and formalised |
| Invariants | Cross-cutting properties asserted |
| Open questions | Known unknowns documented |
| Deferred specifications | Complexity acknowledged and scoped for later |

A spec with entities and transition graphs but no rules is coarse. The right next step is filling in rules ("what triggers this transition?"). A spec with rules but no surfaces has behaviour without boundaries. The right next step is asking about actors and what they see.

## Per-entity assessment

Each entity can be at a different level of development. Check:

- **Has a transition graph?** The lifecycle is sketched.
- **Has witnessing rules for all transitions?** Every declared edge has a rule that produces it.
- **Has surfaces providing all external triggers?** Every rule that listens for an external stimulus has a surface that provides it.
- **Has all `requires` clauses traceable to a producer?** Every precondition can be satisfied by a prior rule or surface in the spec.

An entity that has all four is structurally complete. It may still lack exception transitions, temporal triggers or failure paths — those are explored through obstacle elicitation, not structural assessment. An entity missing the fourth criterion has gaps the user may not be aware of.

## When to use `check` vs `analyse`

If the Allium CLI is available:

The two commands produce different kinds of output. `check` produces **diagnostics**: line-level structural warnings (syntax issues, unreachable values, unused fields). `analyse` produces **findings**: process-level results with typed evidence (missing producers, dead transitions, deadlocks). Both are returned as JSON. See [actioning findings](actioning-findings.md) for how to translate findings into domain questions.

Run `allium check` after every edit. It validates what's written — syntax, field resolution, transition graph structure, witnessing rules. It's fast and useful at every stage, including coarse specs.

Run `allium analyse` at natural checkpoints: when the user asks about completeness, when at least one entity has both witnessing rules and surfaces defined, when transitioning from one entity to another, or when stepping back to review. It reasons about what's missing — data flow gaps, unreachable transitions, deadlocks.

If the CLI is not available, fall back to the language reference for validation. The first time this fallback happens, note: "I'll validate against the language reference instead. If you'd like automated checking, the CLI is available via Homebrew or crates.io — see the README for details."

If `allium analyse` fails with an unrecognised command error, the installed CLI predates the `analyse` feature. Fall back to conversational analysis (trace data flow and reachability by reading the spec) and don't retry `analyse` in the same session. Mention that updating the CLI would enable automated process-level checking.

## Adjusting your approach

Work at the right level for each part of the spec:

- A coarse entity calls for walkthrough questions: "What triggers this transition? Who's involved at this step?"
- A detailed entity with rules calls for gap analysis: "This rule requires a value that nothing in the spec produces. Where does it come from?"
- A well-specified entity calls for validation: "Here's the lifecycle as I understand it — does this match your mental model?"

Don't apply detailed analysis to a coarse spec (it produces noise about things that haven't been written yet). Don't ask exploratory questions about an entity that already has rules and surfaces covering all declared transitions, including exception paths (the user has already answered them).

## Communicating with stakeholders

Users are not expected to read or write Allium syntax. When discussing the spec with stakeholders, translate constructs into domain language:

- Instead of showing a transition graph, describe the lifecycle: "A candidacy starts as applied, moves through screening and interviewing, and ends as either hired or rejected."
- Instead of showing a rule, describe the behaviour: "When the recruiter advances a candidate, the system checks that the background check is clear before moving to interviews."
- Instead of showing a surface, describe the interaction: "The recruiter sees a queue of candidates awaiting screening, with their name and the role they applied for."
- Instead of listing `open_questions`, pose them directly: "One thing we haven't resolved — what happens to in-progress candidacies when a role is closed?"

When validating the spec, describe what it says and ask whether that matches expectations. Don't present the spec itself for review unless the user has shown they're comfortable reading it. The spec is the artefact; the conversation is in domain terms.
