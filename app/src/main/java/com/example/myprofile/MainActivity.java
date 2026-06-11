package com.example.myprofile;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.DragEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText etNamaLengkap, etTempatLahir, etTanggalLahir, etHobi, etBio;
    private TextView tvUmurMain, tvZodiakMain;
    private RadioGroup rgJenisKelamin;
    private Spinner spJurusan;
    private SwitchCompat swStatusAktif;
    private Button btnSimpan, btnPilihGambar;
    private ImageView ivProfile;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> pickMedia =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    try {
                        ivProfile.setImageURI(uri);
                    } catch (Exception e) {
                        ivProfile.setImageResource(android.R.drawable.ic_menu_camera);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadExistingData();

        etTanggalLahir.setOnClickListener(v -> showDatePicker());
        btnPilihGambar.setOnClickListener(v -> pickMedia.launch("image/*"));

        ivProfile.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: return true;
                case DragEvent.ACTION_DRAG_ENTERED: ivProfile.setAlpha(0.6f); return true;
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED: ivProfile.setAlpha(1.0f); return true;
                case DragEvent.ACTION_DROP:
                    ivProfile.setAlpha(1.0f);
                    ClipData clipData = event.getClipData();
                    if (clipData != null && clipData.getItemCount() > 0) {
                        Uri uri = clipData.getItemAt(0).getUri();
                        if (uri != null) {
                            selectedImageUri = uri;
                            ivProfile.setImageURI(uri);
                        }
                    }
                    return true;
            }
            return false;
        });

        btnSimpan.setOnClickListener(v -> saveAndReturn());
    }

    private void loadExistingData() {
        Intent intent = getIntent();
        if (intent != null) {
            String nama = intent.getStringExtra("EXISTING_NAMA");
            String tempat = intent.getStringExtra("EXISTING_TEMPAT_LAHIR");
            String tanggal = intent.getStringExtra("EXISTING_TANGGAL_LAHIR");
            String hobi = intent.getStringExtra("EXISTING_HOBI");
            String bio = intent.getStringExtra("EXISTING_BIO");
            String img = intent.getStringExtra("EXISTING_IMAGE");
            String gender = intent.getStringExtra("EXISTING_GENDER");
            String jurusan = intent.getStringExtra("EXISTING_JURUSAN");
            boolean status = intent.getBooleanExtra("EXISTING_STATUS", false);

            if (nama != null) etNamaLengkap.setText(nama);
            if (tempat != null) etTempatLahir.setText(tempat);
            if (hobi != null) etHobi.setText(hobi);
            if (bio != null) etBio.setText(bio);
            if (tanggal != null) {
                etTanggalLahir.setText(tanggal);
                try {
                    String[] p = tanggal.split("/");
                    calculateAndShowDetails(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2]));
                } catch (Exception ignored) {}
            }
            if (img != null) {
                try {
                    selectedImageUri = Uri.parse(img);
                    ivProfile.setImageURI(selectedImageUri);
                } catch (SecurityException e) {
                    selectedImageUri = null;
                    ivProfile.setImageResource(android.R.drawable.ic_menu_camera);
                } catch (Exception e) {
                    selectedImageUri = null;
                    ivProfile.setImageResource(android.R.drawable.ic_menu_camera);
                }
            }
            
            if (gender != null) {
                if (gender.equals(getString(R.string.laki_laki))) {
                    rgJenisKelamin.check(R.id.rb_laki);
                } else if (gender.equals(getString(R.string.perempuan))) {
                    rgJenisKelamin.check(R.id.rb_perempuan);
                }
            }
            
            if (jurusan != null) {
                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spJurusan.getAdapter();
                int pos = adapter.getPosition(jurusan);
                if (pos >= 0) spJurusan.setSelection(pos);
            }
            
            swStatusAktif.setChecked(status);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void initViews() {
        etNamaLengkap = findViewById(R.id.et_nama_lengkap);
        etTempatLahir = findViewById(R.id.et_tempat_lahir);
        etTanggalLahir = findViewById(R.id.et_tanggal_lahir);
        etHobi = findViewById(R.id.et_hobi);
        etBio = findViewById(R.id.et_bio);
        tvUmurMain = findViewById(R.id.tv_umur_main);
        tvZodiakMain = findViewById(R.id.tv_zodiak_main);
        rgJenisKelamin = findViewById(R.id.rg_jenis_kelamin);
        spJurusan = findViewById(R.id.sp_jurusan);
        swStatusAktif = findViewById(R.id.sw_status_aktif);
        btnSimpan = findViewById(R.id.btn_simpan);
        btnPilihGambar = findViewById(R.id.btn_pilih_gambar);
        ivProfile = findViewById(R.id.iv_profile);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    etTanggalLahir.setText(date);
                    calculateAndShowDetails(dayOfMonth, monthOfYear + 1, year1);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void calculateAndShowDetails(int day, int month, int year) {
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - year;
        if (today.get(Calendar.MONTH) + 1 < month || 
           (today.get(Calendar.MONTH) + 1 == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
            age--;
        }
        tvUmurMain.setText(getString(R.string.umur_label, age));
        tvZodiakMain.setText(getString(R.string.zodiak_label, getZodiac(day, month)));
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

    private void saveAndReturn() {
        String nama = etNamaLengkap.getText().toString().trim();
        String tempatLahir = etTempatLahir.getText().toString().trim();
        String tanggalLahir = etTanggalLahir.getText().toString().trim();
        String hobi = etHobi.getText().toString().trim();
        String bio = etBio.getText().toString().trim();
        
        if (nama.isEmpty() || tanggalLahir.isEmpty()) {
            Toast.makeText(this, "Nama dan Tanggal Lahir harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int selectedGenderId = rgJenisKelamin.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId != -1) {
            RadioButton rbSelected = findViewById(selectedGenderId);
            gender = rbSelected.getText().toString();
        }

        String jurusan = spJurusan.getSelectedItem().toString();
        boolean statusAktif = swStatusAktif.isChecked();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("NAMA", nama);
        resultIntent.putExtra("TEMPAT_LAHIR", tempatLahir);
        resultIntent.putExtra("TANGGAL_LAHIR", tanggalLahir);
        resultIntent.putExtra("HOBI", hobi);
        resultIntent.putExtra("BIO", bio);
        resultIntent.putExtra("GENDER", gender);
        resultIntent.putExtra("JURUSAN", jurusan);
        resultIntent.putExtra("STATUS_AKTIF", statusAktif);
        if (selectedImageUri != null) {
            resultIntent.putExtra("IMAGE_URI", selectedImageUri.toString());
        }
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}