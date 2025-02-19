package com.theanimegroup.movie_ticket_booking_client.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.theanimegroup.movie_ticket_booking_client.R;
import com.theanimegroup.movie_ticket_booking_client.api.APIUnit;
import com.theanimegroup.movie_ticket_booking_client.api.AuthenticationService;
import com.theanimegroup.movie_ticket_booking_client.models.request.AuthenticationRequest;
import com.theanimegroup.movie_ticket_booking_client.models.response.AuthenticationResponse;
import com.theanimegroup.movie_ticket_booking_client.models.response.ResponseObject;
import com.theanimegroup.movie_ticket_booking_client.util.TokenUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private AuthenticationService authenticationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        TextView loginButton = findViewById(R.id.login_button);
        TextView registerLinkTxt = findViewById(R.id.register_link);
        authenticationService = APIUnit.getInstance().getAuthenticationService();
        loginButton.setOnClickListener(v -> loginUser());
        registerLinkTxt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        AuthenticationRequest loginRequest = AuthenticationRequest.builder()
                .email(email)
                .password(password)
                .build();

        authenticationService.login(loginRequest).enqueue(new Callback<ResponseObject<AuthenticationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseObject<AuthenticationResponse>> call, @NonNull Response<ResponseObject<AuthenticationResponse>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    assert response.body() != null;
                    String token = response.body().getData().getToken();
                    TokenUtils.saveAuthToken(LoginActivity.this, token);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseObject<AuthenticationResponse>> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }
}
