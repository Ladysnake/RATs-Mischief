# Rat's Mischief - Changelog:

### Rat's Mischief 1.1: When Rats'll Fly Update - 1.16.5
- Added the Mask of Rat
    - Grants a bit of protection as well as all enchantments present on it to your rats when their owner has one equipped 
    - TODO Makes the user share their status effects (positive and negative) with all their tamed 
    - Can be repaired with Phantom Membrane
    - Will always drop when a player kills doctor4t
- Tamed rats now drop what they hold upon dying
- Rats will now determine what blocks they can break by checking for hardness instead of resistance
    - Rats can now mine blocks that have a hardness of at most 1
- Rats affected by Strength are now able to mine blocks that have a hardness of 1 additional hardness per Strength level.
    - Rats without Strength are able to mine blocks that don't require tools and have a hardness of 1 or lower
    - Rats under Strength I are able to mine blocks that have a hardness of 2 or lower, for instance tree logs and stone
    - Rats under Strength II are able to mine blocks that have a hardness of 3 or lower, for instance ores
- Furthermore, rats affected by Haste will mine 20% faster per Haste level
- Rats can now mine blocks that are under the glass material category
- Rats can now mine blocks even if they require a tool
- TODO Rats will now pick up items from the ground if they can add it to their held stack
- Introducing Elytrats (and flying rats)! Elytrats can be equipped on rats to allow them to fly.
    - Rats equipped with Elytrats will fly whenever they need to fight a foe
    - Crafted from a firework rocket, 4 phantom membranes and a piece of leather
    - Shift right click on a rat with an Elytrat in hand to make the rat equip it, shift right click with an empty hand on a rat wearing an Elytrats to make it unequip it

### Rat's Mischief 1.0.3 - 1.16.5
- Added compatibility with Eldritch Mobs so eldritch rats can naturally spawn
- Reduced the range check for villages when spawning rats

### Rat's Mischief 1.0.2 - 1.16.5
- Fixed a bug that caused rats to not properly check for spawn conditions, leading to rats spawning whenever beds are present
- Fixed a crash that would occur when rats are trying to bring back an item but their owner is dead
- Fixed a crash that would occur when an order is given with a staff but the owner isn't present

### Rat's Mischief 1.0.1 - 1.16.5
- Rats no longer attack cats, but just chase them for fun
- Rats will no longer drop items if their owner's inventory is full
- Rats are no longer able to drink splash and lingering potions

### Rat's Mischief 1.0 - 1.16.5
- Added rats, with 8 color variants (albino, black, grey, husky, chocolate, light brown, russian blue, and the very rare gold rat)
- Added 6 name cosmetic variants (Remy, doctor4t, Ratater, Jorato, Jerma, and 16 dye variants for Rat Kid)
- Added Leather, Twisted and Purpur Rat Pouches, that can be used to respectively carry 5, 10 and 20 rats
- Added the Harvest, Collection and Love Rat Staffs
- Added "A Rat in Time" (pixel art version) custom painting, original made by @AsterofSubcon on Twitter