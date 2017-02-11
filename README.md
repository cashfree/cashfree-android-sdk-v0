# CashfreeSDK_Android

Welcome to CashfreeSDK repository. This repo hosts both the Android Library of CashfreeSDK as well as a Sample App to get you started.

The Detailed documentation is available [here](http://docs.gocashfree.com/docs/v1/).
 
The CashfreeSDK is bundled as a AAR. More information at https://developer.android.com/studio/projects/android-library.html


Here are steps to import the CashfreeSDK into your Android App with Android Studio version 2.2:

1. Get the cashfreeSDK.aar file by cloning this repository.
2. Go to File -> New -> New Module..
![New Module](/images/CFSDK_NewModule.png)
3. Select Import .JAR/.AAR Package
4. In the next dialog box navigate to cashfreeSDK.aar
5. Once you click Finish cashfreeSDK module will be imported, however, you need to add it as a dependency to your project before it can be used
6. To add dependency on CashfreeSDK go to File -> Project Structure. 
7. In the dialog box on the left find your app module and select it.
8. Select dependencies and click the green + symbol near the top-right and select Module Dependency
![Select Module](/images/CFSDK_SelectModule.png)
9. Finally select "cashfreeSDK" and click OK. You should be good to go.



