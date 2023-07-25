clang -E -Xclang -fmodules foundation.m > foundation.e.m
clang -Xclang -ast-dump=json -fsyntax-only -fmodules ./foundation.e.m > ./foundation.m.ast.json

