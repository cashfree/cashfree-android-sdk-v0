package com.gocashfree.cfsdk.sample.cfsdksample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;

import java.util.HashMap;
import java.util.Map;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_PAYMENT_MODES;


public class MainActivity extends AppCompatActivity implements CFClientInterface {

    private static final String TAG = "MainActivity";

    private Map<String, String> params;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void initPayment(View view) {
        // This should be changed to your own server checksum generator utility.
        // Refer CashfreeSDK Documentation for more info.
        String checksum_url ="https://www.musclesandwellness.com/appservices/userdashboard/gocash.php";

        params = new HashMap<>();

        // Change this to reflect your own APP_ID. Refer CashfreeSDK Documentation for more info.
        params.put(PARAM_APP_ID, "17395cac6b633f4af601ff1371");
        params.put(PARAM_ORDER_ID, ((EditText) findViewById(R.id.order_id)).getText().toString());
        params.put(PARAM_ORDER_AMOUNT, ((EditText) findViewById(R.id.order_amount)).getText().toString());
        params.put(PARAM_ORDER_NOTE, ((EditText) findViewById(R.id.order_note)).getText().toString());
        params.put(PARAM_CUSTOMER_NAME, ((EditText) findViewById(R.id.customer_name)).getText().toString());
        params.put(PARAM_CUSTOMER_PHONE, ((EditText) findViewById(R.id.customer_phone)).getText().toString());
        params.put(PARAM_CUSTOMER_EMAIL,((EditText) findViewById(R.id.customer_email)).getText().toString());

        // Allow all payment modes by using empty string
        params.put(PARAM_PAYMENT_MODES, "");

        for(Map.Entry entry : params.entrySet()) {
            Log.d("TEST", entry.getKey() + " " + entry.getValue());
        }

        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();

        // stage identifies whether you want trigger test or production service
        String stage = "TEST";
        cfPaymentService.doPayment(this, params, checksum_url, this, stage);
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

