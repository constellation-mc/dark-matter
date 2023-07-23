## What's new:

!!! Mixpanel module has undergone a rewrite. !!!

### Analytics

* Fixed new users getting null IDs.
* ID will only be assigned if analytics are enabled.
* Now, handlers will handle messages if either analytics or crashlytics are on.
* Mixpanel module has undergone a rewrite
  * `org.json` and `mixpanel` dependencies were dropped.
  * `mixpanel` was condensed into a single MixpanelAPI class, which immediately sends your request.
  * `MessageProvider` no longer requires you to return anything.

### Danger

* Danger will no longer auto-start with the game.
  * You no longer need `= "dark-matter-danger:instrumentation"` in your `fabric.mod.json
