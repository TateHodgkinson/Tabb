package hacks.eng.tab;

import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
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

    void updateRequestedAmount(final String userDebt, final String userCred, final double amount) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Users").child(userCred).child("Friends").hasChild(userDebt)) {
                    double amountCur = dataSnapshot.child("Users").child(userCred).child("Friends").child(userDebt).child("RequestedAmount").getValue(Double.class);
                    myRef.child("Users").child(userCred).child("Friends").child(userDebt).child("RequestedAmount").setValue(amountCur + amount);
                } else {
                    myRef.child("Users").child(userCred).child("Friends").child(userDebt).child("RequestedAmount").setValue(amount);

                }
                if (dataSnapshot.child("Users").child(userDebt).child("Friends").hasChild(userCred)) {
                    double amountCur = dataSnapshot.child("Users").child(userDebt).child("Friends").child(userCred).child("RequestedAmount").getValue(Double.class);
                    myRef.child("Users").child(userDebt).child("Friends").child(userCred).child("RequestedAmount").setValue(amountCur - amount);
                } else {
                    myRef.child("Users").child(userDebt).child("Friends").child(userCred).child("RequestedAmount").setValue(-amount);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
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

    void getApprovalStatus(final String curUser, final String findUser) {



        myRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                approvalStatus = (boolean) dataSnapshot.child("Users").child(curUser).child("Friends").child(findUser).child("ApprovalStatus").getValue();
                if(approvalStatus){
                    double amountCur = dataSnapshot.child("Users").child(curUser).child("Friends").child(findUser).child("RequestedAmount").getValue(Double.class);
                    updateAmount(curUser,findUser,amountCur);
                    updateAmount(findUser,curUser,-amountCur);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }


    public void createList(final String phoneNumber){

        DatabaseReference theRef = myRef.child("Users").child(phoneNumber).child("Friends");

        theRef.addChildEventListener(new ChildEventListener() {



            @Override
            public void onChildAdded(DataSnapshot ds, String s) {
                String number = ds.getKey();
                Log.d("PHONE NUMBER-ADD", number);
                double cur = ds.child("Amount").getValue(Double.class);
                Data d = new Data(number,cur,0);
                DebtsFragment.instance.fill_with_data(d,false);
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String s) {
                String number = ds.getKey();
                Log.d("PHONE NUMBER-CHANGED", number);
                double cur = ds.child("Amount").getValue(Double.class);
                Data d = new Data(number,cur,0);
                DebtsFragment.instance.fill_with_data(d,true);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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


                }
                MainFragment.instance.updateTextViews(sum,debts);
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


