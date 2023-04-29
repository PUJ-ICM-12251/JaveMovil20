package com.example.javemovil20;

import androidx.activity.result.ActivityResultLauncher;
        import androidx.appcompat.app.AppCompatActivity;

        import android.os.Bundle;
        import android.view.View;
        import android.widget.Toast;

import com.example.javemovil20.databinding.ActivityQractivityBinding;
import com.journeyapps.barcodescanner.CaptureActivity;
        import com.journeyapps.barcodescanner.ScanContract;
        import com.journeyapps.barcodescanner.ScanOptions;

        import java.util.Scanner;

public class QRActivity extends AppCompatActivity {
    ActivityQractivityBinding binding;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null) {

            binding.textView.setText(result.getContents());
        }else{
            Toast.makeText(this, "cancelado", Toast.LENGTH_SHORT).show();
        }
    });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQractivityBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.btnCamaraQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escanear();
            }
        });
    }

    public void escanear() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt("ESCANEAR CODIGO");
        options.setCameraId(0);
        options.setOrientationLocked(false);
        options.setBeepEnabled(true);
        options.setCaptureActivity(CaptureActivityPortraint.class);
        options.setBarcodeImageEnabled(false);

        barcodeLauncher.launch(options);


    }
}
