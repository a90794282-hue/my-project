package game;

import entity.NPC;
import entity.Player;
import world.TileMap;

import java.util.ArrayList;
import java.util.List;

public class NPCManager {
    private List<NPC> npcs = new ArrayList<>();

    public void spawnNPCsInWorld(TileMap map, Player player) {
        npcs.clear();

        // Торговец
        NPC merchant = new NPC("Торговец",
                (int) player.x + 200, (int) player.y + 100);
        merchant.shop = true;
        merchant.setDialogue(
                "Приветствую, путник!",
                "У меня есть товары на любой вкус.",
                "Заходи, если что-то нужно."
        );
        npcs.add(merchant);

        // Квестодатель
        NPC questGiver = new NPC("Старейшина",
                (int) player.x - 200, (int) player.y + 150);
        questGiver.setDialogue(
                "Сынок, у нас беда!",
                "Монстры нападают на деревню.",
                "Помоги нам, и я щедро награжу тебя!"
        );
        questGiver.setQuest("Защитник деревни",
                "Убить 10 монстров", 10, 300, 150);
        npcs.add(questGiver);

        // Мастер
        NPC master = new NPC("Мастер",
                (int) player.x + 150, (int) player.y - 200);
        master.setDialogue(
                "Хочешь стать сильнее?",
                "Тренируйся и прокачивайся.",
                "Удачи, воин."
        );
        master.setQuest("Испытание силы",
                "Достичь 3 уровня", 3, 200, 100);
        npcs.add(master);

        // Загадочный странник
        NPC wanderer = new NPC("Странник",
                (int) player.x - 300, (int) player.y - 100);
        wanderer.setDialogue(
                "Я видел то, что тебе не снилось...",
                "Тёмные силы пробудились...",
                "Берегись!",
                "Хотя, тебе не понять..."
        );
        npcs.add(wanderer);
    }

    public NPC findNPCNear(Player player) {
        for (NPC npc : npcs) {
            if (npc.isPlayerNear(player.x + player.width / 2.0,
                    player.y + player.height / 2.0)) {
                return npc;
            }
        }
        return null;
    }

    public List<NPC> getNPCs() { return npcs; }
    public void clear() { npcs.clear(); }
}