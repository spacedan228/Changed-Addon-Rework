package net.foxyas.changedaddon.world.gamerules;

public enum WorldDifficulty {

    NONE(-1),
    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3),
    HARDCORE(4);

    private final int level;

    WorldDifficulty(int level) {
        this.level = level;
    }

    public int getAsLevel() {
        return level;
    }
}
