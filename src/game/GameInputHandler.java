package game;

import entity.*;
import item.Potion;
import magic.Spell;
import ui.MainMenu;
import ui.PauseMenu;
import ui.SettingsMenu;
import world.Portal;

import java.awt.event.KeyEvent;
import java.util.List;

public class GameInputHandler {
    private Game game;

    public GameInputHandler(Game game) {
        this.game = game;
    }

    // ========== ГЛАВНОЕ МЕНЮ ==========
    public void handleMainMenu(int code) {
        MainMenu.Action action = game.mainMenu.handleKey(code);
        executeMainMenuAction(action);
    }

    public void handleMainMenuClick() {
        MainMenu.Action action = game.mainMenu.handleClick(
                game.input.getMouseX(),
                game.input.getMouseY(),
                game.getWidth(),
                game.getHeight());
        executeMainMenuAction(action);
    }

    private void executeMainMenuAction(MainMenu.Action action) {
        switch (action) {
            case NEW_GAME -> {
                SoundManager.play("menu");
                game.transition.start(() -> {
                    game.state.setScreen(GameState.Screen.CHARACTER_CREATION);
                    game.mainMenu.reset();
                });
            }
            case SETTINGS -> {
                SoundManager.play("menu");
                game.transition.start(() -> {
                    game.state.setScreen(GameState.Screen.SETTINGS);
                    game.settingsMenu.reset();
                    game.mainMenu.reset();
                });
            }
            case LOAD_GAME -> {
                SoundManager.play("menu");
                if (SaveManager.hasSave(1)) {
                    SaveManager.load(game, 1);
                } else {
                    game.showToast("Нет сохранений");
                }
            }
            case CREDITS -> game.showToast("Авторы: ты сам :)");
            case QUIT -> System.exit(0);
        }
    }

    // ========== СОЗДАНИЕ ПЕРСОНАЖА ==========
    public void handleCharacterCreation(int code) {
        game.creationUI.handleKey(code);
        if (game.creationUI.done) {
            game.player.setAppearance(game.creationUI.appearance);
            game.transition.start(() -> {
                game.state.setScreen(GameState.Screen.PLAYING);
                game.loadWorld();
            });
        }
    }

    // ========== ПАУЗА ==========
    public void handlePause(int code) {
        PauseMenu.Action action = game.pauseMenu.handleKey(code);
        executePauseAction(action);
    }

    public void handlePauseClick() {
        PauseMenu.Action action = game.pauseMenu.handleClick(
                game.input.getMouseX(),
                game.input.getMouseY(),
                game.getWidth(),
                game.getHeight());
        executePauseAction(action);
    }

    private void executePauseAction(PauseMenu.Action action) {
        switch (action) {
            case RESUME -> {
                SoundManager.play("menu");
                game.transition.start(() -> {
                    game.state.setScreen(GameState.Screen.PLAYING);
                    game.pauseMenu.reset();
                    game.input.reset();
                });
            }
            case SETTINGS -> {
                SoundManager.play("menu");
                game.transition.start(() -> {
                    game.state.setScreen(GameState.Screen.SETTINGS);
                    game.settingsMenu.reset();
                    game.pauseMenu.reset();
                });
            }
            case MAIN_MENU -> {
                SoundManager.play("menu");
                SoundManager.playMusic("music_menu");
                game.transition.start(() -> {
                    game.state.setScreen(GameState.Screen.MAIN_MENU);
                    game.pauseMenu.reset();
                    game.mainMenu.reset();
                    game.input.reset();
                });
            }
            case QUIT -> System.exit(0);
        }
    }

    // ========== НАСТРОЙКИ ==========
    public void handleSettings(int code) {
        SettingsMenu.Action action = game.settingsMenu.handleKey(code);
        if (action == SettingsMenu.Action.BACK) {
            SoundManager.play("menu");
            game.transition.start(() -> {
                if (game.state.getPreviousScreen() == GameState.Screen.PAUSED) {
                    game.state.setScreen(GameState.Screen.PAUSED);
                } else {
                    game.state.setScreen(GameState.Screen.MAIN_MENU);
                }
                game.settingsMenu.reset();
                game.input.reset();
            });
        }
    }

