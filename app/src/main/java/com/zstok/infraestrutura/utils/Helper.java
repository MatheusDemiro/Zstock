package com.zstok.infraestrutura.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.zstok.R;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    //Método que monta o Toast
    public static void criarToast(Context context, String texto){
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }
    //Método que verifica a expressão regular do email
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
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(editText, smf);

        editText.addTextChangedListener(mtw);
    }
    public static void mascaraTelefone(TextView textView){
        SimpleMaskFormatter smf = new SimpleMaskFormatter("(NN)NNNN-NNNN");
        MaskTextWatcher mtw = new MaskTextWatcher(textView, smf);

        textView.addTextChangedListener(mtw);
    }
    public static void mascaraCelular(TextView textView){
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
        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN.NNN.NNN/NNNN-NN");
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
    //Método que busca qualquer caractere que não seja número e substitui por vazio
    public static String removerMascara(String str){
        return str.replaceAll("\\D", "");
    }
    //Obtendo URI da imagem
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    //Método para remoção de acentos
    public static String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
    //Resgatando data atual
    public static String getData(){
        Date data = new Date();
        SimpleDateFormat formatar = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatar.format(data);
    }
}
