package com.example.homelibrary.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.homelibrary.R;
import com.example.homelibrary.data.AuthManager;
import com.example.homelibrary.data.models.User;
import com.example.homelibrary.ui.common.AvatarUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Profile header + embedded SettingsFragment below.
 */
public class ProfileFragment extends Fragment {

    private ImageView avatarView;
    private TextView nicknameView;
    private TextView emailView;

    private DatabaseReference userRef;
    private ValueEventListener userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf,
                             @Nullable ViewGroup parent,
                             @Nullable Bundle state) {
        return inf.inflate(R.layout.fragment_profile, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        avatarView   = v.findViewById(R.id.profile_avatar);
        nicknameView = v.findViewById(R.id.profile_nickname);
        emailView    = v.findViewById(R.id.profile_email);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.settings_container, new SettingsFragment());
            ft.commit();
        }

        subscribeUser();
    }

    private void subscribeUser() {
        if (AuthManager.getInstance().getCurrentUser() == null) return;
        String uid = AuthManager.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userListener = new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot ds) {
                if (!isAdded()) return;
                User u = ds.getValue(User.class);
                if (u != null) bind(u);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { }
        };
        userRef.addValueEventListener(userListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
    }

    private void bind(@NonNull User u) {
        String nick = (u.nickname != null && !u.nickname.isEmpty())
                ? u.nickname
                : (u.email != null ? u.email.split("@")[0] : "user");
        nicknameView.setText(nick);
        emailView.setText(u.email != null ? u.email : "");

        if (u.avatarUrl != null && !u.avatarUrl.isEmpty()) {
            Glide.with(this).load(u.avatarUrl)
                    .placeholder(R.drawable.placeholder_avatar)
                    .circleCrop()
                    .into(avatarView);
        } else {
            Bitmap bmp = AvatarUtil.create(requireContext(),
                    String.valueOf(nick.charAt(0)), 96);
            avatarView.setImageBitmap(bmp);
        }
    }
}
