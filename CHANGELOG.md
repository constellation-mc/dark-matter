## What's new:

### Mirage

* Fixed the `Registry worlds_together:worldgen/betterx/biome not found` crash with BCLib.
  * More specifically, we now use `getOptionalWrapper` instead of `getWrapperOrThrow`.