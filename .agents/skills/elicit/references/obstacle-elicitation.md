# Obstacle elicitation

Use these techniques when exploring failure paths, timeouts, exception transitions and actor handoffs.

## Use the pre-mortem

Instead of the abstract "what can go wrong?", use a concrete framing.

"Imagine it's six months from now. This system has been built and deployed. Something has gone wrong and people are frustrated. What happened?"

People are better at imagining concrete failure than listing abstract risks. The pre-mortem produces vivid, specific failure modes rather than generic edge cases. Each failure mode maps to an exception transition, a timeout rule, or an invariant.

Follow up each failure with: "How should the system have prevented that? Or handled it?" The answer is the rule or guard that's missing from the spec.

## Ask what happens when nothing happens

At every step where a human actor needs to act, ask: "What if nobody does anything? After a day? After a week?"

The answer is one of:
- "Nothing, it just waits." This is a design decision worth making explicit. Document it as the intended behaviour, possibly with an open question about whether it's acceptable.
- "After X time, Y happens." This is a temporal trigger: `when: entity.timestamp_field + config.duration <= now` with `requires: entity.status = expected_state` to prevent re-firing. Do not use `becomes` for time-delayed behaviour — `becomes` fires immediately when an entity enters a state, not after a delay.
- "Someone should be notified." This surfaces a notification or escalation path.

Most specs underspecify inaction. The happy path assumes everyone acts promptly. Real systems have stale candidacies, expired invitations and abandoned carts. These need rules.

## Explore handoffs between actors

At every state transition, ask: "Who takes over at this point? How do they know it's their turn? What do they need to see?"

The answers reveal:
- **Actor transitions** — which actor is responsible for the next step
- **Notification needs** — how the next actor learns they need to act
- **Information requirements** — what the next actor's surface must expose
- **Related surface links** — how surfaces connect to each other

Handoffs are where processes break in practice. The outgoing actor assumes the incoming actor knows what happened. The incoming actor assumes they'll be told. The spec needs to make the handoff explicit: what triggers the notification, what information it carries, and what the next actor sees when they arrive.

## Enumerate alternatives at each step

For each step in the happy path, systematically ask: "What else could happen here?" Keep asking until the user can't think of anything more. This is the discipline that prevents gaps: stories and informal descriptions only capture the paths someone happens to think of. Enumeration forces completeness.

Work through the happy path step by step:
1. State the step: "At this point, the recruiter reviews the application."
2. Ask: "What's the main thing that happens?" (The happy path outcome — already captured.)
3. Ask: "What else could happen?" (First alternative — maybe rejection.)
4. Ask: "Anything else?" (Second alternative — maybe deferral, or requesting more information.)
5. Keep asking until exhausted.
6. For each alternative: "What happens next if this path is taken?" (Follow the alternative to its terminal state.)

Each alternative becomes either an exception transition in the graph, an additional rule, or an open question if the user isn't sure. If an alternative branches into its own multi-step flow, capture it as an open question and return to it in a later pass rather than following every branch immediately.

## Systematically test each transition

After enumeration, test each transition for robustness:
- "What if the preconditions aren't met? What should happen?"
- "Can this transition be reversed? Can someone undo it?"
- "Is there a time limit on being in this state?"
- "Can this transition happen more than once?"

For critical entities, test every transition. For less critical entities, focus on the transitions most likely to fail or stall. Critical entities are those central to the system's value proposition, those that handle money or compliance-sensitive data, or those the user mentioned during the pre-mortem. Transitions most likely to stall are those that depend on external actors or systems, those with temporal dependencies, and those where a human must act.

## What to capture

Obstacle elicitation produces:
- **Exception transitions** (screening → rejected, interview → cancelled)
- **Temporal triggers** with `requires` guards (invitation expires after 48 hours)
- **Escalation paths** (stuck candidacy → notify recruiter after 5 days)
- **Terminal error states** (background check flagged → candidacy terminated)
- **Invariants** (system-wide properties that must hold: "no candidate can have two active candidacies for the same role")
- **Open questions** for unresolved failure scenarios
