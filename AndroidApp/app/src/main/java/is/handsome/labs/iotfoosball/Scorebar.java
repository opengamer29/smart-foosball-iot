package is.handsome.labs.iotfoosball;

import android.content.Context;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

public class Scorebar {
    //TODO devide to model and view
    private ArrayList<ScoreViewPager> mScorebarsView;
    private CurentGame mCurentGame;

    public Scorebar (Context context, final ArrayList<ScoreViewPager> scorebarsView) {
        this.mScorebarsView = scorebarsView;
        scorebarsView.get(0).addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position,
                    float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (scorebarsView.size() > 1) {
                    scorebarsView.get(1).setCurrentItem(((position - (position % 10)) / 10), true);
                }
                if (mCurentGame != null) mCurentGame.notifyListed(Scorebar.this, position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        scorebarsView.get(0).setAdapter(new ScorebarPagerAdapter(context,
                (int) Math.pow(10,scorebarsView.size())));

        for (int i = 1; i < scorebarsView.size(); i++) {
            ScorebarPagerAdapter spAdapter = new ScorebarPagerAdapter(context,
                    (int) Math.pow(10,scorebarsView.size()-1));

            scorebarsView.get(i).setFirstDigitViewPager(scorebarsView.get(0));
            scorebarsView.get(i).setListing(false);
            scorebarsView.get(i).setAdapter(spAdapter);
            if (i != (scorebarsView.size() - 1)) {
                final int finalI = i;
                scorebarsView.get(i).addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position,
                            float positionOffset,
                            int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        scorebarsView.get(finalI +1)
                                .setCurrentItem(((position - (position % 10)) / 10), true);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
    }

    public void setCurentGame(CurentGame curentGame) {
        this.mCurentGame = curentGame;
    }

    public void setScore(int score) {
        mScorebarsView.get(0).setCurrentItem(score);
    }
}