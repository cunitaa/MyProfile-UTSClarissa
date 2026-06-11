package com.example.myprofile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class ProfileInfoActivity extends AppCompatActivity {

    private TextView tvNama, tvTtl, tvUmur, tvZodiak, tvGender, tvJurusan, tvStatus, tvHobi, tvBio;
    private ImageView ivProfileDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_info_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

        Button btnKembali = findViewById(R.id.btn_kembali);
        btnKembali.setOnClickListener(v -> finish());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String nama = extras.getString("NAMA", "-");
            String tempatLahir = extras.getString("TEMPAT_LAHIR", "-");
            String tanggalLahir = extras.getString("TANGGAL_LAHIR", "");
            String gender = extras.getString("GENDER", "-");
            String jurusan = extras.getString("JURUSAN", "-");
            String hobi = extras.getString("HOBI", "-");
            String bio = extras.getString("BIO", "-");
            boolean statusAktif = extras.getBoolean("STATUS_AKTIF", false);
            String imageUriStr = extras.getString("IMAGE_URI");

            if (imageUriStr != null) {
                try {
                    ivProfileDisplay.setImageURI(Uri.parse(imageUriStr));
                } catch (SecurityException e) {
                    ivProfileDisplay.setImageResource(android.R.drawable.ic_menu_report_image);
                } catch (Exception e) {
                    ivProfileDisplay.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            }

            tvNama.setText(getString(R.string.nama_label, nama));
            tvTtl.setText(getString(R.string.ttl_label, tempatLahir, tanggalLahir));
            tvGender.setText(getString(R.string.gender_label, gender));
            tvJurusan.setText(getString(R.string.jurusan_label, jurusan));
            tvStatus.setText(getString(R.string.status_label, statusAktif ? "Mahasiswa Aktif" : "Tidak Aktif"));
            tvHobi.setText("Hobi: " + hobi);
            tvBio.setText("Bio: " + bio);

            if (!tanggalLahir.isEmpty()) {
                calculateDetails(tanggalLahir);
            }
        }
    }

    private void initViews() {
        tvNama = findViewById(R.id.tv_nama);
        tvTtl = findViewById(R.id.tv_ttl);
        tvUmur = findViewById(R.id.tv_umur);
        tvZodiak = findViewById(R.id.tv_zodiak);
        tvGender = findViewById(R.id.tv_gender);
        tvJurusan = findViewById(R.id.tv_jurusan);
        tvStatus = findViewById(R.id.tv_status);
        tvHobi = findViewById(R.id.tv_hobi);
        tvBio = findViewById(R.id.tv_bio);
        ivProfileDisplay = findViewById(R.id.iv_profile_display);
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
            tvUmur.setText(getString(R.string.umur_label, age));
            tvZodiak.setText(getString(R.string.zodiak_label, getZodiac(day, month)));

        } catch (Exception e) {
            tvUmur.setText(getString(R.string.umur_label, 0));
            tvZodiak.setText(getString(R.string.zodiak_label, "-"));
        }
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