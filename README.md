## Kotlin Visibility Modifiers

Are you missing package visibility in kotlin? Me too!

This repository is to provide `PackagePrivate` and `FilePrivate` annotations
as well as necessary lint checks.

### Usage

Add dependency to you `build.gradle`:

`implementation("io.github.esentsov:kotlin-visibility:1.0.0")`

Use annotations `@PackagePrivate` and `@FilePrivate` in your code.
All necessary lint checks are already included, Android Studio will pick them up
and show an error every time you are trying to access annotated members outside of the respective scope.
