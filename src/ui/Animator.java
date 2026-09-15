package ui;

import java.awt.image.BufferedImage;
import java.util.List;

public class Animator {
    private List<BufferedImage> frames;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private int frameDurationMs;
    private boolean looping;
    private boolean finished = false;

    public Animator(List<BufferedImage> frames, int frameDurationMs, boolean looping) {
        this.frames = frames;
        this.frameDurationMs = frameDurationMs;
        this.looping = looping;
    }

    public void update() {
        if (frames == null || frames.isEmpty()) return;
        if (finished) return;

        long now = System.currentTimeMillis();
        if (now - lastFrameTime >= frameDurationMs) {
            lastFrameTime = now;
            currentFrame++;
            if (currentFrame >= frames.size()) {
                if (looping) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.size() - 1;
                    finished = true;
                }
            }
        }
    }

    public BufferedImage getCurrentFrame() {
        if (frames == null || frames.isEmpty()) return null;
        if (currentFrame >= frames.size()) currentFrame = frames.size() - 1;
        return frames.get(currentFrame);
    }

    public void reset() {
        currentFrame = 0;
        lastFrameTime = 0;
        finished = false;
    }

    public boolean isFinished() { return finished; }
    public int getCurrentFrameIndex() { return currentFrame; }
    public int getFrameCount() { return frames == null ? 0 : frames.size(); }
    public boolean hasFrames() { return frames != null && !frames.isEmpty(); }
}