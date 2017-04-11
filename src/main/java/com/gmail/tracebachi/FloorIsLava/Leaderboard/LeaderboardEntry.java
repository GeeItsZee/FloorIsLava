package com.gmail.tracebachi.FloorIsLava.Leaderboard;

/**
 * Created by Jeremy Lugo on 4/10/2017.
 */
public class LeaderboardEntry implements Comparable<LeaderboardEntry>
{
    private String name;
    private int score;

    public LeaderboardEntry(String name, int score)
    {
        this.name = name;
        this.score = score;
    }

    public String getName()
    {
        return name;
    }

    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    @Override
    public int compareTo(LeaderboardEntry o)
    {
        if(score > o.score)
        {
            return 1;
        }
        else if(score < o.score)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }
}
