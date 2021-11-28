package com.upt.cti.smartwallet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Payment;
import ui.AddPaymentActivity;
import ui.PaymentAdapter;

public class MainActivity extends AppCompatActivity  {

    private int currentMonth;
    private List<Payment> payments = new ArrayList<>();
    private DatabaseReference databaseReference;

    private TextView tStatus;
    private Button bPrevious;
    private Button bNext;
    private FloatingActionButton fabAdd;
    private ListView listPayments;

    private ValueEventListener databaseListener = null;

    public enum Month {
        January, February, March, April, May, June, July, August,
        September, October, November, December;


        public static Month intToMonthName(int index) {
            return Month.values()[index];
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tStatus = (TextView) findViewById(R.id.tStatus);
        bPrevious = (Button) findViewById(R.id.bPrevious);
        bNext = (Button) findViewById(R.id.bNext);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        listPayments = (ListView) findViewById(R.id.listPayments);
        final PaymentAdapter adapter = new PaymentAdapter(this, R.layout.item_payment, payments);
        listPayments.setAdapter(adapter);

        listPayments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AppState.get().setCurrentPayment(payments.get(i));
                startActivity(new Intent(getApplicationContext(), AddPaymentActivity.class));
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://smart-wallet-d5bb5-default-rtdb.europe-west1.firebasedatabase.app");
        databaseReference = database.getReference();
        AppState.get().setDatabaseReference(databaseReference);
        AppState.get().getDatabaseReference().child("wallet").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot,String previousChildName) {
                Payment newPayment = snapshot.getValue(Payment.class);
                if (newPayment != null) {
                    newPayment.timestamp = snapshot.getKey();
                    AppState.get().updateLocalBackup(getApplicationContext(), newPayment, true);
                    if (!payments.contains(newPayment))
                    {
                        payments.add(newPayment);
                    }
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot snapshot,String previousChildName) {

                Payment payment = snapshot.getValue(Payment.class);

                if (payment != null) {
                    payment.timestamp = snapshot.getKey();
                    AppState.get().updateLocalBackup(getApplicationContext(), payment, true);
                    for (Payment p : payments) {
                        if (p.timestamp.equals(payment.timestamp)) {
                            payments.set(payments.indexOf(p), payment);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

                    Payment payment = snapshot.getValue(Payment.class);

                    if (payment != null) {
                        payment.timestamp = snapshot.getKey();
                        AppState.get().updateLocalBackup(getApplicationContext(), payment, true);
                        for (Payment p: payments) {
                            if (p.equals(payment)) {
                                payments.remove(p);
                                break;
                            }
                        }

                        adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot,String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        }) ;

        if (!AppState.isNetworkAvailable(this)) {
            // has local storage already
            if (AppState.get().hasLocalStorage(this)) {
                payments = AppState.get().loadFromLocalBackup(this);
                if(payments!=null)
                        tStatus.setText("Found " + payments.size() + " payments for " +
                                Month.intToMonthName(currentMonth) + ".");
                else
                    tStatus.setText("Found 0");
            } else {
                Toast.makeText(this, "This app needs an internet connection!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }
    public void clicked(View view){
        switch(view.getId()){
            case R.id.fabAdd:
                startActivity(new Intent(this, AddPaymentActivity.class));
                break;
        }
    }
}