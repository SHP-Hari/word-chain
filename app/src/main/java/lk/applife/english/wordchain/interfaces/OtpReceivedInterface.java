package lk.applife.english.wordchain.interfaces;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) S.Hariprasanth
 * Date: 17-Nov-20
 * Time: 3:48 AM
 */
public interface OtpReceivedInterface {
    void onOtpReceived(String otp);
    void onOtpTimeout();
}
