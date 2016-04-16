package info.dourok.weimagepicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

import java.lang.reflect.Field;

/**
 * Created by DouO on 2016/1/20.
 */
public class CustomButton extends Button {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            Class clazz = Class.forName("com.android.internal.R$styleable");
            Field field = clazz.getDeclaredField("View");

            int[] View = (int[]) field.get(clazz);
            int View_background = clazz.getDeclaredField("View_background").getInt(clazz);
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, View, 0, 0);
            System.out.println(a.peekValue(View_background).coerceToString());
            a.getDrawable(View_background);
            a.recycle();

//            for (Field f : clazz.getDeclaredFields()) {
//                System.out.println(f.getName());
//            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public CustomButton(Context context) {
        super(context);
    }
}
