## Dark Matter

A small collection of random might-be-useful utils and patches.

[![](https://jitpack.io/v/me.melontini/dark-matter.svg)](https://jitpack.io/#me.melontini/dark-matter)

### Want to use this in your project?

` for some reason `

First, add JitPack to your repositories.

```groovy
repositories {
    maven {
        url 'https://jitpack.io'
    }
} 
```

and then

Since `0.3.0` Dark Matter uses modules.

```groovy
dependencies {
    //other dependencies...

    //dark-matter-base is required by all modules.
    modImplementation include("me.melontini.dark-matter:dark-matter-base:${project.dark_matter}")
    //or com.github.melontini.dark-matter

    //if you need dark-matter-recipe-book, you also have to include its dependencies.
    //such as dark-matter-enums
    modImplementation include("me.melontini.dark-matter:dark-matter-recipe-book:${project.dark_matter}")
    modImplementation include("me.melontini.dark-matter:dark-matter-enums:${project.dark_matter}")

    //alternatively, you can include all modules like this:
    modImplementation include("me.melontini.dark-matter:dark-matter:${project.dark_matter}")

    //other dependencies...
}
```

You can find all the tags and modules here: https://jitpack.io/#me.melontini/dark-matter