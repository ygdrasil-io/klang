#import <Foundation/Foundation.h>

@interface TestClass : NSObject <NSCopying>

@property (nonatomic, assign) NSString *testProperty;

 - (void)testMethod;
 + (BOOL)testMethod:(NSString *)parameter withParameter:(NSString *)testParameter;

@end