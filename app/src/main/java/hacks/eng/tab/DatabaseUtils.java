package hacks.eng.tab;

import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by williamgill on 2017-02-04.
 */

public class DatabaseUtils {


    DatabaseReference myRef;

    DatabaseUtils(DatabaseReference myDatabase) {
        myRef = myDatabase;
    }


    void performTransaction(Transaction transaction) {
        transaction.addTransactionFirebase(myRef);
    }

    void updateAmount(final String userDebt, final String userCred, final double amount) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Users").child(userCred).child("Friends").hasChild(userDebt)) {
                    double amountCur = dataSnapshot.child("Users").child(userCred).child("Friends").child(userDebt).child("Amount").getValue(Double.class);
                    myRef.child("Users").child(userCred).child("Friends").child(userDebt).child("Amount").setValue(amountCur + amount);
                } else {
                    myRef.child("Users").child(userCred).child("Friends").child(userDebt).child("Amount").setValue(amount);

                }
                if (dataSnapshot.child("Users").child(userDebt).child("Friends").hasChild(userCred)) {
                    double amountCur = dataSnapshot.child("Users").child(userDebt).child("Friends").child(userCred).child("Amount").getValue(Double.class);
                    myRef.child("Users").child(userDebt).child("Friends").child(userCred).child("Amount").setValue(amountCur - amount);
                } else {
                    myRef.child("Users").child(userDebt).child("Friends").child(userCred).child("Amount").setValue(-amount);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public boolean approvalStatus = false;

    boolean getApprovalStatus(final String curUser, final String findUser) {
        myRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                approvalStatus = (boolean) dataSnapshot.child("Users").child(curUser).child("Friends").child(findUser).child("ApprovalStatus").getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        return approvalStatus;
    }

    private double sum = 0;
    private double debts = 0;


    double[] totalSum(final String phoneNumber) {

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> friends = dataSnapshot.child("Users").child(phoneNumber).child("Friends").getChildren();
                while (friends.iterator().hasNext()) {
                    DataSnapshot ds = (DataSnapshot) friends.iterator().next();
                    double cur = (double) ds.child("Amount").getValue();
                    if (cur > 0) {
                        sum += cur;
                    } else {
                        debts += cur;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        double[] credDebt = {sum, debts};
        sum = 0;
        debts = 0;
        return credDebt;

        //Date, amount, people involved, approval status for each person
    }


    ArrayList<Data> dataArrayList = new ArrayList<>();
    ArrayList<Data> createList(final String phoneNumber){
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> friends = dataSnapshot.child("Users").child(phoneNumber).child("Friends").getChildren();
                while (friends.iterator().hasNext()) {
                    DataSnapshot ds = (DataSnapshot) friends.iterator().next();
                    String number = (String) ds.getValue();
                    double cur = (double) ds.child("Amount").getValue();
                    dataArrayList.add(new Data(number,cur,0));

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return dataArrayList;
    }
}

class Transaction {

    double amount;
    String[] peopleInvolved;
    String time;

    Transaction(double amountx, String[] peopleInvolvedx, String timex) {
        amount = amountx;
        peopleInvolved = peopleInvolvedx;
        time = timex;
    }


    public void addTransactionFirebase(DatabaseReference myRef) {
        for (int i = 0; i < peopleInvolved.length; i++) {
            myRef.child("Users").child(peopleInvolved[0]).child("Transactions").child(time).child("PeopleInvolved").child(peopleInvolved[i]).child("AmountOwed").child(peopleInvolved[i]).setValue(amount);
            myRef.child("Users").child(peopleInvolved[0]).child("Transactions").child(time).child("PeopleInvolved").child(peopleInvolved[i]).child("ApprovalStatus").setValue(false);
        }

    }


}


