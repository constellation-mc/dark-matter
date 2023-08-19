## What's new:

### Important changes for Glitter.

The mod now provides Forge-style mod IDs. So, you can use either `dark-matter-content` or `dark_matter_content`.

### Glitter

* `tickLogic()` is being replaced by `tick()`.
* * In later versions `tick()` will become abstract.
* Internal methods in `AbstractScreenParticle` are now actually internal.
* `AbstractScreenParticle` no longer contains any display ticking logic.
* Removed the multiplication by `0.99` of `Particle`'s velocity.
* Screen-bounded particles will no longer flicker for a frame after their screen was closed.

### Content

* The `Building {x} ItemGroup without Fabric Item Groups` warning will only get raised if `fabric-item-groups-v0` is not present. (<=1.19.2)
