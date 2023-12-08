## What's new:

### Base

* Added `ofTrusted` to Generic(Field/Method/Constructor). This will construct a wrapper using `IMPL_LOOKUP` of MethodHandles.Lookup.

### Config

* Fixed `save` not being passed to `load()`.
* `processOptions` now returns if the config has been modified by processors.
* Added `ofField` to Option. 
* ConfigBuilder now supports adding multiple scanners and processors.
* Added simple `postLoad` and `postSave` events.

### Danger

* Removed ByteBuddyAgent in favor of `loadAgent`. This is an experiment.

### Minecraft

* Added `empty()` to TextUtil.
