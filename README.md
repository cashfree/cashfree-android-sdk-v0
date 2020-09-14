


# Cashfree SDK Integration Steps

## Step 1: Download Library

Add Cashfree maven repository to your module's build.gradle file.

```xml
 repositories { maven { url 'https://maven.cashfree.com/release'} }
 ```

NOTE: We have started distributing our SDK through our private maven repository instead of distributing it through this Github repository.  This repository will be hosting only the sample integration project.

## Step 2: Add Dependency

Open the build.gradle file of your module and add the Cashfree SDK in the dependency section.

For the android support library version, add the following:

```xml
 dependencies { implementation 'com.cashfree.pg:android-support-sdk:1.4.2.2' }
 ```

For the latest androidX library version, add the following:

```xml
 dependencies { implementation 'com.cashfree.pg:android-sdk:1.5.0' }
 ```
<br/>

## Step 3: Add permissions and Dependencies

The CashfreeSDK requires that you add the permissions shown below in your `Android Manifest` file.
We support integration from API level 16. Do ensure that the minSdkVersion in the build.gradle of your app is equal to (or greater than) that.

```xml
<manifest ...>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<application ...>
```

Add  these dependencies to your build.gradle file.

```xml
dependencies {
    ...
    //Dependencies used by all payment modes
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'com.android.volley:volley:1.1.1'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'

    //doPayment - If OTP Auto read enabled (default)
    implementation 'com.google.android.material:material:1.1.0'
    implementation 'com.google.android.gms:play-services-auth:17.0.0'
    implementation 'com.google.android.gms:play-services-auth-api-phone:17.3.0'

    //doUPIPayment
    implementation 'androidx.recyclerview:recyclerview:1.1.0'

    //doGPayPayment  - Also add the google maven repository as shown in the consecutive step
    implementation files ("libs/google-pay-client-api-1.0.0.aar")
    implementation 'com.google.android.gms:play-services-tasks:15.0.1'

    //doAmazonPayment
    implementation 'androidx.browser:browser:1.0.0'
}
```

Add the google maven repository to your project in the top level build.gradle only if you are using doGPayPayment() function. The web checkout solution uses collect request which does not need the Google Pay app to be installed in the same phone.


```gradle

    allprojects{
        repositories {
            google()
            ...
        }
    }

```
Set the tools:node attribute to "merge" in the definition of your application element in the Android Manifest file.
If OTP auto-read-submit feature is enabled(by default) for your account add the Google Play services version meta-data.

```xml
<application
        ...
        tools:node="merge">
          <!--Only add it if you need Auto OTP reading feature is enabled-->
        <meta-data android:name="com.google.android.gms.version"
          android:value="@integer/google_play_services_version" />
</application>
```


<br>

## Step 4: Generate Token (From Backend)
You will need to generate a token from your backend and pass it to app while initiating payments. For generating token you need to use our token generation API. Please take care that this API is called only from your <b><u>backend</u></b> as it uses **secretKey**. Thus this API should **never be called from App**.

<br/>

### Request Description

<copybox>

  For production/live usage set the action attribute of the form to:
   `https://api.cashfree.com/api/v2/cftoken/order`

  For testing set the action attribute to:
   `https://test.cashfree.com/api/v2/cftoken/order`

</copybox>

You need to send orderId, orderCurrency and orderAmount as a JSON object to the API endpoint and in response a token will received. Please see  the description of request below.

```bash
curl -XPOST -H 'Content-Type: application/json'
-H 'x-client-id: <YOUR_APP_ID>'
-H 'x-client-secret: <YOUR_SECRET_KEY>'
-d '{
  "orderId": "<ORDER_ID>",
  "orderAmount":<ORDER_AMOUNT>,
  "orderCurrency": "INR"
}' 'https://test.cashfree.com/api/v2/cftoken/order'
```
<br/>

### Request Example

Replace **YOUR_APP_ID** and **YOUR_SECRET_KEY** with actual values.
```bash
curl -XPOST -H 'Content-Type: application/json' -H 'x-client-id: YOUR_APP_ID' -H 'x-client-secret: YOUR_SECRET_KEY' -d '{
  "orderId": "Order0001",
  "orderAmount":1,
  "orderCurrency":"INR"
}' 'https://test.cashfree.com/api/v2/cftoken/order'
```
<br/>

### Response Example

