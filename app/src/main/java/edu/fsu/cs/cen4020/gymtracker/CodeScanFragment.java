package edu.fsu.cs.cen4020.gymtracker;
// https://github.com/yuriy-budiyev/code-scanner

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class CodeScanFragment extends Fragment {
    public static final String INTENT_KEY_SECOND_FRAGMENT_DATA = "CodeScanFragment";
    public static final String INTENT_QR_CODE_KEY = "CodeScanFragment_QR_CODE";
    private CodeScanner mCodeScanner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        final View root = inflater.inflate(R.layout.fragment_code_scanner, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {




//                        Intent intent = new Intent();
//                        intent.putExtra(CodeScanFragment.INTENT_KEY_SECOND_FRAGMENT_DATA, qr_code);

                        String qr_code = result.getText();


                        Bundle bundle = new Bundle();
                        bundle.putString(INTENT_QR_CODE_KEY, qr_code);

                        // Dismiss the fragment
                        Navigation.findNavController(root).navigate(R.id.scanFragment,bundle);

                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}