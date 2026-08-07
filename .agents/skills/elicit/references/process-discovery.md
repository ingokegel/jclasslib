# Process discovery

Use these techniques when the user hasn't articulated the process yet, when they're starting from scratch, or when you need to understand the shape of a system before getting into construct-level detail.

## Let the user talk first

Before imposing any Allium structure, let the user describe the process in their own words. Don't interrupt for entity types, field names or state transitions. Capture the raw description, then organise it into constructs afterward. If the description becomes unclear or contradictory, ask a brief clarifying question, but don't redirect into Allium constructs yet.

Prompt with: "Tell me about this system. What does it do?" or "Walk me through the main thing that happens, start to finish."

## Use past tense

When the user struggles to articulate a process in the abstract, switch to past tense. Recalling what happened is easier than prescribing what should happen.

"Tell me about the last time someone was hired at your company" produces richer material than "describe the hiring process." Follow up with "and then what happened?" to walk the timeline. The events become rule triggers, the actors become actors, the decisions become guards.

## Start from outcomes

Most people can name what they're trying to achieve before they can describe how they get there. Ask about the destination before asking about the route.

"What does success look like for this process?" or "When this process finishes well, what's the result?" The answer gives you the terminal states. Then work backward: "What has to happen before that? And before that?"

If there are multiple outcomes (hired vs rejected, fulfilled vs refunded), capture them all. They define the shape of the transition graph.

## Find the walking skeleton

Once you have a rough sense of the process, ask: "If we could only build one path through this, what would it be? The simplest journey from start to finish."

The answer is the happy path — the coarse spec. Entities with transition graphs showing the main flow. Everything else (exception paths, alternative flows, edge cases) is added incrementally.

Once you have the skeleton, write it as a coarse Allium spec (entities with transition graphs, actors, open questions) and describe it back to the user in domain language for validation — don't present raw syntax. The skeleton is the transition from free-form discovery to formalisation.

## Identify actors early

Ask "who's involved?" early in the conversation. For each actor: "What do they need to do their job?" and "What do they need to see?"

Each actor's perspective is a partial view of the process. The full process emerges from composing these views. If two actors describe the same step differently, you've found either an ambiguity or a handoff that needs clarifying.

## Layered decomposition

For complex processes, work through layers in order. Each layer surfaces a different kind of Allium construct. Ask about each layer before moving to the next.

1. **Events.** "What are the things that happen in this process?" Capture in past tense ("candidate applied", "background check completed", "offer accepted"). These become entity state transitions and rule triggers.
2. **Commands.** "For each event, what triggered it? A person doing something, or the system reacting?" Commands from people become surface `provides` actions. System reactions become rules with `becomes` or `transitions_to` triggers.
3. **Actors.** "Who issued each command? Which role or system?" Each distinct role or system becomes an actor declaration.
4. **Entities.** "Which thing in the system changed when this event happened?" Group events by the entity they affect. Each group becomes an entity with a lifecycle.
5. **Policies.** "Are there any automatic reactions — whenever X happens, Y should follow?" These become rules with chained triggers or `becomes` triggers.
6. **Information needs.** "At each decision point, what did the actor need to see to make the decision?" These become surface `exposes` and reveal data dependencies between entities.
7. **Unknowns.** "Is there anything here you're not sure about, or where different people would give different answers?" These become `open_questions`.

This layered approach produces a richer set of constructs than open-ended conversation. Use it when the process involves multiple actors, crosses entity boundaries, or when the user gives detailed but unstructured descriptions that need organising. For processes with a single actor and a straightforward lifecycle, the techniques above (outcomes-first, walking skeleton) are sufficient.

## What to capture

Whether using layered decomposition or open-ended discovery, note:

- **Events** (things that happen) → entity state transitions, rule triggers
- **Actors** (people or systems involved) → actor declarations
- **Decisions** (choices someone makes) → rule guards, alternative transitions
- **Information needs** ("they need to see X to decide") → surface exposes, data dependencies
- **Outcomes** (what success and failure look like) → terminal states
- **Unknowns** ("I'm not sure how that works") → open questions

Before finding the walking skeleton, capture as prose notes or simple bullet lists ("Candidate applied → recruiter screened → interviews happened → decision made"). Don't use Allium syntax yet. After the skeleton is clear, organise into Allium constructs and describe the result back to the user in domain terms for correction.

## When to stop

Process discovery is complete when you can write the walking skeleton: you know the main entities, their lifecycle states, the actors involved and the terminal outcomes. You don't need every detail — that's what later phases provide. If you have enough to write a coarse spec with transition graphs and open questions, move on.
