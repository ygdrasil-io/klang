
enum EnumName {
  Value1 = 0x2,
  Value2 = 0x1
};

struct StructName {
    enum EnumName* field1;
    char field3;
};

char function(int *a, void* b, enum EnumName myEnum);
void* function2();
struct StructName* function3();