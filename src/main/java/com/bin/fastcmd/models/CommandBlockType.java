package com.bin.fastcmd.models;

/**
 * Enum representing different types of command blocks
 */
public enum CommandBlockType {
    NORMAL("Normal", "Обычный"),
    CHAIN("Chain", "Цепной"),
    REPEATING("Repeating", "Цикличный");
    
    private final String englishName;
    private final String russianName;
    
    CommandBlockType(String englishName, String russianName) {
        this.englishName = englishName;
        this.russianName = russianName;
    }
    
    public String getEnglishName() {
        return englishName;
    }
    
    public String getRussianName() {
        return russianName;
    }
    
    public String getLocalizedName(String language) {
        if ("ru".equals(language)) {
            return russianName;
        }
        return englishName;
    }
    
    public static CommandBlockType fromString(String name) {
        for (CommandBlockType type : values()) {
            if (type.englishName.equalsIgnoreCase(name) || type.russianName.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    public static String[] getLocalizedNames(String language) {
        CommandBlockType[] types = values();
        String[] names = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            names[i] = types[i].getLocalizedName(language);
        }
        return names;
    }
}
