## What's new:

### Base

* Added `Result` similar to that of Rust's.
* Deprecated `whenAvailable` with a class parameter. The same effect can be achieved by using `<>`.
* Added `getCallerFrame` to `Utilities`.
* Added wrappers for regular functional interfaces to `Exceptions`.

### Mixin

* Added `IMixinPredicate` to `@MixinPredicate`. This allows specifying custom predicates for each mixin.