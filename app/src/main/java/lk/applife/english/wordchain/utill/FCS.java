package lk.applife.english.wordchain.utill;

import android.animation.ArgbEvaluator;
import android.animation.TimeAnimator;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * Created by Android Studio.
 *
 * @author Great Hari <greathari777@gmail.com>
 * @copyright (c) greathari777@gmail.com
 * Date: 31-May-20
 * Time: 7:18 PM
 */
public class FCS extends ForegroundColorSpan implements TimeAnimator.TimeListener {

    private TextView tv;
    private int[] colors;
    private int color;
    TimeAnimator animator;
    ArgbEvaluator evaluator;

    public FCS(TextView tv, int[] colors) {
        super(colors[0]);
        this.tv = tv;
        this.colors = colors;
        animator = new TimeAnimator();
        animator.setTimeListener(this);
        evaluator = new ArgbEvaluator();
        animator.start();
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        float sin = (float) (Math.sin(Math.PI * totalTime / 1000f));
        float fraction = sin * sin;
//        float fraction = (float) (-Math.cos(2*Math.PI * totalTime / 1000f) + 1) / 2f;
        color = (int) evaluator.evaluate(fraction, colors[0], colors[1]);
        tv.invalidate();
        if (totalTime > 20000) {
            animator.end();
        }
    }
}
