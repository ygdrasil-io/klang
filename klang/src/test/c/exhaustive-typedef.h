
typedef struct {
    int x;
} Struct1;
struct Struct2{
    int x;
};
enum Enum1 {
  Value1 = 0x2,
};
typedef enum  {
  Value2 = 0x1
} Enum2;

typedef int IntAlias;
typedef int* IntPtrAlias;
typedef int** IntPtrPtrAlias;
typedef int*** IntPtrPtrPtrAlias;
typedef int (*IntPtrArrayAlias)[10];
typedef int (*IntFuncAlias)(int);
typedef Struct1* Struct1PtrAlias;
typedef struct Struct2* Struct2PtrAlias;
typedef enum Enum1* Enum1PtrAlias;
typedef Enum2* Enum2PtrAlias;
typedef int* IntPtrArray5Alias[5];

typedef const int* ConstIntPtrAlias;
typedef const int** ConstIntPtrPtrAlias;
typedef const int*** ConstIntPtrPtrPtrAlias;
typedef const int (*ConstIntPtrArrayAlias)[10];
typedef const int (*ConstIntFuncAlias)(const int);
typedef const Struct1* ConstStruct1PtrAlias;
typedef const struct Struct2* ConstStruct2PtrAlias;
typedef const enum Enum1* ConstEnum1PtrAlias;
typedef const Enum2* ConstEnum2PtrAlias;
typedef const int* ConstIntPtrArray5Alias[5];

typedef unsigned int UIntAlias;
typedef unsigned int* UIntPtrAlias;
typedef unsigned int** UIntPtrPtrAlias;
typedef unsigned int*** UIntPtrPtrPtrAlias;
typedef unsigned int (*UIntPtrArrayAlias)[10];
typedef unsigned int (*UIntFuncAlias)(unsigned int);

typedef const unsigned int ConstUIntAlias;
typedef const unsigned int* ConstUIntPtrAlias;
typedef const unsigned int** ConstUIntPtrPtrAlias;
typedef const unsigned int*** ConstUIntPtrPtrPtrAlias;
typedef const unsigned int (*ConstUIntPtrArrayAlias)[10];
typedef const unsigned int (*ConstUIntFuncAlias)(const unsigned int);