    // ========== ДЕРЕВО СКИЛЛОВ ==========
    public void handleSkillTree(int code) {
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_K) {
            SoundManager.play("menu");
            game.transition.start(() -> {
                game.state.setScreen(GameState.Screen.PLAYING);
                game.input.reset();
            });
            return;
        }
        game.skillTreeUI.handleKey(code, game.player);
    }

    // ========== КРАФТ ==========
    public void handleCraft(int code) {
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_C) {
            SoundManager.play("menu");
            game.transition.start(() -> {
                game.state.setScreen(GameState.Screen.PLAYING);
                game.input.reset();
            });
            return;
        }
        game.craftUI.handleKey(code, game.player);
    }

    // ========== ДОСТИЖЕНИЯ ==========
    public void handleAchievements(int code) {
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_B) {
            SoundManager.play("menu");
            game.transition.start(() -> {
                game.state.setScreen(GameState.Screen.PLAYING);
                game.input.reset();
            });
        }
    }

    // ========== КВЕСТЫ ==========
    public void handleQuests(int code) {
        if (code == KeyEvent.VK_ESCAPE || code == KeyEvent.VK_J) {
            SoundManager.play("menu");
            game.transition.start(() -> {
                game.state.setScreen(GameState.Screen.PLAYING);
                game.input.reset();
            });
            return;
        }
        game.questUI.handleKey(code, game.questSystem, game.player);
    }

    // ========== GAME OVER ==========
    public void handleGameOver(int code) {
        if (code == KeyEvent.VK_R) {
            game.player.respawn();
            game.enemies.clear();
            game.input.reset();
            if (game.portalManager.inDungeon) game.exitDungeon();
            game.transition.start(() -> {
                game.state.setScreen(GameState.Screen.PLAYING);
                SoundManager.playMusic("music_world");
            });
            return;
        }
        if (code == KeyEvent.VK_ESCAPE) {
            SoundManager.play("menu");
            SoundManager.playMusic("music_menu");
            game.transition.start(() -> {
                game.state.setScreen(GameState.Screen.MAIN_MENU);
                game.input.reset();
                game.mainMenu.reset();
            });
        }
    }

    // ========== ИГРОВОЙ ПРОЦЕСС ==========
    public void handleGameplay(int code) {
        // Escape
        if (code == KeyEvent.VK_ESCAPE) {
            if (game.state.getScreen() == GameState.Screen.INVENTORY) {
                game.state.setScreen(GameState.Screen.PLAYING);
            } else {
                SoundManager.play("menu");
                game.state.setScreen(GameState.Screen.PAUSED);
                game.pauseMenu.reset();
            }
            game.input.reset();
            return;
        }

        // Инвентарь
        if (code == KeyEvent.VK_I) {
            SoundManager.play("menu");
            game.state.setScreen(game.state.getScreen() == GameState.Screen.INVENTORY
                    ? GameState.Screen.PLAYING : GameState.Screen.INVENTORY);
            game.input.reset();
            return;
        }

        // T — NPC
        if (code == KeyEvent.VK_T) {
            NPC near = game.npcManager.findNPCNear(game.player);
            if (near != null) {
                game.dialogueUI.open(near);
                SoundManager.play("menu");
                game.achievements.onNPCSpoken();
            } else {
                game.showToast("Нет NPC рядом");
            }
            game.input.reset();
            return;
        }

        // K — скиллы
        if (code == KeyEvent.VK_K) {
            SoundManager.play("menu");
            game.transition.start(() -> {
                game.skillTreeUI.open(game.skillTree);
                game.state.setScreen(GameState.Screen.SKILL_TREE);
            });
            game.input.reset();
            return;
        }

        // C — крафт
        if (code == KeyEvent.VK_C) {
            SoundManager.play("menu");
            game.transition.start(() -> {
                game.craftUI.open(game.craftSystem);
                game.state.setScreen(GameState.Screen.CRAFT);
            });
            game.input.reset();
            return;
        }

        // B — достижения
        if (code == KeyEvent.VK_B) {
            SoundManager.play("menu");
            game.transition.start(() ->
                    game.state.setScreen(GameState.Screen.ACHIEVEMENTS));
            game.input.reset();
            return;
        }

        // J — квесты
        if (code == KeyEvent.VK_J) {
            SoundManager.play("menu");
            game.transition.start(() ->
                    game.state.setScreen(GameState.Screen.QUESTS));
            game.input.reset();
            return;
        }

        // E — портал
        if (code == KeyEvent.VK_E) {
            if (!game.portalManager.inDungeon) {
                Portal near = game.portalManager.findPortalNear(game.player);
                if (near != null) {
                    if (near.canEnter(game.player.getLevel())) {
                        game.transition.start(() -> game.enterDungeon(near));
                    } else {
                        game.showToast("Нужен уровень " + near.difficulty.minLevel);
                    }
                }
            } else {
                game.transition.start(() -> game.exitDungeon());
            }
            return;
        }

        // Q — зелье / выход
        if (code == KeyEvent.VK_Q) {
            if (game.portalManager.inDungeon) {
                game.transition.start(() -> game.exitDungeon());
            } else {
                useHealthPotion();
            }
            return;
        }

        // F — рывок
        if (code == KeyEvent.VK_F && game.dashCooldown == 0) {
            game.player.dash();
            game.particles.spawnMagicBurst(
                    game.player.x + game.player.width / 2.0,
                    game.player.y + game.player.height / 2.0,
                    ColorCache.rgb(100, 200, 255));
            game.dashCooldown = Config.PLAYER_DASH_COOLDOWN;
        }

        // Статы
        if (code == KeyEvent.VK_1) game.player.spendPoint(Stats.ScalingStat.STRENGTH);
        if (code == KeyEvent.VK_2) game.player.spendPoint(Stats.ScalingStat.DEXTERITY);
        if (code == KeyEvent.VK_3) game.player.spendPoint(Stats.ScalingStat.INTELLIGENCE);
        if (code == KeyEvent.VK_4) game.player.spendVitality();

        // Пробел — атака
        if (code == KeyEvent.VK_SPACE) game.attackEnemies();
    }

    private void useHealthPotion() {
        for (int i = 0; i < game.player.getInventory().getItems().size(); i++) {
            var it = game.player.getInventory().getItems().get(i);
            if (it instanceof Potion p && p.kind == Potion.Kind.HEALTH) {
                game.player.hp = Math.min(game.player.maxHp,
                        game.player.hp + p.value);
                game.player.getInventory().remove(it);
                SoundManager.play("pickup");
                game.damageNumbers.add(new DamageNumber(
                        game.player.x + game.player.width / 2.0,
                        game.player.y,
                        "+" + p.value,
                        ColorCache.rgb(80, 220, 100)));
                game.showToast("Зелье HP (+" + p.value + ")");
                return;
            }
        }
        game.showToast("Нет зелий HP");
    }
}