package is.handsome.labs.iotfoosball;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

public class CurrentGame {
    private Game mGame;
    private boolean mIsGameStarted;
    private ArrayList<IncludePlayer> mIncludePlayers;
    private DatabaseReference mReference;
    private int mThreshold;
    private int mScoreA;
    private Scorebar mScorebarA;
    private int mScoreB;
    private Scorebar mScorebarB;
    private ArrayList<String> mPlayersId;
    private FirebaseListPlayers mFirebaseListPlayers;

    public CurrentGame(ArrayList<IncludePlayer> includePlayers,
            DatabaseReference databaseReference,
            Scorebar teamA,
            Scorebar teamB,
            FirebaseListPlayers firebaseListPlayers) {
        mGame = new Game();
        mIsGameStarted = false;
        this.mScorebarA = teamA;
        mScoreA = 0;
        this.mScorebarB = teamB;
        mScoreB = 0;
        this.mFirebaseListPlayers = firebaseListPlayers;
        this.mIncludePlayers = includePlayers;
        this.mReference = databaseReference;
        mThreshold = 50;
        mPlayersId = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            mPlayersId.add(i, "");
        }
    }

    public void setThreshold(int threshold) {
        this.mThreshold = threshold;
    }

    public void setMode(String mode) {
        mGame.setMode(mode);
    }

    public void startGame() {
        if (!mIsGameStarted) {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);
            String datestart = dateFormat.format(Calendar.getInstance().getTime());
            Log.d("gamesave", "set datestart to " + datestart);
            mGame.setDateStart(datestart);
            mIsGameStarted = true;
        }
    }

    public void endGame() {
        if (mIsGameStarted) {
            mIsGameStarted = false;
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ROOT);
            String dateend = dateFormat.format(Calendar.getInstance().getTime());
            Log.d("gamesave", "set dateend to " + dateend);
            mGame.setDateEnd(dateend);
            mGame.setIds(mPlayersId.get(0),
                    mPlayersId.get(1),
                    mPlayersId.get(2),
                    mPlayersId.get(3));
            mGame.setMode("2x2mb");
            mGame.setScore(mScoreA, mScoreB);
            mReference.push().setValue(mGame);
        }
    }

    public void notifyScored(String teamScored) {
        Timber.d("game notified");
        if (teamScored.equals("A")) {
            mScoreA++;
            mScorebarA.setScore(mScoreA);
        }
        if (teamScored.equals("B")) {
            mScoreB++;
            mScorebarB.setScore(mScoreB);
        }
        if ((mScoreA >= mThreshold)|| (mScoreB >= mThreshold)) endGame();
    }

    public void notifyListed(Scorebar scorebar, int position) {
        if(scorebar == mScorebarA) {
            mScoreA = position;
        }
        if (scorebar == mScorebarB) {
            mScoreB = position;
        }
    }

    public void notifyDraged(int position, int index) {
        mPlayersId.set(position, mFirebaseListPlayers.getKeyList().get(index));
        mIncludePlayers.get(position)
                .nick
                .setText(mFirebaseListPlayers.getDataList().get(index).getNick());
        String score = String.format(Locale.US, "%d:%d",
                mFirebaseListPlayers.getDataList().get(index).getWins(),
                mFirebaseListPlayers.getDataList().get(index).getLoses());
        mIncludePlayers.get(position).score.setText(score);

        mFirebaseListPlayers.getFirebaseImgSetter()
                .setAvatar(mFirebaseListPlayers.getDataList().get(index).getNick(),
                        mIncludePlayers.get(position).avatar);
    }

    public String getPlayerId(int position) {
        return mPlayersId.get(position);
    }
}