## What's new:

### Major breaking changes!!!

Split API/Impl in all modules. A lot of package changes.

### Content

* `FabricEntityTypeBuilder` and `FabricBlockEntityTypeBuilder` can now be used in `RegistryUtil`. You need to bring your own Fabric API.
* `ItemGroupHelper.InjectEntries` now provides `ItemGroup.Entries` instead of `ItemGroup.EntriesImpl`.
* Some constructors in `ContentBuilder` were public.
* Non-prefixed methods in interfaces will be removed soon.

### Danger

* In an act of desperation, `InstrumentationAccess` will try to attach the ByteBuddy agent if the DM agent one fails.
* `InstrumentationAccess` now uses `ClassLoader#getSystemClassLoader` instead of `FabricLoader.class#getClassLoader`.

### Enums

* `dark_matter$extendEnum` now checks if identical constants exist and returns the old one if that's the case.

### Minecraft

* `NBTUtil` has been renamed to `NbtUtil`.
* `NbtUtil` and `NbtBuilder` are now part of `minecraft`.
* `NbtUtil#writeInventoryToNbt` and `NbtUtil#readInventoryFromNbt` now accept custom keys.

### Mirage

* Public instances are available in `Mirage`.
* The init mixin is no longer required.
* * The game will hang for a while if this mixin is not applied.

### Glitter

* Particles no longer wait for resource init before ticking.

### Recipe Book

* `RecipeBookHelper#createCategory` now only accepts identifiers. 
* Added `createGroup` to `RecipeBookHelper`.
* Non-prefixed methods in interfaces will be removed soon.
