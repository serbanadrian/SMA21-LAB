package ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.upt.cti.smartwallet.AppState;
import com.upt.cti.smartwallet.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Payment;
import model.PaymentType;

public class AddPaymentActivity extends AppCompatActivity {

    private EditText eName;
    private EditText eCost;
    private Spinner sType;
    private TextView tTimestamp;

    private Button btDelete;
    private Button btSave;


    private Payment payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_payment);
        setTitle("Add or edit payment");

        // ui
        eName = (EditText) findViewById(R.id.eName);
        eCost = (EditText) findViewById(R.id.eCost);
        sType = (Spinner) findViewById(R.id.sType);
        tTimestamp = (TextView) findViewById(R.id.tTimestamp);
        btDelete = (Button) findViewById(R.id.btDelete);
        btSave = (Button) findViewById(R.id.btSave);

        // spinner adapter
        String[] types = PaymentType.getTypes();
        final ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this,

                android.R.layout.simple_spinner_item, types);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sType.setAdapter(sAdapter);

        // initialize UI if editing
        payment = AppState.get().getCurrentPayment();
        if (payment != null) {
            eName.setText(payment.getName());
            eCost.setText(String.valueOf(payment.getCost()));
            tTimestamp.setText("Time of payment: " + payment.timestamp);
            try {
                sType.setSelection(Arrays.asList(types).indexOf(payment.getType()));
            } catch (Exception e) {
            }
        } else {
            tTimestamp.setText("");
        }

    }

    public static String getCurrentTimeDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public void clicked(View view) {
        switch (view.getId()) {
            case R.id.btSave:
                if (payment != null)
                    save(payment.timestamp);
                else
                    save(AppState.getCurrentTimeDate());
                break;
            case R.id.btDelete:
                if (payment != null)
                    delete(payment.timestamp);
                else
                    Toast.makeText(this, "Payment does not exist", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void save(String timestamp) {
        Payment payment = new Payment(
                Double.parseDouble(eCost.getText().toString()),
                eName.getText().toString(),
                sType.getSelectedItem().toString()
        );
        payment.timestamp = timestamp;
        Map<String, Object> map = new HashMap<>();
        map.put("cost", payment.getCost());
        map.put("name", payment.getName());
        map.put("type", payment.getType());
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).updateChildren(map);
        AppState.get().setCurrentPayment(payment);
        finish();

    }
    private void delete(String timestamp){
        AppState.get().getDatabaseReference().child("wallet").child(timestamp).removeValue();
        finish();
    }
}
