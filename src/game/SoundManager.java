package game;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private static final Map<String, Clip> clips = new HashMap<>();
    private static Clip musicClip = null;
    private static String musicName = null;

    // ============= ЗАГРУЗКА =============

    public static void load(String name) {
        if (clips.containsKey(name)) {
            System.out.println("[Sound] Уже загружен: " + name);
            return;
        }
        try {
            URL url = SoundManager.class.getResource("/sounds/" + name + ".wav");
            System.out.println("[Sound] Пытаюсь загрузить: " + name + " → URL = " + url);

            if (url == null) {
                System.err.println("[Sound] ❌ НЕ НАЙДЕН: " + name);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            long frames = clip.getFrameLength();
            float sec = (float) frames / clip.getFormat().getFrameRate();
            System.out.println("[Sound] ✅ Загружен " + name
                    + " | длина: " + String.format("%.2f", sec) + " сек");

            applyVolume(clip, Config.sfxVolume);
            clips.put(name, clip);
        } catch (Exception e) {
            System.err.println("[Sound] ❌ Ошибка загрузки " + name + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============= SFX =============

    public static void play(String name) {
        System.out.println("[Sound] play('" + name + "') вызван");

        if (!Config.soundEnabled) {
            System.out.println("[Sound]   → звук ВЫКЛ");
            return;
        }
        if (Config.sfxVolume <= 0) {
            System.out.println("[Sound]   → громкость 0");
            return;
        }

        Clip clip = clips.get(name);
        if (clip == null) {
            System.out.println("[Sound]   → клип не в кэше, загружаю");
            load(name);
            clip = clips.get(name);
            if (clip == null) {
                System.out.println("[Sound]   → НЕТ КЛИПА, играю бип");
                playBeepFor(name);
                return;
            }
        }

        try {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
            System.out.println("[Sound]   → ✅ ИГРАЮ " + name
                    + " | running=" + clip.isRunning()
                    + " | vol=" + Config.sfxVolume);
        } catch (Exception e) {
            System.err.println("[Sound]   → ❌ Ошибка: " + e.getMessage());
        }
    }

    private static void playBeepFor(String name) {
        int freq = switch (name) {
            case "hit" -> 400;
            case "pickup" -> 800;
            case "levelup" -> 1000;
            case "spell" -> 600;
            case "step" -> 200;
            case "enemy_death" -> 300;
            case "player_death" -> 150;
            case "menu" -> 500;
            default -> 500;
        };
        playBeep(freq, 80, Config.sfxVolume);
    }

    public static void playBeep(int frequency, int durationMs, int volume) {
        if (!Config.soundEnabled) return;
        if (volume <= 0) return;
        try {
            float sampleRate = 44100;
            int samples = (int) (sampleRate * durationMs / 1000.0);
            byte[] buf = new byte[samples];
            for (int i = 0; i < samples; i++) {
                double angle = 2.0 * Math.PI * i * frequency / sampleRate;
                double fade = 1.0 - (double) i / samples;
                buf[i] = (byte) (Math.sin(angle) * volume * fade * 1.27);
            }
            AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(af);
            line.open(af, buf.length);
            line.start();
            line.write(buf, 0, buf.length);
            line.drain();
            line.close();
        } catch (Exception ignored) {}
    }

    // ============= МУЗЫКА =============

    public static void playMusic(String name) {
        if (!Config.soundEnabled) return;

        if (musicClip != null && musicName != null && musicName.equals(name)
                && musicClip.isRunning()) {
            return;
        }

        stopMusic();

        try {
            URL url = SoundManager.class.getResource("/sounds/" + name + ".wav");
            System.out.println("[Music] playMusic: " + name + " → URL = " + url);

            if (url == null) {
                System.err.println("[Music] ❌ НЕ НАЙДЕНА: " + name);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            musicClip = AudioSystem.getClip();
            musicClip.open(ais);
            applyVolume(musicClip, Config.musicVolume);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
            musicName = name;
            System.out.println("[Music] ✅ ИГРАЕТ " + name);
        } catch (Exception e) {
            System.err.println("[Music] ❌ Ошибка: " + e.getMessage());
        }
    }

    public static void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
        musicName = null;
    }

    // ============= ГРОМКОСТЬ =============

    public static void setMusicVolume(int volume) {
        Config.musicVolume = volume;
        System.out.println("[Volume] musicVolume = " + volume);
        if (musicClip != null) {
            applyVolume(musicClip, volume);
        }
    }

    public static void setSfxVolume(int volume) {
        Config.sfxVolume = volume;
        System.out.println("[Volume] sfxVolume = " + volume);
        for (Clip c : clips.values()) {
            applyVolume(c, volume);
        }
    }

    private static void applyVolume(Clip clip, int volumePercent) {
        if (clip == null) return;

        if (volumePercent < 0) volumePercent = 0;
        if (volumePercent > 100) volumePercent = 100;

        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB;
                if (volumePercent <= 0) {
                    dB = gain.getMinimum();
                } else {
                    dB = (float) (20 * Math.log10(volumePercent / 100.0));
                }
                dB = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), dB));
                gain.setValue(dB);
                System.out.println("[Volume]   MASTER_GAIN = " + String.format("%.1f", dB) + " dB");
                return;
            }
            if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
                FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
                float v = volumePercent / 100f;
                v = Math.max(vol.getMinimum(), Math.min(vol.getMaximum(), v));
                vol.setValue(v);
                System.out.println("[Volume]   VOLUME = " + v);
                return;
            }
            System.err.println("[Volume] ❌ Клип не поддерживает громкость");
        } catch (Exception e) {
            System.err.println("[Volume] ❌ Ошибка: " + e.getMessage());
        }
    }

    public static void shutdown() {
        for (Clip c : clips.values()) {
            if (c.isRunning()) c.stop();
            c.close();
        }
        clips.clear();
        stopMusic();
    }
}