package net.foxyas.changedaddon.world.gamerules;

public enum ChangedEntitySpawnDressedType {
    NON_LATEX,
    LATEX,
    ANY,
    NONE;

    public static ChangedEntitySpawnDressedType fromString(String value) {
        try {
            return ChangedEntitySpawnDressedType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NONE;
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
