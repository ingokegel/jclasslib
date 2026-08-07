# Assumption checking

Use these techniques when you have a coarse or complete spec and need to verify it matches the user's mental model. Show-back and ordering checks work on coarse specs (transition graphs without rules). Scenario traces require rules and surfaces to be defined. Actor verification works at any stage.

## Show back what you've heard

After capturing a process or a set of rules, write the spec, then describe what it says in domain language. Don't present raw Allium syntax — translate constructs into a narrative the stakeholder can validate.

"Based on what you've described, here's the lifecycle for Candidacy. Applied, then screening, then interviewing, then deciding, and from there either hired or rejected. Screening can also lead directly to rejection. Is this right?"

Let the user correct, refine and extend. Common responses:
- "Yes, but you're missing X" → add the missing transition or entity
- "Not quite — Y happens before Z" → reorder the transitions
- "What about W?" → the user remembered something they hadn't mentioned

## Test ordering assumptions

When the transition graph is taking shape, test whether the declared ordering is correct.

"Could these steps happen in a different order? What if the background check completed before screening was finished — would that change anything?"

This surfaces:
- **False ordering constraints** — steps the user assumed were sequential but could be parallel
- **Missing concurrency** — two things that can happen simultaneously but the graph forces them into sequence
- **Hidden dependencies** — steps that truly must follow a specific order, revealing data dependencies

If the user says "those could happen in either order", the transition graph may need restructuring. If they say "no, X absolutely must happen before Y", ask why — the answer is usually a data dependency that should be a `requires` clause.

## Verify actor assignments

After identifying actors and their surfaces, check the assignments.

"I have the recruiter screening candidates and the hiring manager making the final decision. Is it always the hiring manager? Could a recruiter make the decision for junior roles?"

Actor boundaries are often assumed rather than decided. Testing them reveals:
- **Role overlap** — two actors who can do the same thing, needing explicit modelling
- **Delegation** — one actor acting on behalf of another
- **Conditional assignment** — different actors for different entity states or types

## Check completeness at transition points

When moving from one entity to the next, or from happy path to edge cases, pause and check.

"Before we move on to interviews — looking at the screening flow, is there anything we haven't covered? Any situation that could come up that we haven't accounted for?"


## Verify against real scenarios

Take a concrete scenario and trace it through the spec.

"Let's say Alice applies for the Senior Engineer role on Monday. Walk me through what happens to her candidacy using the spec we've written. Does each step match what you'd expect?"

If the spec produces a different outcome than the user expects, you've found a gap. The gap might be a missing rule, a wrong guard, or an unstated assumption.
