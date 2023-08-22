#clang -Xclang -ast-dump -fsyntax-only -fmodules -fdiagnostics-color=never ./class.m > ./class.m.ast
#clang -Xclang -ast-dump=json -fsyntax-only -fmodules ./class.m > ./class.m.ast.json
#clang -Xclang -ast-dump -fsyntax-only -fmodules -fdiagnostics-color=never ./nsenum.m > ./nsenum.m.ast
#clang -Xclang -ast-dump=json -fsyntax-only -fmodules ./nsenum.m > ./nsenum.m.ast.json
#clang -Xclang -ast-dump -fsyntax-only -fmodules -fdiagnostics-color=never ./protocol.m > ./protocol.m.ast
#clang -Xclang -ast-dump=json -fsyntax-only -fmodules ./protocol.m > ./protocol.m.ast.json
#clang -Xclang -ast-dump -fsyntax-only -fmodules -fdiagnostics-color=never ./category.m > ./category.m.ast
#clang -Xclang -ast-dump=json -fsyntax-only -fmodules ./category.m > ./category.m.ast.json

clang -Xclang -ast-dump -fsyntax-only -fmodules -fdiagnostics-color=never ./test.m > ./test.m.ast

#/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/System/Library/Frameworks/
#docker run --mount src="/Users/chaos/Workspace/klang2/./klang/sample/objective-c",target=/workspace,type=bind \
#  --mount src="/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/System/Library/Frameworks",target=/workspace/frameworks,type=bind \
#  ubuntu-clang-16 ls /workspace/frameworks/
#docker run --rm --mount src="/Users/chaos/Workspace/klang2/./klang/sample/c",target=/workspace,type=bind ubuntu-clang-16 clang-16 -Xclang -ast-dump -fsyntax-only -I /workspace /workspace/typedef.h
#TODUMP="/workspace/class.m"
#docker run --rm --mount src="/Users/chaos/Workspace/klang2/./klang/sample/objective-c",target=/workspace,type=bind \
#  --mount src="/Applications/Xcode.app//Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include",target=/workspace/include,type=bind \
#  --mount src="/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/System/Library/Frameworks",target=/workspace/frameworks,type=bind \
#  ubuntu-clang-16 clang-16 -Xclang -ast-dump -ObjC -fsyntax-only -fmodules -I /workspace/include -I /workspace -F /workspace/frameworks -Wno-typedef-redefinition $TODUMP