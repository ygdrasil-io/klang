# Klang Toolkit

*Experimental toolkit to generate bindings from C/C++/Objective-C library to Kotlin using Clang.*

## Status Overview

Currently, we are in the experimental phase of parsing headers. We have two main approaches:

### Approach 1: Standalone LibClang 15 Library

- **Advantages**
    - More accurate header parsing.
- **Disadvantages**
    - LibClang is part of the LLVM toolchain.

### Approach 2: Clang 15 from Docker for JSON AST Generation

- **Advantages**
    - Easier to use.
- **Disadvantages**
    - Limited information on JSON AST.
    - Requires Docker on host for JSON AST generation.
    - Hard to use OS-specific headers.

We can generate a good enough binding with JNA on a C library. Check the `bindings` folder for examples.

## Known Issues

Currently, with Gradle, the native library is loaded multiple times on the daemon. Therefore, the Klang plugin does not
support the Gradle daemon.

## Additional Information

The `Jextract` code is embedded almost as is, and the license may differ from Klang toolkit.

This project embeds C headers from different sources; namely:

1. `/klang/src/main/resource/darwin-headers.zip` is obtained from XCode. Refer to `/usr/bin/xcrun --show-sdk-path`.
2. `/klang/src/main/resource/c-headers.zip` is obtained from Clang 15 headers.

Clang dynamic libraries (version 15) are embedded. These are fetched
from [prebuilt version on GitHub](https://github.com/klang-toolkit/libclang-binary/releases/tag/15).