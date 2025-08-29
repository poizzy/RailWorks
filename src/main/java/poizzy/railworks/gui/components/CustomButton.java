package poizzy.railworks.gui.components;

import cam72cam.mod.event.ClientEvents;
import cam72cam.mod.gui.helpers.GUIHelpers;
import cam72cam.mod.gui.screen.IScreenBuilder;
import cam72cam.mod.render.opengl.RenderState;
import cam72cam.mod.resource.Identifier;
import poizzy.railworks.RailWorks;
import poizzy.railworks.library.MouseHelper;

// CustomButton.java
public abstract class CustomButton implements MouseHelper.MouseEvent {
    private Type type;
    private int x, y;
    private final int width, height;
    private boolean hovered, visible;
    private CustomButton arrowLeft;
    private CustomButton arrowRight;
    private int index;
    private String text = "";
    private static final Identifier TEXTURE = new Identifier(RailWorks.MODID, "textures/gui/controller.png");

    public enum Type {
        PLUS,
        MINUS,
        STATE,
        REDSTONE,
        ARROW_LEFT,
        ARROW_RIGHT;
        private static final Type[] vals = values();

        public Type next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    public CustomButton(Type type, int x, int y, int width, int height) {
        this(type, x, y, width, height, /*createChildren*/ (type == Type.STATE || type == Type.REDSTONE));
    }

    private CustomButton(Type type, int x, int y, int width, int height, boolean createChildren) {
        MouseHelper.addListener(this);

        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = true;

        switch (type) {
            case STATE:
                arrowLeft = new CustomButton(Type.ARROW_LEFT, x + 4, y + 7, 7, 11, false) {
                    @Override
                    public void onClick(boolean clickDir) {
                        CustomButton.this.onClick(false);
                    }
                };
                arrowRight = new CustomButton(Type.ARROW_RIGHT, x + 92, y + 7, 7, 11, false) {
                    @Override
                    public void onClick(boolean clickDir) {
                        CustomButton.this.onClick(true);
                    }
                };
                break;
            case REDSTONE:
                arrowLeft = new CustomButton(Type.ARROW_LEFT, x + 4, y + 7, 7, 11, false) {
                    @Override
                    public void onClick(boolean clickDir) {
                        CustomButton.this.onClick(false);
                    }
                };
                arrowRight = new CustomButton(Type.ARROW_RIGHT, x + 24, y + 7, 7, 11, false) {
                    @Override
                    public void onClick(boolean clickDir) {
                        CustomButton.this.onClick(true);
                    }
                };
                break;
        }
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        switch (type) {
            case STATE:
                if (arrowLeft != null) arrowLeft.setPosition(x + 4,  y + 7);
                if (arrowRight != null) arrowRight.setPosition(x + 92, y + 7);
                break;
            case REDSTONE:
                if (arrowLeft != null) arrowLeft.setPosition(x + 4,  y + 7);
                if (arrowRight != null) arrowRight.setPosition(x + 24, y + 7);
                break;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }

    private static CustomButton mouseCapture = null;


    private boolean isOver(int mx, int my) {
        return mx >= x && my >= y && mx < x + width && my < y + height;
    }
    private static boolean hit(CustomButton b, int mx, int my) {
        return b != null && b.visible && mx >= b.x && my >= b.y && mx < b.x + b.width && my < b.y + b.height;
    }
    private boolean overChild(int mx, int my) {
        if (type != Type.STATE && type != Type.REDSTONE) return false;
        return hit(arrowLeft, mx, my) || hit(arrowRight, mx, my);
    }

    @Override
    public void onMouseEvent(ClientEvents.MouseGuiEvent event) {
        if (!visible) return;

        if (event.action == ClientEvents.MouseAction.MOVE) {
            this.hovered = isOver(event.x, event.y);
            return;
        }

        if (event.action == ClientEvents.MouseAction.SCROLL) {
            // TODO
            return;
        }

        if (event.button != 0) return;

        boolean overSelf = isOver(event.x, event.y);

        if (event.action == ClientEvents.MouseAction.CLICK) {
            if (overChild(event.x, event.y)) return;

            if (mouseCapture == null && overSelf) {
                mouseCapture = this;
            }
            return;
        }

        if (event.action == ClientEvents.MouseAction.RELEASE) {
            if (mouseCapture  == this && overSelf) {
                switch (type) {
                    case PLUS:
                    case MINUS:
                        onClick(type == Type.MINUS);
                        break;
                    case ARROW_LEFT:
                    case ARROW_RIGHT:
                        onClick(type == Type.ARROW_RIGHT);
                        break;
                }
            }
            if (mouseCapture == this) {
                mouseCapture = null;
            }
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setText(String newText) {
        this.text = newText;
    }

    public String getText() {
        return this.text;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void draw(IScreenBuilder screen, RenderState state) {
        if (!visible) return;

        switch (type) {
            case PLUS:
                GUIHelpers.texturedRect(TEXTURE, x, y, hovered ? 201 : 157, 205, 22, 22, width, height, 256, 256);
                break;
            case MINUS:
                GUIHelpers.texturedRect(TEXTURE, x, y, hovered ? 201 : 157, 231, 22, 22, width, height, 256, 256);
                break;
            case STATE:
                GUIHelpers.texturedRect(TEXTURE, x, y, 47, 172, 103, 24, width, height, 256, 256);
                if (arrowLeft != null) arrowLeft.draw(screen, state);
                if (arrowRight != null) arrowRight.draw(screen, state);
                break;
            case REDSTONE:
                GUIHelpers.texturedRect(TEXTURE, x, y, 10, 172, 35, 24, width, height, 256, 256);
                if (arrowLeft != null) arrowLeft.draw(screen, state);
                if (arrowRight != null) arrowRight.draw(screen, state);
                break;
            case ARROW_LEFT:
                GUIHelpers.texturedRect(TEXTURE, x, y, 62, hovered ? 212 : 226, 7, 11, width, height, 256, 256);
                break;
            case ARROW_RIGHT:
                GUIHelpers.texturedRect(TEXTURE, x, y, 90, hovered ? 212 : 226, 7, 11, width, height, 256, 256);
                break;
        }

        GUIHelpers.drawCenteredString(text, x + (width / 2) + 1, y + (height / 2) - 3, 0xFFFFFFFF);
    }

    public Type getType() { return type; }

    public void setType(Type t) {
        if ((type == Type.PLUS || type == Type.MINUS) &&
                (t    == Type.PLUS || t    == Type.MINUS)) {
            this.type = t;
            return;
        }
        this.type = t;
    }

    public void dispose() {
        MouseHelper.removeListener(this);
        if (arrowLeft != null) arrowLeft.dispose();
        if (arrowRight != null) arrowRight.dispose();
    }

    public abstract void onClick(boolean type);
}
