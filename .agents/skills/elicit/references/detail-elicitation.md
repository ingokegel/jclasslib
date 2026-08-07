# Detail elicitation

Use these techniques when an entity has a lifecycle (transition graph) but needs rules, surfaces, fields and data dependencies filled in. The shape is known; the detail isn't.

## Start from examples, not abstractions

Before writing rules, collect concrete scenarios. Ask for at least two specific cases.

"Give me a case where someone was hired. Now give me one where they were rejected at screening. What was different?"

The differences between the cases reveal the `requires` guards. The commonalities reveal the `ensures` outcomes. Rules emerge from comparing scenarios rather than being defined in the abstract.

When a rule is ambiguous or the user can't articulate the conditions, ask for more examples. "Can you give me a case where this went a different way?" Each new example narrows the rule.

## Actor walkthrough

Pick a specific human actor and walk through their perspective in first person. For system actors (external APIs, background services), use third person instead: "The payment gateway receives a charge request. What does it need? What does it return?"

"You're the recruiter. You open the system on Monday morning. What's in front of you?" The answer is surface `exposes` — the data the actor sees.

"What can you do from here?" The answer is surface `provides` — the actions available.

"When would this action not be available?" The answer is the `when` guard on the provides clause.

"After you've done that, what happens next? Who takes over?" The answer reveals the handoff to the next actor and the next surface.

## Trace data flow backward

When you encounter a decision point or a rule with preconditions, work backward from the requirement.

"The hiring manager needs to see interview feedback before deciding. Where does that feedback come from? Who provides it? At what point in the process?"

Each "where does this come from?" reveals a data dependency. Follow the chain until you reach a surface where an actor enters the data or an external system provides it. If the chain ends without a source, you've found a gap — a `missing_producer` in checker terms.

## Ground abstract descriptions

When a user describes something abstractly ("the system shows relevant information"), ground it with a concrete question.

"If you were looking at the screen right now, what would you see? What specific information?" This surfaces the exact fields that need to be in `exposes`.

"Can you sketch what that screen looks like, in words? What's at the top? What's the main content?"

## Prompt for external system boundaries

When a step depends on data from outside the system, ask how it enters.

"You mentioned the background check results come back. How does that happen? Does someone enter them manually, or does an external service send them automatically?"

The answer determines whether you need a surface facing a human actor or a contract integration point facing a system. Many process gaps involve external systems (payment processors, identity verification, notification services) where the spec needs an entry point but the user assumes the data just appears.

## What to produce

If an entity's transition graph has grown beyond eight or so states, consider whether the lifecycle should be split. A booking entity that spans request, rental, inspection and deposit settlement might be clearer as separate entities linked by relationships. Ask the user: "This entity is covering a lot of ground. Would it be clearer to separate the [X] phase from the [Y] phase into its own entity?"

At the end of detail elicitation for an entity, you should have:

- **Fields** with types, including state-dependent fields (`when` clauses)
- **Rules** witnessing every transition, with `requires` and `ensures`
- **Surfaces** for each actor that interacts with this entity, with `exposes` and `provides`
- **Relationships** connecting this entity to related entities
- **Config** for any variable values (durations, thresholds, limits)
- **Open questions** for anything unresolved

Write the spec, then describe what it says in domain language and verify it with the user before moving on (see [assumption checking](assumption-checking.md)).
