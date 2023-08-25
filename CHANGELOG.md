## What's new:

### Breaking changes for content and notable for others.

### Base

* Introducing new and improved `ExtendablePlugin`!
  * This is a successor of `ExtendedPlugin`, but with more features!
  * One of the main feature is plugin support (Plugin-Plugins)
  * Default plugins include: `MixinPredicatePlugin`, `ShouldApplyPlugin`, `PublicizePlugin`.
  * Only MixinPredicatePlugin is on by default.
  * Plugins can be added using the inner `DefaultPlugins` class.
* Added `@MixinPredicate`. Part of `MixinPredicatePlugin`
  * This annotation succeeds `@MixinShouldApply`.
  * This also comes with a better version of `@Mod`, where you can specify a version predicate.
* Added `@Publicize`. Part of `PublicizePlugin`
  * Patches annotated fields and methods from `private` to `public`.
  * This is meant for static members which need to be accessed from the outside.
* Added `AsmUtil`.
  * This includes `mapAnnotationNode()` and `mapObjectFromAnnotation()` from `ExtendedPlugin`.
* Added `of()` methods to `Tuple` and `MutableTuple`.
* Fixed `@MixinShouldApply` skipping every second mod.

### Content

* Split api/impl in content builder.
* Added `ItemGroup` as a parameter of `AnimatedItemGroup`

### Enums

* A debug message is now logged after extending enums.

### Glitter

* The passed MatrixStack should be new in less cases. (<1.20)

### Minecraft

* ValueTracker gets a facelift!
  * Instead of using fields/classes as ids, we use actual string IDs.
  * Now you can track anything using a supplier!
  * Reflection is still supported, but wrapped in a supplier.
  * You can now add timed trackers which will disappear after the timer is up.
  * So it should actually be useful now.

### Recipe Book

* Methods in `RecipeBookHelper` were renamed to make more sense and better represent what they do.
  * Old methods are still present, but are deprecated.
  * The singular group parameter was replaced by varargs.
  * `addToSearchGroup()`, `registerGroups()` and `registerAndAddToSearch()` should have overload parity.
* Added `registerAndAddToSearch()` to `RecipeBookHelper`. Saves one line!
* Duplicate groups should now be handled better.
