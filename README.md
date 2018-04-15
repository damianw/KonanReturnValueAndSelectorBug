KonanReturnValueAndSelectorBug
==============================

(I don't have enough knowledge of the subject to come up with a good name.)

This project illustrates a bug with kotlin-native when platform types are exposed
in a Framework API (though that may have nothing to do with the problem, I'm not sure).

This project assumes that the latest version of the `kotlin-native` repository to
build with is present in the parent directory to this project. To customize,
see `gradle.properties`.

### Relevant Code

Common-module `Example.kt`:
```Kotlin
package com.github.damianw.konanreturnvalueandselectorbug

expect class Decimal

expect operator fun Decimal.plus(augend: Decimal): Decimal
expect operator fun Decimal.minus(augend: Decimal): Decimal

expect object Decimals {

    val ZERO: Decimal

}

data class Product(val name: String, val price: Decimal)

data class ShoppingCart(val products: List<Product>) {

    fun firstPrice(): Decimal {
        return products.first().price
    }

    fun computeTotalCost(): Decimal {
        var sum: Decimal = Decimals.ZERO
        for (product in products) {
            sum += product.price
        }
        return sum
    }

}
```

iOS-module `Example.kt`:
```Kotlin
package com.github.damianw.konanreturnvalueandselectorbug

import platform.Foundation.*

@Suppress("CONFLICTING_OVERLOADS")
actual typealias Decimal = NSDecimalNumber

actual operator fun Decimal.plus(augend: Decimal): Decimal = decimalNumberByAdding(augend)
actual operator fun Decimal.minus(augend: Decimal): Decimal = decimalNumberBySubtracting(augend)

actual object Decimals {

    actual val ZERO = Decimal("0.0")

}
```

Usage in sample app `ViewController.swift`:
```Swift
override func viewDidLoad() {
    super.viewDidLoad()
    let shoppingCart = KRVASBShoppingCart(products: [
        KRVASBProduct(name: "Apples", price: 2.0),
        KRVASBProduct(name: "Oranges", price: 3.0)
    ])
    let firstPrice = shoppingCart.firstPrice()
    let totalCost = shoppingCart.computeTotalCost()
    NSLog("%s", totalCost)
}
```

### Build & Run

Build `KonanReturnValueAndSelectorBug.framework` for the iPhone Simulator:
```
./gradlew compileKonanKonanReturnValueAndSelectorBugIphone_sim
```

Then open, build, and run the XCode project found in `sampleapp`.

### Error

`computeTotalCost` causes the following error:
```
2018-04-15 14:58:35.709260-0700 KonanReturnValueAndSelectorBugSampleApp[85029:9062142] -[__NSCFNumber decimalNumberByAdding:]: unrecognized selector sent to instance 0xb000000000000005
2018-04-15 14:58:35.716434-0700 KonanReturnValueAndSelectorBugSampleApp[85029:9062142] *** Terminating app due to uncaught exception 'NSInvalidArgumentException', reason: '-[__NSCFNumber decimalNumberByAdding:]: unrecognized selector sent to instance 0xb000000000000005'
*** First throw call stack:
(
	0   CoreFoundation                      0x00000001049171e6 __exceptionPreprocess + 294
	1   libobjc.A.dylib                     0x0000000101399031 objc_exception_throw + 48
	2   CoreFoundation                      0x0000000104998784 -[NSObject(NSObject) doesNotRecognizeSelector:] + 132
	3   CoreFoundation                      0x0000000104899898 ___forwarding___ + 1432
	4   CoreFoundation                      0x0000000104899278 _CF_forwarding_prep_0 + 120
	5   KonanReturnValueAndSelectorBug      0x0000000100bdd469 platform_Foundation_kniBridge3922 + 57
	6   KonanReturnValueAndSelectorBug      0x0000000100bdcf37 kfun:platform.Foundation.objcKniBridge3750(konan.internal.NativePtr;konan.internal.NativePtr;platform.Foundation.NSDecimalNumber)platform.Foundation.NSDecimalNumber + 151
	7   KonanReturnValueAndSelectorBug      0x0000000100b08a76 kfun:com.github.damianw.konanreturnvalueandselectorbug.plus@platform.Foundation.NSDecimalNumber.(platform.Foundation.NSDecimalNumber)platform.Foundation.NSDecimalNumber + 86
	8   KonanReturnValueAndSelectorBug      0x0000000100b08938 kfun:com.github.damianw.konanreturnvalueandselectorbug.ShoppingCart.computeTotalCost()platform.Foundation.NSDecimalNumber + 360
	9   KonanReturnValueAndSelectorBug      0x0000000100b07d94 kfun:com.github.damianw.konanreturnvalueandselectorbug.Product.equals(kotlin.Any?)ValueType + 772
	10  KonanReturnValueAndSelectorBugSampleApp 0x00000001008208e4 _T039KonanReturnValueAndSelectorBugSampleApp14ViewControllerC11viewDidLoadyyFTo + 36
	11  UIKit                               0x0000000101dd9191 -[UIViewController loadViewIfRequired] + 1215
	12  UIKit                               0x0000000101dd95d4 -[UIViewController view] + 27
	13  UIKit                               0x0000000101ca7183 -[UIWindow addRootViewControllerViewIfPossible] + 122
	14  UIKit                               0x0000000101ca7894 -[UIWindow _setHidden:forced:] + 294
	15  UIKit                               0x0000000101cba62c -[UIWindow makeKeyAndVisible] + 42
	16  UIKit                               0x0000000101c2e43a -[UIApplication _callInitializationDelegatesForMainScene:transitionContext:] + 4739
	17  UIKit                               0x0000000101c3362b -[UIApplication _runWithMainScene:transitionContext:completion:] + 1677
	18  UIKit                               0x0000000101ff5e4a __111-[__UICanvasLifecycleMonitor_Compatability _scheduleFirstCommitForScene:transition:firstActivation:completion:]_block_invoke + 866
	19  UIKit                               0x00000001023c8909 +[_UICanvas _enqueuePostSettingUpdateTransactionBlock:] + 153
	20  UIKit                               0x0000000101ff5a86 -[__UICanvasLifecycleMonitor_Compatability _scheduleFirstCommitForScene:transition:firstActivation:completion:] + 236
	21  UIKit                               0x0000000101ff62a7 -[__UICanvasLifecycleMonitor_Compatability activateEventsOnly:withContext:completion:] + 675
	22  UIKit                               0x00000001029674d4 __82-[_UIApplicationCanvas _transitionLifecycleStateWithTransitionContext:completion:]_block_invoke + 299
	23  UIKit                               0x000000010296736e -[_UIApplicationCanvas _transitionLifecycleStateWithTransitionContext:completion:] + 433
	24  UIKit                               0x000000010264b62d __125-[_UICanvasLifecycleSettingsDiffAction performActionsForCanvas:withUpdatedScene:settingsDiff:fromSettings:transitionContext:]_block_invoke + 221
	25  UIKit                               0x0000000102846387 _performActionsWithDelayForTransitionContext + 100
	26  UIKit                               0x000000010264b4f7 -[_UICanvasLifecycleSettingsDiffAction performActionsForCanvas:withUpdatedScene:settingsDiff:fromSettings:transitionContext:] + 223
	27  UIKit                               0x00000001023c7fb0 -[_UICanvas scene:didUpdateWithDiff:transitionContext:completion:] + 392
	28  UIKit                               0x0000000101c31f0c -[UIApplication workspace:didCreateScene:withTransitionContext:completion:] + 515
	29  UIKit                               0x0000000102204a97 -[UIApplicationSceneClientAgent scene:didInitializeWithEvent:completion:] + 361
	30  FrontBoardServices                  0x0000000106f472f3 -[FBSSceneImpl _didCreateWithTransitionContext:completion:] + 331
	31  FrontBoardServices                  0x0000000106f4fcfa __56-[FBSWorkspace client:handleCreateScene:withCompletion:]_block_invoke_2 + 225
	32  libdispatch.dylib                   0x0000000104e8e848 _dispatch_client_callout + 8
	33  libdispatch.dylib                   0x0000000104e93e14 _dispatch_block_invoke_direct + 592
	34  FrontBoardServices                  0x0000000106f7b470 __FBSSERIALQUEUE_IS_CALLING_OUT_TO_A_BLOCK__ + 24
	35  FrontBoardServices                  0x0000000106f7b12e -[FBSSerialQueue _performNext] + 439
	36  FrontBoardServices                  0x0000000106f7b68e -[FBSSerialQueue _performNextFromRunLoopSource] + 45
	37  CoreFoundation                      0x00000001048b9bb1 __CFRUNLOOP_IS_CALLING_OUT_TO_A_SOURCE0_PERFORM_FUNCTION__ + 17
	38  CoreFoundation                      0x000000010489e4af __CFRunLoopDoSources0 + 271
	39  CoreFoundation                      0x000000010489da6f __CFRunLoopRun + 1263
	40  CoreFoundation                      0x000000010489d30b CFRunLoopRunSpecific + 635
	41  GraphicsServices                    0x000000010780da73 GSEventRunModal + 62
	42  UIKit                               0x0000000101c350b7 UIApplicationMain + 159
	43  KonanReturnValueAndSelectorBugSampleApp 0x0000000100821c97 main + 55
	44  libdyld.dylib                       0x0000000104f0b955 start + 1
)
libc++abi.dylib: terminating with uncaught exception of type NSException
```

A detail that might be relevant: adding a breakpoint after the evaluation of
`firstPrice` shows unexpectedly that it is not of type `NSDecimalNumber`:
```
(lldb) print firstPrice
(__NSCFNumber) $R1 = 0xb000000000000025
```
