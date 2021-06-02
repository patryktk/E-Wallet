package pl.tkaczyk.walletapp;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;

public class DecimalDigitalFilter extends DigitsKeyListener {

    public DecimalDigitalFilter() {
        super(false,true);
    }

    private int digit = 2;

    public void setDigits(int d){
        digit = d;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        CharSequence out = super.filter(source,start,end,dest,dstart,dend);

        //if change replace source
        if(out != null){
            source = out;
            start = 0;
            end = out.length();
        }
        int len = end - start;

        //if deleting source is empty
        //and deleting can't break anything
        if(len == 0){
            return source;
        }
        int dlen = dest.length();

        for(int i=0; i < dstart; i++){
            if(dest.charAt(i) == '.'){
                // being here means, that a number has
                // been inserted after the dot
                // check if the amount of digits is right
                return (dlen-(i+1) + len > digit) ? "" : new SpannableStringBuilder(source, start, end);
            }
        }
        for(int i=0; i < end; i ++){
            if(source.charAt(i) == '.'){
                // being here means, dot has been inserted
                // check if the amount of digits is right
                if((dlen - dend) + (end - (i+1)) > digit){
                    return "";
                }else{
                    break;
                    // return new SpannableStringBuilder(source, start, end);
                }
            }
        }
        // if the dot is after the inserted part,
        // nothing can break
        return new SpannableStringBuilder(source, start, end);
    }
}
