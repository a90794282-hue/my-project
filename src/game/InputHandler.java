package game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Set;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {

    // ===== Клавиатура =====
    private final Set<Integer> held = new HashSet<>();
    private final Set<Integer> pressedThisFrame = new HashSet<>();
    private final Set<Integer> releasedThisFrame = new HashSet<>();

    // ===== Мышь =====
    private int mouseX = 0, mouseY = 0;
    private boolean mouseLeftHeld = false;
    private boolean mouseLeftPressedThisFrame = false;
    private boolean mouseRightHeld = false;
    private boolean mouseRightPressedThisFrame = false;

    // ========== КЛАВИАТУРА ==========
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (held.contains(code)) return;
        held.add(code);
        pressedThisFrame.add(code);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        held.remove(code);
        releasedThisFrame.add(code);
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    // ========== МЫШЬ ==========
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (!mouseLeftHeld) {
                mouseLeftHeld = true;
                mouseLeftPressedThisFrame = true;
            }
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (!mouseRightHeld) {
                mouseRightHeld = true;
                mouseRightPressedThisFrame = true;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();

        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseLeftHeld = false;
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            mouseRightHeld = false;
        }
    }

    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }

    // ========== API ==========
    public boolean isDown(int code) { return held.contains(code); }
    public boolean wasPressed(int code) { return pressedThisFrame.contains(code); }
    public Set<Integer> getPressed() { return pressedThisFrame; }

    public boolean isLeftMouseDown() { return mouseLeftHeld; }
    public boolean wasLeftMousePressed() { return mouseLeftPressedThisFrame; }
    public boolean wasRightMousePressed() { return mouseRightPressedThisFrame; }

    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }

    public void clearFrame() {
        pressedThisFrame.clear();
        releasedThisFrame.clear();
        mouseLeftPressedThisFrame = false;
        mouseRightPressedThisFrame = false;
    }

    public void reset() {
        held.clear();
        pressedThisFrame.clear();
        releasedThisFrame.clear();
        mouseLeftHeld = false;
        mouseLeftPressedThisFrame = false;
        mouseRightHeld = false;
        mouseRightPressedThisFrame = false;
    }
}