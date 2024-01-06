## What's new:

The `analytics` module is no more! It's been replaced by `crash-handler`, which can only upload crash reports and logs.

### Analytics

* Removed module

### Base

* Added `Path` to `ConfigManager` interfaces.
* `VarHandle` methods from `MiscReflection` should now work on Java 22
* Removed `@Deprecated(forRemoval = true)` methods.

### Content

* Removed `@Deprecated(forRemoval = true)` methods.

### Crash Handler

* Module added.

### Minecraft

* Removed `WorldUtil`.
* Fixed generic type on `getOrCreate(ServerWorld, Supplier<T>, String)`

### Recipe Book

* Removed `@Deprecated(forRemoval = true)` methods.
* Calls to `ImmutableList.of` and `ImmutableMap.of` are now automatically wrapped with mutable alternatives.