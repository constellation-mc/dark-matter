## What's new:

### Analytics


### Content

* `FabricEntityTypeBuilder` and `FabricBlockEntityTypeBuilder` can now be used in `RegistryUtil`. You need to bring your own Fabric API.
* `ItemGroupHelper` now prefers the Fabric API event when FAPI is loaded. (1.19.3+)
* `ItemGroupHelper.InjectEntries` now provides `ItemGroup.Entries` instead of `ItemGroup.EntriesImpl`.
* Some constructors in `ContentBuilder` were public.

### Danger

* In an act of desperation, `InstrumentationAccess` will try to attach the ByteBuddy agent if the DM agent one fails.
* `InstrumentationAccess` now uses `ClassLoader#getSystemClassLoader` instead of `FabricLoader.class#getClassLoader`.

### Minecraft

* `NbtUtil` and `NbtBuilder` are now part of `minecraft`.
* `NbtUtil#writeInventoryToNbt` and `NbtUtil#readInventoryFromNbt` now accept custom keys.

### Mirage

* The init mixin is no longer required.
* * The game will hang for a bit if this mixin fails to apply.

### Glitter

* Particles no longer wait for resource init before ticking.
