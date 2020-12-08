package lk.applife.english.wordchain.utill;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) greathari777@gmail.com
 * Date: 08-Dec-20
 * Time: 11:16 PM
 */
public class LocaleConfigurationUtil {

    public static Context adjustFontSize(Context context){
        Configuration configuration = context.getResources().getConfiguration();
        // This will apply to all text like -> Your given text size * fontScale
        configuration.fontScale = 0.8f;
        return context.createConfigurationContext(configuration);
    }
}
