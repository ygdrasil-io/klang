#clang -E -Xclang -fmodules foundation.m > foundation.e.m
#clang -Xclang -ast-dump=json -fsyntax-only -fmodules ./foundation.e.m > ./foundation.m.ast.json
clang -E -Xclang -fmodules cocoa.m > cocoa.e.m
clang -Xclang -ast-dump=json -fsyntax-only -fmodules ./cocoa.e.m > ./cocoa.m.ast.json

