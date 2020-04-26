## Kotlin Visibility Modifiers

Are you missing package private visibility in kotlin?

This repository is to provide `PackagePrivate` and `FilePrivate` annotations
as well as necessary lint checks. 

`@PackagePrivate` annotation is a replacement for java package-private visibility. 
`@FilePrivate` is a new thing for java/kotlin. As the name says annotatated members 
can be accessed in the same file they are declared.

### Usage

Add dependency to you `build.gradle`:

`implementation("io.github.esentsov:kotlin-visibility:1.1.0")`

Use annotations `@PackagePrivate` and `@FilePrivate` in your code.
All necessary lint checks are already included, Android Studio will pick them up
and show an error every time you are trying to access annotated members outside of the respective scope.

### Release notes

#### 1.1.0
Add support for class-level annotations. Detect usage of annotated classes, objects, annotation classes.
Also detect usage of function, properties or inner classes of an annotated class.

#### 1.0.0
Initial release. Support detections of annotated constructors, properties and functions.
