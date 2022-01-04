# SpeedrunPractice
A mod designed to streamline the practicing process for minecraft speedrunners. Includes end practice, post blind practice, nether practice, and overworld practice, as well as the ability to change structure generation settings like nether structure region size and what bastion types can spawn.

# Settings
The settings menu can be opened by pressing the button marked "Speedrun Practice Options" in the options menu. The settings are saved to config/speedrun-practice.json.

# Commands

/instaperch  will cause the dragon to enter perch phase.

/practice end <seed> - teleports the player to the end dimension with the given seed, or a random seed if no seed is given.

/practice nether <seed> - teleports the player to the nether dimension of a new world with the given seed, or a random seed if no seed is given.

/practice postblind <maxDist> <seed> - teleports the player to a random location at least maxDist away from a stronghold in a new world with the given seed, or a random seed if no seed is given. If maxDist is not given, the default max distance is used which can be changed in the settings.

/practice overworld <seed> - teleports the player to the overworld spawn of a new world with the given seed, or a random seed if no seed is given.

/practice <practiceType> inventory <slot> select - Sets the inventory slot to be used for the specified practice type.

/practice <practiceType> inventory <slot> save - Saves the current inventory to the given slot of the given practice type.

/practice seed - Display the current practice seed. If someone else sets their seed to be the same as yours then the random seeds of their practice worlds will be the same as yours. To be used if you want to race people on the same sequence of seeds without having to manually agree on a seed every time.

/practice seed <seed> - Set the practice seed

/practice world - Displays the RegistryKey of the current world
