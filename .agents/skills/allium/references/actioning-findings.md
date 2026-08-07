# Actioning findings

When `allium analyse` produces findings, translate each into a domain question rather than presenting raw output. The user should never see finding types, evidence chains or JSON. They should hear a question that helps them improve their spec.

## Finding types and question strategies

### `missing_producer`

A rule's `requires` clause references a value that nothing in the spec establishes. The data dependency is unsatisfied.

**Ask about the source.** Work backward from the requirement: "The hiring decision needs the background check to be clear, but nothing in the spec says where background check results come from. Is this provided by an external service, or does someone enter it manually?"

The `searched` field in the finding shows what the checker looked for. If it found a partial chain (a rule that could produce the value, but whose trigger is itself unreachable), follow the chain: "There's a rule to handle background check results, but nothing triggers it. How do results get into the system?"

### `unreachable_trigger`

A rule listens for a trigger that no surface provides and no other rule emits. The rule can never fire.

**Ask about the entry point.** "This rule handles background check results, but nothing in the spec says where they come from. Is this a webhook from an external service? A screen where someone enters the result? Something else?"

If the trigger name suggests an external system, prompt for whether it should be a surface (human-facing) or a contract integration point (system-facing).

### `dead_transition`

A transition is declared in the graph and witnessed by a rule, but the rule's guards can never be satisfied. The transition exists on paper but is impossible in practice.

**Ask what's needed.** "The spec says a candidacy can move from screening to interviewing, but that requires the background check to be clear. I can't find a path through the spec that produces a clear background check. What needs to happen for this transition to work?"

The finding's evidence shows which guard is unsatisfiable and why. Use this to frame the question in terms of what's missing, not what's broken.

### `deadlock`

A non-terminal state has no achievable exit. The entity can reach this state but can never leave it.

**Ask what happens when things stall.** "If a candidacy reaches the screening state and the background check never completes, the candidacy is stuck. What should happen in that situation? Is there a timeout? Can someone manually override it?"

If the finding includes cycle evidence (states that loop without reaching terminal), frame it differently: "The spec allows a job to bounce between retrying and waiting indefinitely without ever completing or failing. Is there a maximum number of retries, or a timeout that breaks the cycle?"

### `conflict`

Two rules with different triggers can both fire in the same state and would set the same field to different values. The outcome is ambiguous.

**Ask about priority.** "If a membership is active and both the expiry timer fires and an admin extends it at the same moment, which should win? Should the extension prevent the expiry, or should the expiry take priority?"

This is distinct from actor choice (where one actor picks between alternatives). Conflicts arise from independent triggers that the spec doesn't order.

### `invariant_risk`

A rule's `ensures` clause could produce a state that violates a declared invariant. The `requires` clause may not prevent it.

**Ask whether to guard or revise.** "The spec says at most one candidate per role can be hired, but the hiring rule doesn't prevent a second hire if the role hasn't been marked as filled. Should we add a guard that checks the role is still open, or is the invariant too strict?"

The finding's evidence shows the mechanism — how the ensures clause is inconsistent with the invariant. Use this to suggest a specific fix rather than asking an open-ended question.

## Choosing which finding to present

When `analyse` returns multiple findings, pick the most relevant one. Apply these criteria in order:

1. If a finding chains into another (a `dead_transition` caused by a `missing_producer` caused by an `unreachable_trigger`), present the root cause first — even if it's in a different entity. Frame it in terms of its effect on the entity the user is working on.
2. If the user is working on a specific entity, pick a finding that affects that entity.
3. If the user just added a rule, pick a finding related to that rule's data flow.
4. If the user asked about completeness, pick the highest-impact finding first — deadlocks before broken data flow chains, broken chains before unreachable triggers.

A `deadlock` or `invariant_risk` finding indicates the spec may be structurally unsound. Surface these before continuing to build on the affected entity — adding more rules to a deadlocked lifecycle compounds the problem. Other finding types (`missing_producer`, `unreachable_trigger`, `dead_transition`, `conflict`) are gaps worth resolving but don't necessarily block further work.

Present one finding at a time. Let the user resolve it before surfacing the next. If `analyse` returns more than five or six findings, present the most impactful two or three individually, then summarise the rest by category: "There are also three unreachable triggers and two missing producers — would you like to work through those, or focus on something else?"
