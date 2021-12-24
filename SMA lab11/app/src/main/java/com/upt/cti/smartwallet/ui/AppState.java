package com.upt.cti.smartwallet.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.upt.cti.smartwallet.MainActivity;
import com.upt.cti.smartwallet.enums.Month;
import com.upt.cti.smartwallet.model.Payment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppState {
    private static AppState singletonObject;

    private String uid;

    public static synchronized AppState get() {
        if (singletonObject == null) {
            singletonObject = new AppState();
        }
        return singletonObject;
    }

    // reference to Firebase used for reading and writing data
    private DatabaseReference databaseReference;
    // current payment to be edited or deleted
    private Payment currentPayment;

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void setCurrentPayment(Payment currentPayment) {
        this.currentPayment = currentPayment;
    }

    public Payment getCurrentPayment() {
        return currentPayment;
    }

    public static String getCurrentTimeDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return sdfDate.format(now);
    }

    public boolean hasLocalStorage(Context context) {
        return context.getFilesDir().listFiles().length > 0;
    }

    private boolean isValid(String dateStr) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public List<Payment> loadFromLocalBackup(Context context, int month) {
        try {
            List<Payment> payments = new ArrayList<>();
            for (File file : context.getFilesDir().listFiles()) {
                if (isValid(file.getName())) {
                    FileInputStream fis = context.openFileInput(file.getName());
                    ObjectInputStream is = new ObjectInputStream(fis);
                    Payment payment = (Payment) is.readObject();
                    if (Month.monthFromTimestamp(payment.timestamp) == month)
                        payments.add(payment);
                    is.close();
                    fis.close();
                }
            }
            return payments;
        } catch (IOException e) {
            Toast.makeText(context, "Cannot access local data.", Toast.LENGTH_SHORT).show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =

                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setUserId(String uid) {
        this.uid = uid;
    }

    public String getUserID() {
        return uid;
    }
}
