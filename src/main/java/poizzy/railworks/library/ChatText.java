package poizzy.railworks.library;

import cam72cam.mod.text.PlayerMessage;
import cam72cam.mod.text.TextUtil;

public enum ChatText {
    CONTROLLER_CLEARED("controller.cleared"),
    CONNECTOR_FIRST_SET("connector.first.set"),
    CONNECTOR_DIM_MISMATCH("connector.dim.mismatch"),
    CONNECTOR_SAME_BLOCK("connector.same.block"),
    CONNECTOR_TILE_MISSING("connector.tile.missing"),
    CONNECTOR_INVALID_PAIR("connector.invalid.pair"),
    CONNECTOR_CONTROLLER_BUSY("connector.controller.busy"),
    CONNECTOR_CONNECTED("connector.connected");

    private final String value;
    ChatText(String value) {
        this.value = value;
    }

    public String getRaw() {
        return "chat.railworks:" + value;
    }

    public PlayerMessage getMessage(Object... objects) {
        return PlayerMessage.translate(getRaw(), objects);
    }

    @Override
    public String toString() {
        return TextUtil.translate(getRaw());
    }
}
