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


    ArrayList<Data> dataArrayList = new ArrayList<>();
    public void createList(final String phoneNumber){
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> friends = dataSnapshot.child("Users").child(phoneNumber).child("Friends").getChildren();
                while (friends.iterator().hasNext()) {
                    DataSnapshot ds = friends.iterator().next();
                    String number = ds.getKey();
                    double cur = ds.child("Amount").getValue(Double.class);
                    dataArrayList.add(new Data(number,cur,0));
                }
                DebtsFragment.instance.fill_with_data(dataArrayList);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    void totalSum(final String phoneNumber) {

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double sum = 0;
                double debts = 0;
                Iterable<DataSnapshot> friends = dataSnapshot.child("Users").child(phoneNumber).child("Friends").getChildren();
                while (friends.iterator().hasNext()) {
                    double cur = friends.iterator().next().child("Amount").getValue(Double.class);
                    if (cur > 0) {
                        sum += cur;
                    } else {
                        debts += cur;
                    }

                    MainFragment.instance.updateTextViews(sum,debts);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Date, amount, people involved, approval status for each person
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


