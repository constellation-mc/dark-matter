## What's new:

* This release drops the `config` module in favor of a much simpler `ConfigManager` in `base`.
* While the new manager is nowhere near as "feature-packed" as `config` was, you can replicate most of `config`'s features using events.

### Base

* Added `ConfigManager`. A handler for as many configs as you want, whereever you want.
* Moved unchecked `run`, `consume`, `supply`, `process` to `Exceptions`.
* Added `*InHierarchy` to `Reflect` for methods an fields.
* Added `Context`.
* Added `share` to `Support`.