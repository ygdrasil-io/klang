echo "#include <Metal/Metal.h>" > metal.m
clang -E -Xclang -fmodules metal.m > metal_E.m
clang -Xclang -ast-dump=json -fsyntax-only -fmodules metal_E.m > metal.ast.json

