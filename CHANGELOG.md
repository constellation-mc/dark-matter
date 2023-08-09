## What's new:

### Analytics


### Content

* FabricEntityTypeBuilder and FabricBlockEntityTypeBuilder can now be used in RegistryUtil. You need to bring your own Fabric API.

### Danger

* In an act of desperation, InstrumentationAccess will try to attach the ByteBuddy agent if the DM one fails.

### Mirage

* The init mixin is no longer required.
* * The game will hang for a bit if this fails to apply.

### Glitter

* Particles no longer wait for resource init before ticking.
