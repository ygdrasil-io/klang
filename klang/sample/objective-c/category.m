#import <Foundation/Foundation.h>

@interface MyClass : NSObject

@end

@interface MyClass (MyCategory)
// Déclarations des nouvelles méthodes que vous voulez ajouter à MaClasse
- (void)newMethod;
@end