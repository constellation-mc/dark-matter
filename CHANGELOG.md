## What's new:

### Analytics

* Fixed new users getting null IDs.
* ID will only be assigned if analytics are enabled.
* Now, handlers will handle messages if either analytics or crashlytics are on.

### Danger

* Danger will no longer auto-start with the game.
  * You no longer need `= "dark-matter-danger:instrumentation"` in your `fabric.mod.json
