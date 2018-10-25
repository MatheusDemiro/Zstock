package com.zstok.infraestrutura.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyTextWatcher implements TextWatcher {

    private final WeakReference<EditText> editTextWeakReference;
    private final Locale locale;

    public MoneyTextWatcher(EditText editText, Locale locale) {
        this.editTextWeakReference = new WeakReference<EditText>(editText);
        this.locale = locale != null ? locale : Locale.getDefault();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();
        if (editText == null) return;
        editText.removeTextChangedListener(this);

        BigDecimal parsed = parseToBigDecimal(editable.toString(), locale);
        String formatted = NumberFormat.getCurrencyInstance(locale).format(parsed);

        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);
    }
    private static BigDecimal parseToBigDecimal(String value, Locale locale) {
        String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance(locale).getCurrency().getSymbol());

        String cleanString = value.replaceAll(replaceable, "");

        return new BigDecimal(cleanString).setScale(
                2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR
        );
    }
    //Aplicando máscara na string
    public static String convertStringToMoney(String string){
        //Talvez tenha que colocar na activity
        Locale mLocale = new Locale("pt", "BR");
        BigDecimal parsed = parseToBigDecimal(string, mLocale);
        return NumberFormat.getCurrencyInstance(mLocale).format(parsed);
    }
    //Retirando máscara da string
    public static BigDecimal convertToBigDecimal(String value){
        BigDecimal parsed = null;
        try {
            String cleanString = value.replaceAll("[R,$,.]", "");
            parsed = new BigDecimal(cleanString).setScale(2,BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100),BigDecimal.ROUND_FLOOR);
        } catch (Exception e) {
            Log.e("Erro na conversão: ", e.getMessage(), e);
        }
        return parsed;
    }
}