```bash
{
"status": "OK",
"message": "Token generated",
"cftoken": "v79JCN4MzUIJiOicGbhJCLiQ1VKJiOiAXe0Jye.s79BTM0AjNwUDN1EjOiAHelJCLiIlTJJiOik3YuVmcyV3QyVGZy9mIsEjOiQnb19WbBJXZkJ3biwiIxADMwIXZkJ3TiojIklkclRmcvJye.K3NKICVS5DcEzXm2VQUO_ZagtWMIKKXzYOqPZ4x0r2P_N3-PRu2mowm-8UXoyqAgsG"
}
```

The "cftoken" is the token that is used authenticate your payment request that will be covered in the next step.
<br/>

## Step 5: Initiate Payment

- App passes the order info and the token to the SDK
- Customer is shown the payment screen where he completes the payment
- Once the payment is complete SDK verifies the payment
- App receives the response from SDK and handles it appropriately


### NOTE

- The order details passed during the token generation and the payment initiation should match. Otherwise you'll get a<b>"invalid order details"</b> error.
- Wrong appId and token will result in <b>"Unable to authenticate merchant"</b> error. The token generated for payment is valid for 5 mins within which the payment has to be initiated. Otherwise you'll get a <b>"Invalid token"</b> error.

# Payment modes


## Web Checkout Integration

### How to integrate


