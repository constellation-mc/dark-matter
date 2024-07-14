## What's new:

### Base

* Added `Result` similar to that of Rust's.
* Added wrappers for regular functional interfaces to `Exceptions`.
* Added (experimental) `*AsResult` methods to `Exceptions`. These methods do not have a generic exception type.

```java
//...
var r = Exceptions.supplyAsResult(() -> Class.forName("java.util.EmptyStackException"))
        .mapErr(Exceptions::wrap).mapVal(Class::getFields);
if (r.error().isPresent()) throw r.error().orElseThrow();
//...
```

* Deprecated `whenAvailable` with a class parameter. The same effect can be achieved by using `<>`.
* Added `getCallerFrame` to `Utilities`.

### Mixin

* Added `IMixinPredicate` to `@MixinPredicate`. This allows specifying custom predicates for each mixin.