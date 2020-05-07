package com.bronzeswordstudios.glorpythespacething;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.Inet4Address;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase localDb = dbHelper.getReadableDatabase();
        Cursor cursor = localDb.query(DataHolder.DataEntry.TABLE_NAME, DataHolder.DataEntry.projection,
                null, null, null, null, null);
        TextView highest_score_view = findViewById(R.id.highest_score_number);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String highestScore = String.valueOf(cursor.getInt(1));
            highest_score_view.setText(highestScore);
            DataHolder.highestScore = Integer.parseInt(highestScore);
        } else {
            highest_score_view.setText("0");
            DataHolder.highestScore = 0;
            ContentValues values = new ContentValues();
            values.put(DataHolder.DataEntry.HIGHEST_SCORE, 0);
            long newRowID = localDb.insert(DataHolder.DataEntry.TABLE_NAME, null, values);
        }
        //test code
        Button playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // test code
        /*db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Error", "DocumentSnapshot added with ID: " + documentReference.getId());
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Error", "Error adding document", e);
                    }
                });*/


    }
}
