package org.rpg.stats;

public class ClientStatsCache {
    private static final PlayerStats stats=new PlayerStats();
    public static PlayerStats getStats(){
        return stats;
    }
}
