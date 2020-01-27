package world.gregs.game.playground

/*

    Waypoint map
        Each waypoint link has a base weight (tiles to destination or heuristic) plus a value calculated for each individual entity to allow for requirements (quest completed, level requirements, 30min cooldown by multiply by 0), or cost (100gp * entity characteristics)

    What about resting/listening to music? (If link intersects radius of musician then decrease weight?)

    Path/route
    1) Decide on a target
    2) Traverse to that target
        Considerations:
            Shortcuts (weighted)
            Large map
            Multiple entities
            Fixed targets
    Traversal
        Obstacles
            Doors
            Gates
            Agility shortcuts
        Methods
            Walk
            Run
            Tele
            Boat
        Targets
            Bank
            Activity location
                Skill
                Quest
                etc...
            Shop
 */