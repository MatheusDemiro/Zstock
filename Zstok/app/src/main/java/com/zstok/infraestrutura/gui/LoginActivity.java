package com.zstok.infraestrutura.gui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.zstok.R;
import com.zstok.pessoa.gui.RegistroActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        TextView tvRegistreSe = findViewById(R.id.tvRegistreSe);

        tvRegistreSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirTelaRegistro();
            }
        });

    }
    private void abrirTelaRegistro(){
        Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
        startActivity(intent);
    }
}
