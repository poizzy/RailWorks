package poizzy.railworks.gui.components;

import cam72cam.mod.ModCore;
import cam72cam.mod.event.ClientEvents;
import cam72cam.mod.gui.helpers.GUIHelpers;
import cam72cam.mod.gui.screen.IScreenBuilder;
import cam72cam.mod.render.opengl.RenderState;
import poizzy.railworks.library.MouseHelper;
import poizzy.railworks.tile.TileSignal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

public class ListSelector implements MouseHelper.MouseEvent {
    private final LinkedList<SelectorLine> list = new LinkedList<>();
    private final LinkedList<String> states;
    private final int x;
    private final int y;
    private int currentY;
    private int firstVisibleRow = 0;

    private static final int LINE_HEIGHT = 26;
    private static final int VISIBLE_ROWS = 5;
    private static final int VIEWPORT_WIDTH = 164;
    private static final int VIEWPORT_HEIGHT = VISIBLE_ROWS * LINE_HEIGHT;

    public ListSelector(TileSignal signal, int x, int y) {
        this.x = x;
        this.y = y;
        this.currentY = y;

        this.states = signal.getDefinition().signalStates.keySet().stream().sorted().collect(Collectors.toCollection(LinkedList::new));

        MouseHelper.addListener(this);
    }

    public final void addLine(int redstoneLevel, String state) {
        if (state == null || state.isEmpty()) state = states.getFirst();

        CustomButton redstoneButton = createRedstoneField(x, currentY);
        CustomButton stateButton = createStateField(x, currentY);
        CustomButton addButton = createAddButton(x, currentY);

        redstoneButton.setText(String.valueOf(redstoneLevel));
        stateButton.setText(state);
        addButton.setIndex(list.size());

        SelectorLine selectorLine = new SelectorLine(redstoneButton, stateButton, addButton);

        list.add(selectorLine);
//        currentY = y + (LINE_HEIGHT * list.size());
        relayout();
    }

    private void relayout() {
        int maxOffset = Math.max(0, list.size() - VISIBLE_ROWS);
        if (firstVisibleRow > maxOffset) firstVisibleRow = maxOffset;
        if (firstVisibleRow < 0) firstVisibleRow = 0;

        for (int i = 0; i < list.size(); i++) {
            SelectorLine line = list.get(i);
            int rel = i - firstVisibleRow;

            if (rel >= 0 && rel < VISIBLE_ROWS) {
                int lineY = y + (rel * LINE_HEIGHT);
                line.setIndex(i);
                line.setPosition(x, lineY);
                line.setVisible(true);
            } else {
                line.setVisible(false);
            }
            line.addButton.setType(i == list.size() - 1 ? CustomButton.Type.PLUS : CustomButton.Type.MINUS);
        }
    }

    public CustomButton createRedstoneField(int x, int y) {
        return new CustomButton(CustomButton.Type.REDSTONE, x, y, 35, 24) {
            @Override
            public void onClick(boolean clickDir) {
                int current = Integer.parseInt(getText());
                int next = current + (clickDir ? 1 : -1);
                next = next > 15 ? 0 : (next < 0 ? 15 : next);
                setText(String.valueOf(next));
            }
        };
    }

    public CustomButton createStateField(int x, int y) {
        return new CustomButton(CustomButton.Type.STATE, x + 37, y, 103, 24) {
            @Override
            public void onClick(boolean clickDir) {
                int currentIndex = states.indexOf(getText());
                if (currentIndex == -1) {
                    ModCore.error("State is not available");
                    return;
                }
                int nextIndex = (currentIndex + (clickDir ? 1 : -1) + states.size()) % states.size();
                setText(states.get(nextIndex));
            }
        };
    }

    public CustomButton createAddButton(int x, int y) {
        return new CustomButton(CustomButton.Type.PLUS, x + 142, y, 22, 22) {
            @Override
            public void onClick(boolean remove) {
                if (remove) {
                    int idx = getIndex();
                    if (idx >= 0 && idx < list.size()) {
                        SelectorLine removed = list.remove(getIndex());
                        removed.dispose();
                        relayout();
                    }
                } else {
                    addLine(0, states.getFirst());
                }
            }
        };
    }

    public void dispose() {
        MouseHelper.removeListener(this);
        for (SelectorLine line : list) {
            line.dispose();
        }
        list.clear();
    }

    public final void draw(IScreenBuilder builder, RenderState state) {
        for (SelectorLine line : list) {
            line.redstoneLevel.draw(builder, state);
            line.state.draw(builder, state);
            line.addButton.draw(builder, state);
        }

        drawScrollbar(builder);
    }

    public void drawScrollbar(IScreenBuilder builder) {
        int total = Math.max(1, list.size());
        if (total <= VISIBLE_ROWS) return;

        int trackX = x - 5;
        int trackY = y;
        int trackH = VIEWPORT_HEIGHT;

        GUIHelpers.drawRect(trackX, trackY, 3, trackH, 0x80000000);

        int handleH = Math.max(8, (int)(trackH * (VISIBLE_ROWS / (float) total)));
        int maxOffset = total - VISIBLE_ROWS;
        int handleY = trackY + (int) ((firstVisibleRow / (float) maxOffset) * (trackH - handleH));

        GUIHelpers.drawRect(trackX, handleY, 3, handleH, 0xFFFFFFFF);
    }

    public Map<Integer, String> getRedstoneLevels() {
        Map<Integer, String> map = new HashMap<>();
        for (SelectorLine line : list) {
            Integer redstone = Integer.parseInt(line.redstoneLevel.getText());
            String state = line.state.getText();
            map.put(redstone, state);
        }
        return map;
    }

    @Override
    public void onMouseEvent(ClientEvents.MouseGuiEvent event) {
        if (event.action == ClientEvents.MouseAction.SCROLL) {
            if (isOverViewport(event.x, event.y)) {
                scrollBy((int) -event.scroll);
            }
        }
    }

    private boolean isOverViewport(int mx, int my) {
        return mx >= x && my >= y && mx < x + VIEWPORT_WIDTH && my < y + VIEWPORT_HEIGHT;
    }

    private void scrollBy(int rows) {
        int maxOffset = Math.max(0, list.size() - VISIBLE_ROWS);
        firstVisibleRow = Math.max(0, Math.min(firstVisibleRow + rows, maxOffset));
        relayout();
    }

    public static class SelectorLine {
        CustomButton redstoneLevel;
        CustomButton state;
        CustomButton addButton;

        public SelectorLine(CustomButton redstoneLevel, CustomButton state, CustomButton addButton) {
            this.redstoneLevel = redstoneLevel;
            this.state = state;
            this.addButton = addButton;
        }

        public void dispose() {
            redstoneLevel.dispose();
            state.dispose();
            addButton.dispose();
        }

        void setPosition(int baseX, int baseY) {
            redstoneLevel.setPosition(baseX, baseY);
            state.setPosition(baseX + 37, baseY);
            addButton.setPosition(baseX + 142, baseY);
        }

        void setIndex(int i) {
            addButton.setIndex(i);
        }

        void setVisible(boolean v) {
            redstoneLevel.setVisible(v);
            state.setVisible(v);
            addButton.setVisible(v);
        }
    }
}
