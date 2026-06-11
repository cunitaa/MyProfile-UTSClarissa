package com.example.myprofile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, dashNama, dashUsername, dashTtl, dashUmur, dashZodiak, dashGender, dashJurusan, dashStatus, dashHobi, dashBio;
    private ImageView ivProfileDash;
    
    private String currentNama, currentTempatLahir, currentTanggalLahir, currentHobi, currentBio, currentImageUri;
    private String currentGender, currentJurusan;
    private boolean currentStatus;

    private final ActivityResultLauncher<Intent> startMainLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    updateDashboard(result.getData());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar_dashboard);
        setSupportActionBar(toolbar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        findViewById(R.id.btn_to_main).setOnClickListener(v -> openEditProfile());
        
        // Tambahkan ini jika toolbar diklik di bagian mana saja (opsional tapi membantu)
        toolbar.setOnClickListener(v -> openEditProfile());

        String user = getIntent().getStringExtra("USERNAME");
        tvWelcome.setText("Welcome!");
        dashUsername.setText(user);
    }

    private void openEditProfile() {
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        intent.putExtra("EXISTING_NAMA", currentNama);
        intent.putExtra("EXISTING_TEMPAT_LAHIR", currentTempatLahir);
        intent.putExtra("EXISTING_TANGGAL_LAHIR", currentTanggalLahir);
        intent.putExtra("EXISTING_HOBI", currentHobi);
        intent.putExtra("EXISTING_BIO", currentBio);
        intent.putExtra("EXISTING_IMAGE", currentImageUri);
        intent.putExtra("EXISTING_GENDER", currentGender);
        intent.putExtra("EXISTING_JURUSAN", currentJurusan);
        intent.putExtra("EXISTING_STATUS", currentStatus);
        startMainLauncher.launch(intent);
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tv_welcome);
        dashNama = findViewById(R.id.dash_nama);
        dashUsername = findViewById(R.id.dash_username);
        dashTtl = findViewById(R.id.dash_ttl);
        dashUmur = findViewById(R.id.dash_umur);
        dashZodiak = findViewById(R.id.dash_zodiak);
        dashGender = findViewById(R.id.dash_gender);
        dashJurusan = findViewById(R.id.dash_jurusan);
        dashStatus = findViewById(R.id.dash_status);
        dashHobi = findViewById(R.id.dash_hobi);
        dashBio = findViewById(R.id.dash_bio);
        ivProfileDash = findViewById(R.id.iv_profile_dash);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit_profile) {
            openEditProfile();
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(DashboardActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateDashboard(Intent data) {
        currentNama = data.getStringExtra("NAMA");
        currentTempatLahir = data.getStringExtra("TEMPAT_LAHIR");
        currentTanggalLahir = data.getStringExtra("TANGGAL_LAHIR");
        currentHobi = data.getStringExtra("HOBI");
        currentBio = data.getStringExtra("BIO");
        currentImageUri = data.getStringExtra("IMAGE_URI");
        currentGender = data.getStringExtra("GENDER");
        currentJurusan = data.getStringExtra("JURUSAN");
        currentStatus = data.getBooleanExtra("STATUS_AKTIF", false);
        
        if (currentNama != null) {
            dashNama.setText(currentNama);
            tvWelcome.setText("Welcome, " + currentNama + "!");
        }
        
        if (currentTempatLahir != null && currentTanggalLahir != null) {
            dashTtl.setText(currentTempatLahir + ", " + currentTanggalLahir);
            calculateDetails(currentTanggalLahir);
        }
        
        if (currentGender != null) dashGender.setText(currentGender);
        if (currentJurusan != null) dashJurusan.setText(currentJurusan);
        dashStatus.setText(currentStatus ? "Mahasiswa Aktif" : "Tidak Aktif");
        
        if (currentHobi != null) dashHobi.setText(currentHobi);
        if (currentBio != null) dashBio.setText(currentBio);
        
        if (currentImageUri != null) {
            try {
                ivProfileDash.setImageURI(Uri.parse(currentImageUri));
            } catch (SecurityException e) {
                ivProfileDash.setImageResource(android.R.drawable.ic_menu_report_image);
            } catch (Exception e) {
                ivProfileDash.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }

        // HAPUS atau KOMENTAR baris ini agar tombol tidak hilang:
        // findViewById(R.id.btn_to_main).setVisibility(View.GONE);
    }

    private void calculateDetails(String dateStr) {
        try {
            String[] parts = dateStr.split("/");
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int year = Integer.parseInt(parts[2]);

            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - year;
            if (today.get(Calendar.MONTH) + 1 < month || 
               (today.get(Calendar.MONTH) + 1 == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
                age--;
            }
            dashUmur.setText(age + " Tahun");
            dashZodiak.setText(getZodiac(day, month));
        } catch (Exception ignored) {}
    }

    private String getZodiac(int day, int month) {
        if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) return "Aquarius";
        if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) return "Pisces";
        if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) return "Aries";
        if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) return "Taurus";
        if ((month == 5 && day >= 21) || (month == 6 && day <= 20)) return "Gemini";
        if ((month == 6 && day >= 21) || (month == 7 && day <= 22)) return "Cancer";
        if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) return "Leo";
        if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) return "Virgo";
        if ((month == 9 && day >= 23) || (month == 10 && day <= 22)) return "Libra";
        if ((month == 10 && day >= 23) || (month == 11 && day <= 21)) return "Scorpio";
        if ((month == 11 && day >= 22) || (month == 12 && day <= 21)) return "Sagittarius";
        return "Capricorn";
    }
}