package game;

public class GameState {
    public enum Screen {
        MAIN_MENU,
        CHARACTER_CREATION,
        PLAYING,
        PAUSED,
        SETTINGS,
        SKILL_TREE,
        CRAFT,
        ACHIEVEMENTS,
        QUESTS,
        DIALOGUE,
        GAME_OVER,
        INVENTORY
    }

    public enum Location {
        WORLD,
        DUNGEON
    }

    private Screen screen = Screen.MAIN_MENU;
    private Location location = Location.WORLD;
    private Screen previousScreen = Screen.MAIN_MENU;

    public Screen getScreen() { return screen; }
    public void setScreen(Screen s) {
        previousScreen = screen;
        this.screen = s;
    }
    public Screen getPreviousScreen() { return previousScreen; }

    public Location getLocation() { return location; }
    public void setLocation(Location l) { this.location = l; }

    public boolean isPlaying() { return screen == Screen.PLAYING; }
    public boolean isPaused() { return screen == Screen.PAUSED; }
    public boolean isInDungeon() { return location == Location.DUNGEON; }
}