For both the modes (normal and [seamless](https://docs.cashfree.com/docs/android/guide/#seamless-integration)) you need to invoke the <b>doPayment()</b> method. However, there are a few extra parameters you need to pass incase of seamless mode.



### doPayment

```java

public  void  doPayment(Context context, Map<String, String> params, String token, String stage)

```

Initiates the payment in a webview. The customer will be taken to the payment page on cashfree server where they will have the option of paying through any payment option that is activated on their account. Once the payment is done the webview will close and the response will be delivered through onActivityResult().

<b>Parameters:</b>

-  <code>context</code>: Context object of the calling activity is required for this method. In most of the cases this will mean sending the instance of the calling activity (this).

-  <code>params</code>: A map of all the relevant parameters described [here](https://docs.cashfree.com/docs/android/guide/#request-parameters).

-  <code>token</code>: The token generated from **Step 4**.

-  <code>stage</code>: Value should be either "**TEST**" or "**PROD**" for testing server or production server respectively.




### doPayment - with Custom toolbar Theme



```java

public  void  doPayment(Context context, Map<String, String> params, String token, String stage, String color1, String color2)

```

Initiate the payment in a webview. The customer will be taken to the payment page on cashfree server where they will have the option of paying through any payment option that is activated on their account. Once the payment is done the webview will close and the response will be delivered through onActivityResult().




<b>Parameters:</b>



-  <code>context</code>: Context object of the calling activity is required for this method. In most of the cases this will mean sending the instance of the calling activity (this).

-  <code>params</code>: A map of all the relevant parameters described [here](https://docs.cashfree.com/docs/android/guide/#request-parameters).

-  <code>token</code>: The token generated from **Step 4**.

-  <code>stage</code>: Value should be either "**TEST**" or "**PROD**" for testing server or production server respectively.

-  <code>color1</code>: Toolbar brackground color

-  <code>color2</code>: Toolbar text and back arrow color




## Amazon Pay



### Initiate Payment



```java

public  void  doAmazonPayment(Context context, Map<String, String> params, String token, String stage)

```



Initiate the payment in chrome tab (or browser if it is not available) in the customer’s phone. The customer will be taken to amazon pay payment page. Once the payment is completed/ended and the user closes the chrome tab, they’ll be taken to SDK screen where the payment verification happens. Once the payment is verified the user is redirected to the app again. The response will be delivered through onActivityResult().



<b>Parameters:</b>



-  <code>context</code>: Context object of the calling activity is required for this method. In most of the cases this will mean sending the instance of the calling activity (this).

-  <code>params</code>: A map of all the relevant parameters described [here](https://docs.cashfree.com/docs/android/guide/#request-parameters).

-  <code>token</code>: The token generated from **Step 4**.

-  <code>stage</code>: Value should be either "**TEST**" or "**PROD**" for testing server or production server respectively.


## Google Pay

### Check GPay Ready



```java

public  void  isGPayReadyForPayment(Context context, final  GooglePayStatusListener listener)

```

Call this function before showing the GPay payment option to the user. It checks if the GPay app is installed in the device and if it is ready for making payments.



<b>Parameters:</b>



-  <code>context</code>: Context object of the calling activity is required for this method. In most of the cases this will mean sending the instance of the calling activity (this).

-  <code>listener</code>: GooglePayStatusListener to receive the result




```java

public  interface  GooglePayStatusListener {
    void  isReady();
    void  isNotReady();
}

```



### Initiate Payment



```java

public  void  gPayPayment(Context context, Map<String, String> params, String token, String stage)

```

Once the payment is initiated the Google Pay app is opened directly and once the payment is done, the SDK verifies the payment and the result is delivered through onActivityResult() to the calling activity.



<b>Parameters:</b>



-  <code>context</code>: Context object of the calling activity is required for this method. In most of the cases this will mean sending the instance of the calling activity (this).

-  <code>params</code>: A map of all the relevant parameters described [here](https://docs.cashfree.com/docs/android/guide/#request-parameters).

-  <code>token</code>: The token generated from **Step 4**.

-  <code>stage</code>: Value should be either "**TEST**" or "**PROD**" for testing server or production server respectively.



## PhonePe



### Check if PhonePe App is ready for payment


```java

public  boolean  doesPhonePeExist(Context context, String stage)

```

Please call this function before showing PhonePe as a payment option to the user. It checks if the device has a valid version of PhonePe app installed and if it is available then it checks if the app is setup to make payments.



<b>Parameters:</b>



-  <code>context</code>: Context object of the calling activity is required for this method. In most of the cases this will mean sending the instance of the calling activity (this).

-  <code>stage</code>: Value should be either "TEST" or "PROD" for testing server or production server respectively.




### Initiate Payment



```java

public  void  phonePePayment(Context context, Map<String, String> params, String token, String stage)

```

Once the payment is initiated the PhonePe app is opened directly for payment and once the payment is done, the SDK verifies the payment and the result is delivered through onActivityResult() to the calling activity.



<b>Parameters:</b>



-  <code>context</code>: Context object of the calling activity is required for this method. In most of the cases this will mean sending the instance of the calling activity (this).

-  <code>params</code>: A map of all the relevant parameters described [here](https://docs.cashfree.com/docs/android/guide/#request-parameters).

-  <code>token</code>: The token generated from **Step 4**.

-  <code>stage</code>: Value should be either "**TEST**" or "**PROD**" for testing server or production server respectively.


## UPI Intent



### Initiate Payment



```java

public  void  upiPayment(Context context, Map<String, String> params, String token, String stage)

```

Payment done through a UPI intent. When the method is invoked the customer will be presented with a list showing all the installed UPI Apps on their device. If instead you want to preselect the client that should be chosen for payment use this method. Once the customer selects their preffered app, the payment confirmation page on the app will open. Once the payment is done on their UPI App the response will be delivered through onActivityResult().



<b>Parameters:</b>



-  <code>context</code>: Context object of the calling activity is required for this method. In most of the cases this will mean sending the instance of the calling activity (this).

-  <code>params</code>: A map of all the relevant parameters described [here](https://docs.cashfree.com/docs/android/guide/#request-parameters).

-  <code>token</code>: The token generated from **Step 4**.

-  <code>stage</code>: Value should be either "**TEST**" or "**PROD**" for testing server or production server respectively.



### selectUpiClient

```java

public  void  selectUpiClient(String upiClientPackage)

```

When initiating the UPI intent the customer is presented with a list of all the UPI client Apps (BHIM, GPay, PhonePe etc.) on their phone. This allows the customer to choose any UPI App they want to pay with. If instead you want the customer to pay with a particular app you can use this method. After calling this method whenever upiPayment is called the customer will be no longer be shown an App selection screen and instead will be redirected to the App whose package is provided in the argument.



<b>Parameters:</b>



-  <code>upiClientPackage</code>: The string describing the java package of the upi client that is to be selected.




### getUpiClients

```java

public  String[] getUpiClients(Context context)

```

Get the packages of all the UPI Clients installed on the device as a string array. These packages can then be passed to selectUpiClient() method to initiate payment.



<b>Parameters:</b>



-  <code>context</code>: Context object of the calling activity is required for this method. In most of the cases this will mean sending the instance of the calling activity (this).






## Receive Response



Once the payment is done you will receive the response on the onActivityResult() function inside the invoking activity. In the intent extras you will receive a set of [response parameters](/android/response-param) which you can use to determine if the transaction was successful. Request code will always be equal to CFPaymentService.REQ_CODE.



```java

@Override

protected  void  onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Same request code for all payment APIs.
        Log.d(TAG, "ReqCode : " + CFPaymentService.REQ_CODE);
        Log.d(TAG, "API Response : ");
        //Prints all extras. Replace with app logic.
        if (data != null) {
            Bundle  bundle = data.getExtras();
            if (bundle != null)
                for (String  key  :  bundle.keySet()) {
                    if (bundle.getString(key) != null) {
                        Log.d(TAG, key + " : " + bundle.getString(key));
                    }
                }
        }
}

```

<br/>
