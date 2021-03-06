package me.shufork.common.utils;

import me.shufork.common.dto.supercell.coc.ClanBasicInfoDto;
import me.shufork.common.dto.supercell.coc.ClanDetailedInfoDto;

public abstract class HomeVillageScore {
    public static final int basicScore(ClanBasicInfoDto source){
        return levelScore(source.getClanLevel());
    }
    public static final int basicScore(ClanDetailedInfoDto source){
        return levelScore(source.getClanLevel());
    }
    public static final int pushScore(ClanDetailedInfoDto source){
        return  source.getClanPoints();
    }
    public static final int warScore(ClanDetailedInfoDto source){
        int score =0;
        score += WinStreakScore.calc(source.getWarWinStreak());
        score += source.getWarWins()*50;
        score += source.getWarTies()*30;
        score += source.getWarLosses()*10;
        return score;
    }

    public static final int totalScore(ClanDetailedInfoDto source){
        return basicScore(source) + pushScore(source) + warScore(source);
    }
    private static int levelScore(final int val){
        int level = Math.max(0,val);
        return level * 500 * ((level/5)+1);
    }

    static class WinStreakScore{
        private static int rankScore(int rank,int rankScore,int rankStep){
            return rank * rankScore * ((rank/rankStep)+1);
        }
        public static int calc(final int winStreak){
            int score = 0;
            int streak = Math.max(0,winStreak);

            // 0 ~ 50
            if(streak <= 0 ) return score;
            score += rankScore(  Math.min(streak,50),48,5);

            // 51 ~ 100 extra
            streak-= 50;
            if(streak <= 0 ) return score;
            score += rankScore(Math.min(streak,100),16,5);

            // 101 ~ 300 extra
            streak-= 100;
            if(streak <= 0 ) return score;
            score += rankScore(Math.min(streak,300),3,2);

            // 301 ~  max  extra
            streak-= 300;
            if(streak <= 0 ) return score;
            score += rankScore(streak,5,1);

            return score;
        }
    }

    public static void main(String[] args) {
        int winStreak = 5;
        int score = WinStreakScore.calc(5);
        System.out.println("winStreak "+winStreak+",score = "+score);

        winStreak = 15;
        score = WinStreakScore.calc(15);
        System.out.println("winStreak "+winStreak+",score = "+score);


        winStreak = 25;
        score = WinStreakScore.calc(25);
        System.out.println("winStreak "+winStreak+",score = "+score);

        winStreak = 55;
        score = WinStreakScore.calc(55);
        System.out.println("winStreak "+winStreak+",score = "+score);

        winStreak = 105;
        score = WinStreakScore.calc(105);
        System.out.println("winStreak "+winStreak+",score = "+score);

        winStreak = 205;
        score = WinStreakScore.calc(205);
        System.out.println("winStreak "+winStreak+",score = "+score);

        winStreak = 305;
        score = WinStreakScore.calc(305);
        System.out.println("winStreak "+winStreak+",score = "+score);

        winStreak = 505;
        score = WinStreakScore.calc(505);
        System.out.println("winStreak "+winStreak+",score = "+score);
        System.out.println("done");
    }
}
