package com.zstok.infraestrutura.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    public static void criarToast(Context context, String texto){
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }
    public static boolean verificaExpressaoRegularEmail(String email) {

        if (!email.isEmpty()) {
            String excecoes = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";
            Pattern pattern = Pattern.compile(excecoes);
            Matcher matcher = pattern.matcher(email);

            return matcher.matches();//se igual a true tem alguma expressão irregular.
        }
        return false;
    }
    public static void mascaraTelefone(EditText editText){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(editText, smf);

        editText.addTextChangedListener(mtw);
    }
    public static void mascaraTelefone(TextView textView){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(textView, smf);

        textView.addTextChangedListener(mtw);
    }
    public static void mascaraCpf(EditText editText){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(editText, smf);

        editText.addTextChangedListener(mtw);
    }
    public static void mascaraCpf(TextView textView){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(textView, smf);

        textView.addTextChangedListener(mtw);
    }
    public static void mascaraCnpj(TextView textView){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NNN.NNN.NNN/NNNN-NN");
        MaskTextWatcher mtw = new MaskTextWatcher(textView, smf);

        textView.addTextChangedListener(mtw);
    }
    public static void mascaraDataNascimento(EditText editText){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(editText, smf);

        editText.addTextChangedListener(mtw);
    }
    public static void mascaraDataNascimento(TextView textView){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN/NN/NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(textView, smf);

        textView.addTextChangedListener(mtw);
    }
    public static String removerMascara(String str){
        return str.replaceAll("\\D", "");
    }
}
