package Server.utils;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class InputHandlerUtil {
    public static void clickMouse(Robot robot, int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public static void pressMouseButton(Robot robot, int button) {
        int buttonMask = getMouseButtonMask(button);
        if (buttonMask != -1) {
            robot.mousePress(buttonMask);
        }
    }

    public static void releaseMouseButton(Robot robot, int button) {
        int buttonMask = getMouseButtonMask(button);
        if (buttonMask != -1) {
            robot.mouseRelease(buttonMask);
        }
    }

    public static void typeKey(Robot robot, int keyCode) {
        robot.keyPress(keyCode);
        robot.keyRelease(keyCode);
    }

    public static void typeText(Robot robot, String text) {
        for (char c : text.toCharArray()) {
            typeChar(robot, c);
        }
    }

    private static void typeChar(Robot robot, char c) {
        try {
            boolean shiftPressed = false;
            if (Character.isUpperCase(c) || "!@#$%^&*".indexOf(c) != -1) {
                shiftPressed = true;
                robot.keyPress(KeyEvent.VK_SHIFT);
            }
            int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
            if (keyCode == KeyEvent.VK_UNDEFINED) {
                throw new IllegalArgumentException("Cannot type character " + c);
            }
            robot.keyPress(keyCode);
            robot.keyRelease(keyCode);
            if (shiftPressed) {
                robot.keyRelease(KeyEvent.VK_SHIFT);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static int getMouseButtonMask(int button) {
        switch (button) {
            case MouseEvent.BUTTON1: return InputEvent.BUTTON1_DOWN_MASK;
            case MouseEvent.BUTTON2: return InputEvent.BUTTON2_DOWN_MASK;
            case MouseEvent.BUTTON3: return InputEvent.BUTTON3_DOWN_MASK;
            default: return -1;
        }
    }
}
