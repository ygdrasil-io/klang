
enum EnumName {
  Value1 = 0x2,
  Value2 = 0x1
};

typedef enum  {
  Value3 = 0x2,
  Value4 = 0x1
} EnumName2;

typedef struct {
    enum EnumName* field1;
    EnumName2 field2;
    char field3;
} StructName;

typedef struct {
    StructName field1;
    StructName* field2;
    char field3;
} StructName2;