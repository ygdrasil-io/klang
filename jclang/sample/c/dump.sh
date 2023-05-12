clang -Xclang -ast-dump=json -fsyntax-only -I./ ./typedef-enum.h > ./typedef-enum.h.ast.json
clang -Xclang -ast-dump=json -fsyntax-only -I./ ./enum.h > ./enum.h.ast.json
clang -Xclang -ast-dump=json -fsyntax-only -I./ ./struct.h > ./struct.h.ast.json