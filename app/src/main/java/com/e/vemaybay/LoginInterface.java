package com.e.vemaybay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class LoginInterface extends AppCompatActivity {
    SharedPreferences prefs = null;
    EditText userName;
    EditText passWord;
    Button login;
    Button finger;
    Button quit;
    TextView forgot;
    TextView create;
    DBHelper DB;
    private Executor executor;
    BiometricManager biometricManager;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_interface);
        DB = new DBHelper(this);
        prefs = getSharedPreferences("com.e.vemaybay", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            prefs.edit().putBoolean("firstrun", false).apply();
            boolean create = DB.CreateDefaultRule();
            if (create)
            {
                Toast.makeText(this, "Set default rules for first use", Toast.LENGTH_SHORT).show();
                DB.AddAirport("SB1", "Tan Son Nhat", "Ho Chi Minh", "Viet Nam");
                DB.AddAirport("SB2", "Noi Bai", "Ha Noi", "Viet Nam");
                DB.AddTuyenBay("TB1", "SB1", "SB2");
                DB.AddTicketLevel("HV1", "Pho Thong", "100");
            }
        }
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginInterface.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(LoginInterface.this,"L???i!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent myIntent = new Intent(LoginInterface.this, MainActivity.class);
                LoginInterface.this.startActivity(myIntent);
                Toast.makeText(LoginInterface.this,"B???n ??ang ????ng nh???p v???i t?? c??ch admin", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginInterface.this,"Sinh tr???c h???c kh??ng kh???p", Toast.LENGTH_SHORT).show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("X??c th???c sinh tr???c h???c").setSubtitle("H??y ?????t v??n tay v??o ?? tr??n").setNegativeButtonText("D??ng m???t kh???u").build();
        userName = findViewById(R.id.ed_Username);
        passWord = findViewById(R.id.ed_Password);
        login = findViewById(R.id.btn_login);
        forgot = findViewById(R.id.txt_forgot);
        create = findViewById(R.id.txt_create);
        finger = findViewById(R.id.btn_finger);
        quit = findViewById(R.id.btn_quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(v.getContext());
                mydialog.setTitle("X??c nh???n tho??t");
                mydialog.setMessage("B???n c?? mu???n tho??t kh???i ???ng d???ng kh??ng?");
                mydialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);

                    }
                });
                mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mydialog.show();

            }
        });
        finger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userName.getText().toString().isEmpty() || passWord.getText().toString().isEmpty())
                {
                    Toast.makeText(v.getContext(), "Ch??a nh???p ?????y ????? th??ng tin", Toast.LENGTH_SHORT).show();
                }
                else {
                    Boolean login = false;
                    int usrType = 0;
                    Cursor cursor = DB.getUser(userName.getText().toString());
                    if (cursor.getCount() == 0) {
                        Toast.makeText(v.getContext(), "Sai t??n ????ng nh???p ho???c m???t kh???u", Toast.LENGTH_SHORT).show();
                        cursor.close();
                    } else {
                        while (cursor.moveToNext()) {
                            if (passWord.getText().toString().equals(cursor.getString(1))) {
                                login = true;
                                usrType = Integer.parseInt(cursor.getString(2));
                            }
                            if (login) {
                                if (usrType == 1) {
                                    Toast.makeText(v.getContext(), "B???n ??ang ????ng nh???p v???i t?? c??ch admin", Toast.LENGTH_SHORT).show();
                                    Intent myIntent = new Intent(LoginInterface.this, MainActivity.class);
                                    //myIntent.putExtra("key", value); //Optional parameters
                                    LoginInterface.this.startActivity(myIntent);
                                } else {
                                    Intent myIntent = new Intent(LoginInterface.this, BookInfo.class);
                                    //myIntent.putExtra("key", value); //Optional parameters
                                    LoginInterface.this.startActivity(myIntent);
                                    Toast.makeText(v.getContext(), "B???n ??ang ????ng nh???p v???i t?? c??ch kh??ch h??ng", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(v.getContext(), "Sai t??n ????ng nh???p ho???c m???t kh???u", Toast.LENGTH_SHORT).show();
                            }
                        }
                        cursor.close();
                    }
                }
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mydialog = new AlertDialog.Builder(v.getContext());
                View mVew = getLayoutInflater().inflate(R.layout.createselect, null);
                mydialog.setTitle("L???a ch???n lo???i t??i kho???n");
                mydialog.setView(mVew);
                Button admin = mVew.findViewById(R.id.btn_sel_admin);
                Button guest = mVew.findViewById(R.id.btn_sel_guest);
                admin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder mydialog = new AlertDialog.Builder(v.getContext());
                        final View mVew = getLayoutInflater().inflate(R.layout.createformadmin, null);
                        final EditText usrname = mVew.findViewById(R.id.ed_create_usr);
                        final EditText passw = mVew.findViewById(R.id.ed_create_pass);
                        final EditText secure = mVew.findViewById(R.id.ed_create_security);
                        mydialog.setTitle("Nh???p th??ng tin");
                        mydialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (usrname.getText().toString().isEmpty() ||passw.getText().toString().isEmpty() ||secure.getText().toString().isEmpty())
                                {
                                    Toast.makeText(mVew.getContext(), "Ch??a nh???p ?????y ????? th??ng tin", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Cursor c = DB.getRule();
                                    String securecode = null;
                                    while (c.moveToNext())
                                    {
                                        securecode = c.getString(5);
                                    }
                                    c.close();
                                    if (secure.getText().toString().equals(securecode))
                                    {
                                       Boolean check =  DB.AddUser(usrname.getText().toString(),passw.getText().toString(),"1");
                                       if (check)
                                       {
                                           Toast.makeText(mVew.getContext(), "T???o th??nh c??ng", Toast.LENGTH_SHORT).show();
                                       }
                                       else
                                       {
                                           Toast.makeText(mVew.getContext(), "T???o th???t b???i", Toast.LENGTH_SHORT).show();
                                       }
                                    }
                                    else
                                    {
                                        Toast.makeText(mVew.getContext(), "Sai m?? b???o m???t", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                        mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        mydialog.setView(mVew);
                        mydialog.show();
                    }
                });
                guest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        AlertDialog.Builder mydialog = new AlertDialog.Builder(v.getContext());
//                        final View mVew = getLayoutInflater().inflate(R.layout.createform, null);
//                        final EditText usrname = mVew.findViewById(R.id.ed_create_usr);
//                        final EditText passw = mVew.findViewById(R.id.ed_create_pass);
//                        mydialog.setTitle("Nh???p th??ng tin");
//                        mydialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (usrname.getText().toString().isEmpty() ||passw.getText().toString().isEmpty() )
//                                {
//                                    Toast.makeText(mVew.getContext(), "Ch??a nh???p ?????y ????? th??ng tin", Toast.LENGTH_SHORT).show();
//                                }
//                                else
//                                {
//                                        Boolean check =  DB.AddUser(usrname.getText().toString(),passw.getText().toString(),"0");
//                                        if (check)
//                                        {
//                                            Toast.makeText(mVew.getContext(), "T???o th??nh c??ng", Toast.LENGTH_SHORT).show();
//                                        }
//                                        else
//                                        {
//                                            Toast.makeText(mVew.getContext(), "T???o th???t b???i", Toast.LENGTH_SHORT).show();
//                                        }
//                                }
//                            }
//                        });
//                        mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.cancel();
//                            }
//                        });
//                        mydialog.setView(mVew);
//                        mydialog.show();
                        Toast.makeText(v.getContext(), "T??nh n??ng ch??a ph??t tri???n", Toast.LENGTH_SHORT).show();
                    }
                });
                mydialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                mydialog.show();
            }
        });
    }
    @Override
    public void onRestart() {
        super.onRestart();
        userName.setText("");
        passWord.setText("");
    }
}