## What's new:

This a large breaking release with lots of fixes, improvements and removals.

Most modules now depend on Fabric API modules.

* Removed static field access in most places.
* Sources jar is now delomboked.

### Content

* Removed module.

### Danger -> Instrumentation

* Renamed Danger to Instrumentation.

### Item Group

* Moved all Item Group utils to this new module.

### Mixin

* Added module.
* Moved all mixin related utils from Base to this module.
* Added VirtualMixins as a way to add mixins at runtime.
* Removed ExtendedPlugin and ShouldApply plugin-plugin.
* Deprecated `@ConstructDummy`.
* Added `owner` parameter to `IPluginPlugin#onLoad`.

### Data

* Added module.
* Moved ExtraCodecs and PersistentStateHelper here.
* Added "safe" variants of vanilla optional and either codecs.
* Added a new non-static data loader API based on the Fabric Resource Loader API.
* Added JsonCodecDataLoader.

### Base

* Moved all mixin related utils to Mixin.
* Improved Support.
* Removed mirror methods from Utilities.
* Moved Tuple to a separate interface, which is implemented by ImmutableTuple and MutableTuple.
* Removed most reflection hacks, leaving some minor things in UnsafeUtils.
* Added keys to Context.
* Added context to ConfigManager. Allows to supply a custom GSON instance.
* Renamed MathStuff to MathUtil.
* getCaller{Class/Name}(int) now return an optional.
* Removed BadCrypt and Lazy.

### Minecraft

* Added AfterFirstReload event.

### Recipe Book

* Moved group lookup to a Fabric event.
* Removed deprecated methods.

