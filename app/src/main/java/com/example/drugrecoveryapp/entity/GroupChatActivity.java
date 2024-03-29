package com.example.drugrecoveryapp.entity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.drugrecoveryapp.GroupChatAdapter;
import com.example.drugrecoveryapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class GroupChatActivity extends AppCompatActivity {

    private EditText messageInput;
    private Button sendButton;
    private ListView chatListView;
    private GroupChatAdapter groupChatAdapter;
    private DatabaseReference groupChatRef;
    private String currentUserUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // Find the Toolbar
        Toolbar toolbar = findViewById(R.id.TBGroupChat);

        // Set the Toolbar as the ActionBar
        setSupportActionBar(toolbar);

        // Enable the back button
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitleTextColor(Color.WHITE);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_keyboard_backspace_24); // Set your white back arrow icon here
        }

        String groupId = getIntent().getStringExtra("groupId");
        currentUserUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        groupChatRef = FirebaseDatabase.getInstance().getReference("GroupChat").child(groupId);

        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatListView = findViewById(R.id.messagesListView);

        ArrayList<MessageModel> messageList = new ArrayList<>();
        groupChatAdapter = new GroupChatAdapter(this, messageList);
        chatListView.setAdapter(groupChatAdapter);
        String groupName = getIntent().getStringExtra("groupName");

        // Set the group name in the TextView
        TextView groupNameTextView = findViewById(R.id.groupNameTextView);
        groupNameTextView.setText(groupName);
        sendButton.setOnClickListener(v -> sendMessage());

        // Listen for new messages
        groupChatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                MessageModel message = dataSnapshot.getValue(MessageModel.class);
                if (message != null) {
                    groupChatAdapter.add(message);
                    scrollChatListViewToBottom();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        String groupId =getIntent().getStringExtra("groupId");

        if (!messageText.isEmpty()) {
            MessageModel message = new MessageModel(groupId, currentUserUid, messageText);
            groupChatRef.push().setValue(message);
            messageInput.setText("");
        }
    }

    private void scrollChatListViewToBottom() {
        chatListView.post(() -> chatListView.setSelection(groupChatAdapter.getCount() - 1));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Handle the back button click
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
