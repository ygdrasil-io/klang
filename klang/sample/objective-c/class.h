@interface TestClass : NSObject

@property NSString *testProperty;

 - (void)testMethod;
 + (BOOL)testMethod:(NSString *)parameter withParameter:(NSString *)testParameter;

@end