package com.example.drumapp;

import java.util.Date;
import java.util.concurrent.TimeUnit;

// this class to calculate time
public class TimeAgo {
    public String getTimeAgo(long duration)
    {
        Date now = new Date();

        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - duration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - duration);
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - duration);
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - duration);

        if(seconds < 60 )
        {
            return "just now";
        }
        else if (minutes == 1)
            return "1 minute ago";
        else if (minutes > 1 && minutes < 60)
            return minutes + " minutes ago";
        else if (hours == 1)
            return "1 hour ago";
        else if (hours > 1 && hours < 24)
            return hours + " hours ago";
        else if (days == 1)
            return  "1 day ago";
        else
            return days + " days ago";
    }
}
