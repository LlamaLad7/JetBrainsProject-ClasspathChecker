# Classpath Checker

This is a small program which determines, given a main class and a list of jars, whether all the direct and indirect
dependencies of the main class are present.

## Usage

```bash
$ ./gradlew build
$ java -jar build/libs/classpath-checker-1.0-SNAPSHOT-all.jar --help
Usage: java -jar classpath-checker.jar [<options>] <mainclass> [<jars>]...

Options:
  --verbose   Enables detailed logging of all considered classes
  -h, --help  Show this message and exit

Arguments:
  <mainclass>  Fully-qualified main class name
  <jars>       JAR files which make up the classpath you want to check
```

See also
the [unit tests](https://github.com/LlamaLad7/JetBrainsProject-ClasspathChecker/blob/main/src/test/kotlin/com/llamalad7/classpathchecker/tests/ClasspathCheckerTests.kt).

By default, the program will output a single `true`/`false` value indicating whether the classpath is complete.

E.g.

```bash
$ java -jar build/libs/classpath-checker-1.0-SNAPSHOT-all.jar com.jetbrains.internship2024.ClassB test-data/build/libs/ModuleB-1.0.jar
false
```

If you enable `--verbose`, more information will be printed, e.g.:

```bash
$ java -jar build/libs/classpath-checker-1.0-SNAPSHOT-all.jar com.jetbrains.internship2024.ClassB test-data/build/libs/ModuleB-1.0.jar --verbose
com.jetbrains.internship2024.ClassB is present in test-data/build/libs/ModuleB-1.0.jar
java.lang.Object is present in current JDK
java.lang.String is present in current JDK
java.lang.Exception is present in current JDK
com.jetbrains.internship2024.ClassA is missing
false
```

Note that the search will stop as soon as any missing class is found.

## Implementation

For the CLI I use [Clikt](https://ajalt.github.io/clikt/), and for the implementation I use [ASM](https://asm.ow2.io/).
> Unfortunately, since ASM abstracts away the constant pool and attributes, we end up needing to search more deeply than
> is strictly necessary. There are other bytecode libraries, like [BCEL](https://github.com/apache/commons-bcel), which
> provide direct access to the constant pool, however unfortunately this would require a lot of extra manual work
> because a lot of bytecode structures store type names inside raw `CONSTANT_Utf8_info`s, which we can't assume contain
> actual type information, so we would need to check a lot of different `Attribute`s ourselves to find the relevant type
> references. The performance of this approach would nonetheless be much better than we get with ASM, but I deemed the
> manual workload to be too much for this test
> task.

We delegate most of the work to an ASM `ClassRemapper`, which conveniently allows us to visit every type reference in
each class file. Unfortunately we need to provide a backing `DummyClassVisitor` implementation which provides
implementations of each sub-visitor (`FieldVisitor`, `MethodVisitor`, etc), otherwise the remapper will skip some
information.

We then continue to scan the referenced classes in a depth-first manner, since this is the simplest implementation.

We also need to consider built-in JDK classes, which we detect by looking at the resources available via the the current
`ClassLoader.getPlatformClassLoader()`. We do not scan JDK classes since we can assume the JDK to be complete, but we
need to check that the classes actually exist in case of e.g. mismatching JDK versions.

## Considerations for the future

- In practice there are some references which the JVM may not look at, e.g. invisible annotations, but
  the specifics of this will likely depend on the JVM implementation and our overall goal, e.g. we may care about the
  compile classpath rather than the runtime classpath.
- If our goal was to find all missing dependencies then instead of terminating when we find a single missing class,
  we would want to accumulate all missing classes and possibly group their packages to display to the user. Given the
  spec requires only a single boolean output, I favoured the early exiting for this implementation.
