package com.example.sample.samplemerchantapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_NOTIFY_URL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_PAYMENT_MODES;



public class MerchantActivity extends AppCompatActivity implements CFClientInterface {
    private static final String TAG = "MerchantActivity";
    private Map<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

        String orderId = String.valueOf(System.currentTimeMillis() / 100000);

        EditText orderIdEditText = (EditText) findViewById(R.id.order_id);
        orderIdEditText.setText(orderId);
    }

    public void initPayment(View view) {
        // This should be changed to your own server checksum generator utility.
        // Refer CashfreeSDK Documentation for more info.
        String checksum_url ="http://192.168.15.163/checksum.php";

        params = new HashMap<>();

        // Change this to reflect your own APP_ID. Refer CashfreeSDK Documentation for more info.
        params.put(PARAM_APP_ID, "MTA2OTkyMDE1ODE0NDIyNTExNjYjIz");

        params.put(PARAM_ORDER_ID, ((EditText) findViewById(R.id.order_id)).getText().toString());
        params.put(PARAM_ORDER_AMOUNT, ((EditText) findViewById(R.id.order_amount)).getText().toString());
        params.put(PARAM_ORDER_NOTE, ((EditText) findViewById(R.id.order_note)).getText().toString());
        params.put(PARAM_CUSTOMER_NAME, ((EditText) findViewById(R.id.customer_name)).getText().toString());
        params.put(PARAM_CUSTOMER_PHONE, ((EditText) findViewById(R.id.customer_phone)).getText().toString());
        params.put(PARAM_CUSTOMER_EMAIL,((EditText) findViewById(R.id.customer_email)).getText().toString());

        // Change this to reflect your own notification url. Refer CashfreeSDK Documentation for more info.
        params.put(PARAM_NOTIFY_URL, "https://www.testapp.com/notify");

        // Allow all payment modes by using empty string
        params.put(PARAM_PAYMENT_MODES, "");

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest checksumRequest = new StringRequest(Request.Method.POST, checksum_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "onResponse(): The response is " + response);

                        String valChecksum = "";

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if ((jsonObject.get("status")).equals("OK")) {
                                valChecksum = (String) jsonObject.get("checksum");
                            } else {
                                Log.e(TAG, "onResponse(): Error in checksum request");
                                return;
                            }

                        } catch (JSONException ex) {
                            Log.e(TAG, "onResponse(): Error in checksum response JSON");
                        }

                        MerchantActivity.this.onChecksumReceived(valChecksum);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse(): The error is " + error.toString());
                    }
                }

        ) {

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> paramsToReturn = new HashMap<>();

                for (String key : MerchantActivity.this.params.keySet()) {
                    paramsToReturn.put(key, MerchantActivity.this.params.get(key));
                }

                return paramsToReturn;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        // Add the request to the RequestQueue.
        queue.add(checksumRequest);
    }

    public void onChecksumReceived(String checksum){
        Log.d(TAG, "onChecksumReceived(): Received Checksum: " + checksum);
        CFPaymentService cfpay = CFPaymentService.getCFPaymentServiceInstance();
        cfpay.doPayment(this, params, checksum, this, CFPaymentService.STAGE_TEST_SERVICE                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       );

        Log.d(TAG, "onChecksumReceived(): Launching Payment Service");
    }

    @Override
    public void onSuccess(Map<String, String> map) {

    }

    @Override
    public void onFailure(Map<String, String> map) {

    }

    @Override
    public void onNavigateBack() {

    }
}
