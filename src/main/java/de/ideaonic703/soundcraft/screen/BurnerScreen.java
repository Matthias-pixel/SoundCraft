package de.ideaonic703.soundcraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.ideaonic703.soundcraft.SoundCraft;
import de.ideaonic703.soundcraft.network.ModPackets;
import de.ideaonic703.soundcraft.network.packet.c2s.SoundCraftScreenC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public class BurnerScreen extends SoundCraftScreen<BurnerScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(SoundCraft.MOD_ID, "textures/gui/cd_burner_gui.png");
    private static final int PADDING = 2;
    private static final int PLAYLIST_X = 62 + PADDING;
    private static final int PLAYLIST_Y = 15 + PADDING;
    private static final int PLAYLIST_WIDTH = 105 - PADDING * 2;
    private static final int PLAYLIST_HEIGHT = 110 + 3 - PADDING * 2;
    private static final int SCROLL_X = PLAYLIST_X + PLAYLIST_WIDTH - 7 + PADDING;
    private static final int SCROLL_HEIGHT = 93;
    private static final int SCROLL_LIMIT = 8;
    private static final int BUTTON_WIDTH = 10;
    private static final int BUTTON_X = 62 + PADDING;
    private static final int BUTTON_U = 0;
    private static final int BUTTON_V = 220;
    private static final int BUTTON_HEIGHT = 10;
    private static final int BUTTON_Y = 124 - BUTTON_HEIGHT - PADDING;
    private static final int TOP_WIDTH = 48;
    private static final int SONG_TEXT_HEIGHT = 8;
    private static final int SONG_COLOR = 0xc6c6c6;
    private static final int SONG_COLOR_AVAILABLE = 0x98971a;
    private static final int SONG_COLOR_UNAVAILABLE = 0x9d0006;
    private static final int BACKGROUND_COLOR = 0xFF282828;
    private static final int SCROLL_BACKGROUND_COLOR = 0xFF1d1d1d;
    private static final int SCROLL_BAR_COLOR = 0xFFc6c6c6;
    private int xOffset;
    private int yOffset;
    private Button[] menuButtons;
    private HashMap<String, PlaylistButton> playlistButtons = new HashMap<>();
    private ScrollButton scrollButton;
    private String selectedSong = "";
    private final BurnerScreenHandler handler;

    public BurnerScreen(BurnerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        SoundCraft.LOGGER.info("Created BurnerScreen");
        this.handler = handler;
    }

    @Override
    protected void init() {
        super.init();
        SoundCraft.LOGGER.info("Init Burner Screen");
        this.titleX = playerInventoryTitleX;
        this.backgroundWidth = 176;
        this.backgroundHeight = 172;
        this.titleY -= TOP_WIDTH;
        this.xOffset = (width - backgroundWidth) / 2;
        this.yOffset = (height - backgroundHeight) / 2 + 3 - TOP_WIDTH;
        this.initButtons();
        if (!this.handler.hasUpdateHandler()) {
            this.handler.registerUpdateHandler(this::updatePlaylist);
        }
        this.handler.updatePlaylist();
    }

    private void initButtons() {
        this.menuButtons = new Button[5];
        for (int i = 0; i < this.menuButtons.length; i++) {
            if (i > 1)
                this.menuButtons[i] = new Button(PLAYLIST_WIDTH + PLAYLIST_X - (BUTTON_WIDTH + PADDING) * (i - 1) + PADDING, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, i, i > 2);
            else
                this.menuButtons[i] = new Button(BUTTON_X + (BUTTON_WIDTH + PADDING) * i, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, i, false);
            final int finalI = i;
            this.menuButtons[i].registerClickListener(new ClickListener() {
                @Override
                public boolean onMouseDown() {
                    return false;
                }

                @Override
                public boolean onMouseUp() {
                    soundCraftScreenAction(finalI, selectedSong);
                    return false;
                }
            });
        }
    }
    private void updatePlaylist(HashMap<String, NbtCompound> playlist) {
        this.playlistButtons = new HashMap<>(playlist.size());
        int i = -1;
        for (String path : playlist.keySet()) {
            i++;
            NbtCompound song = playlist.get(path);
            PlaylistButton button = new PlaylistButton(path, i, song);
            button.registerClickListener(new ClickListener() {
                @Override
                public boolean onMouseDown() {
                    return true;
                }

                @Override
                public boolean onMouseUp() {
                    selectedSong = song.getString("path");
                    return updateSelection();
                }
            });
            this.playlistButtons.put(path, button);
        }
        selectedSong = "";
        updateSelection();
    }

    private boolean updateSelection() {
        // Playlist buttons
        for (PlaylistButton button : this.playlistButtons.values()) {
            button.setSelected(button.getPath().equals(this.selectedSong));
        }
        // Menu buttons
        if (this.playlistButtons.containsKey(this.selectedSong)) {
            NbtCompound selectedSong = playlistButtons.get(this.selectedSong).getSong();
            boolean hasCd = this.handler.hasCd();
            if (selectedSong.getBoolean("added")) {
                menuButtons[0].setActive(false);
                menuButtons[1].setActive(hasCd);
                menuButtons[2].setActive(false);
            } else {
                menuButtons[0].setActive(hasCd);
                menuButtons[1].setActive(false);
                menuButtons[2].setActive(true);
            }
        } else {
            menuButtons[0].setActive(false);
            menuButtons[1].setActive(false);
            menuButtons[2].setActive(false);
        }
        menuButtons[3].setActive(true);
        menuButtons[4].setActive(true);
        return true;
    }

    private void renderPlaylist(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int scrollY = 0;
        if(this.playlistButtons.size() > SCROLL_LIMIT && this.scrollButton != null)
            scrollY = (int)(scrollButton.getScrolled()*(SCROLL_LIMIT-this.playlistButtons.size())*(BUTTON_HEIGHT+PADDING));
        for (PlaylistButton button : this.playlistButtons.values()) {
            button.setScrollY(scrollY);
            button.render(matrices, mouseX, mouseY);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
    }

    private void renderButtons(MatrixStack matrices, int mouseX, int mouseY) {
        int oldZOffset = getZOffset();
        setZOffset(1);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        for (Button button : this.menuButtons) {
            button.render(matrices, mouseX, mouseY);
        }
        setZOffset(oldZOffset);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        drawTexture(matrices, xOffset, yOffset, 0, 0, backgroundWidth, backgroundHeight + TOP_WIDTH);
    }

    private void drawScrollBar(MatrixStack matrices, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int songs = this.playlistButtons.size();
        if (!(songs > SCROLL_LIMIT)) {
            return;
        }
        float ratio = (float)SCROLL_LIMIT/(float)songs;
        drawColor(matrices, xOffset+SCROLL_X-PADDING, yOffset + PLAYLIST_Y, 7, SCROLL_HEIGHT, SCROLL_BACKGROUND_COLOR);
        if(this.scrollButton == null)
            this.scrollButton = new ScrollButton(SCROLL_X + 1 - PADDING, PLAYLIST_Y + 1, 5, SCROLL_HEIGHT - 2, ratio, SCROLL_BAR_COLOR);
        this.scrollButton.render(matrices, mouseX, mouseY);
    }

    public void drawTransparent(MatrixStack matrices, int x, int y, int width, int height, int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.colorMask(true, true, true, false);
        fillGradient(matrices, x, y, x + width, y + height, alpha * 0x1000000, alpha * 0x1000000);
        RenderSystem.colorMask(true, true, true, true);
    }
    public void drawColor(MatrixStack matrices, int x, int y, int width, int height, int color, int z) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        fillGradient(matrices, x, y, x + width, y + height, color, color, z);
    }
    public void drawColor(MatrixStack matrices, int x, int y, int width, int height, int color) {
        drawColor(matrices, x, y, width, height, color, 0);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        drawColor(matrices, xOffset + PLAYLIST_X, yOffset + PLAYLIST_Y, PLAYLIST_WIDTH, PLAYLIST_HEIGHT - PADDING * 3 - BUTTON_HEIGHT, BACKGROUND_COLOR);
        this.renderPlaylist(matrices, mouseX, mouseY);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        super.render(matrices, mouseX, mouseY, delta);
        drawColor(matrices, xOffset + PLAYLIST_X, yOffset + PLAYLIST_Y + PLAYLIST_HEIGHT - PADDING * 3 - BUTTON_HEIGHT, PLAYLIST_WIDTH, BUTTON_HEIGHT + PADDING, BACKGROUND_COLOR, 1);
        this.renderButtons(matrices, mouseX, mouseY);
        this.drawScrollBar(matrices, mouseX, mouseY);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button_type) {
        SoundCraft.LOGGER.info("Mouse released");
        for (Button button : this.menuButtons) {
            button.onMouseUp(mouseX, mouseY, button_type);
        }
        for (Button button : this.playlistButtons.values()) {
            button.onMouseUp(mouseX, mouseY, button_type);
        }
        if(this.scrollButton != null)
            this.scrollButton.onMouseUp(mouseX, mouseY, button_type);
        super.mouseReleased(mouseX, mouseY, button_type);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button_type) {
        SoundCraft.LOGGER.info("Mouse clicked");
        for (Button button : this.menuButtons) {
            button.onMouseDown(mouseX, mouseY, button_type);
        }
        for (Button button : this.playlistButtons.values()) {
            button.onMouseDown(mouseX, mouseY, button_type);
        }
        if(this.scrollButton != null)
            this.scrollButton.onMouseDown(mouseX, mouseY, button_type);
        super.mouseClicked(mouseX, mouseY, button_type);
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return super.keyReleased(GLFW.GLFW_KEY_DOWN, scanCode, modifiers);
    }

    private class Button {
        protected int x, y, width, height, index, dragX = 0, dragY = 0;
        protected boolean active, hidden, underMouse, clicked, selected;
        protected HoverListener hoverListener = null;
        protected ClickListener clickListener = null;

        public Button(int x, int y, int width, int height, int index, boolean active) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.index = index;
            this.active = active;
            this.hidden = false;
            this.underMouse = false;
            this.clicked = false;
            this.selected = false;
        }

        protected boolean touching(int x, int y) {
            return (x > this.x && x < this.x + this.width) && (y > this.y && y < this.y + this.height);
        }

        public void render(MatrixStack matrices, int mouseX, int mouseY) {
            mouseX -= xOffset;
            mouseY -= yOffset;
            if (this.hidden) {
                this.clicked = false;
                return;
            }
            boolean underMouse = this.touching(mouseX, mouseY);
            if (this.hoverListener != null && underMouse != this.underMouse) {
                this.hoverListener.onHover(underMouse);
            }
            this.underMouse = underMouse;
            this.display(matrices, mouseX, mouseY);
        }

        protected void displaySelected(MatrixStack matrices) {
            drawTransparent(matrices, xOffset + this.x, yOffset + this.y, this.width, this.height, 0x66);
        }

        protected void displayHovered(MatrixStack matrices) {
            drawTransparent(matrices, xOffset + this.x, yOffset + this.y, this.width, this.height, 0x44);
        }
        protected void displayClicked(MatrixStack matrices) {
            displayHovered(matrices);
        }

        protected void displayActive(MatrixStack matrices) {
            drawTexture(matrices, xOffset + this.x, yOffset + this.y, BUTTON_U + (this.width + PADDING) * this.index, BUTTON_V, this.width, this.height);
        }

        protected void displayInactive(MatrixStack matrices) {
            drawTexture(matrices, xOffset + this.x, yOffset + this.y, BUTTON_U + (this.width + PADDING) * this.index, BUTTON_V + this.height, this.width, this.height);
        }

        protected void display(MatrixStack matrices, int mouseX, int mouseY) {
            if (this.selected) {
                this.displaySelected(matrices);
            }
            if (this.active) {
                this.displayActive(matrices);
                if (this.isHovered())
                    this.displayHovered(matrices);
                if (this.isClicked())
                    this.displayClicked(matrices);
            } else {
                this.displayInactive(matrices);
            }
        }

        public boolean onMouseDown(double mouseX, double mouseY, int button) {
            boolean success = true;
            mouseX -= xOffset;
            mouseY -= yOffset;
            boolean isTouching = this.touching((int) mouseX, (int) mouseY);
            if (isTouching && !this.clicked) {
                if(this.clickListener != null)
                    success = this.clickListener.onMouseDown();
                this.dragX = (int)(mouseX);
                this.dragY = (int)(mouseY);
            }
            this.clicked = isTouching;
            return success;
        }

        public boolean onMouseUp(double mouseX, double mouseY, int button) {
            boolean success = true;
            if (this.clickListener != null && this.clicked) {
                success = this.clickListener.onMouseUp();
            }
            this.clicked = false;
            this.dragX = 0;
            this.dragY = 0;
            return success;
        }

        public void move(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public boolean isActive() {
            return this.active;
        }

        public void setHidden(boolean hidden) {
            this.hidden = hidden;
        }

        public boolean isHidden() {
            return this.hidden;
        }

        public void registerHoverListener(HoverListener hoverListener) {
            this.hoverListener = hoverListener;
        }

        public void registerClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        public boolean isHovered() {
            return this.underMouse;
        }

        public boolean hasHoverListener() {
            return this.hoverListener != null;
        }

        public boolean hasClickListener() {
            return this.clickListener != null;
        }

        public void click() {
            this.clicked = true;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return this.selected;
        }
        public int getDragX() {
            return this.dragX;
        }
        public int getDragY() {
            return this.dragY;
        }

        public boolean isClicked() {
            return this.clicked;
        }
    }

    private class PlaylistButton extends Button {
        protected final NbtCompound song;
        protected final String path;

        public int getScrollY() {
            return scrollY;
        }

        public void setScrollY(int scrollY) {
            this.scrollY = scrollY;
        }

        protected int scrollY, index;
        protected final boolean added;

        public PlaylistButton(String path, int index, NbtCompound song) {
            super(PLAYLIST_X - PADDING, PLAYLIST_Y - PADDING + (SONG_TEXT_HEIGHT + PADDING * 2) * index, PLAYLIST_WIDTH + PADDING * 2, SONG_TEXT_HEIGHT + PADDING * 2, -1, true);
            this.path = path;
            this.index = index;
            this.song = song;
            this.added = this.song.getBoolean("added");
            this.scrollY = 0;
            this.setActive(song.getBoolean("available"));
        }

        @Override
        public void displaySelected(MatrixStack matrices) {
            RenderSystem.setShaderTexture(0, TEXTURE);
            drawTransparent(matrices, xOffset + this.x, yOffset + this.y + this.scrollY, this.width, this.height, 0x66);
        }

        @Override
        public void displayActive(MatrixStack matrices) {
            if (this.added) {
                drawTextWithShadow(matrices, textRenderer, Text.literal(this.song.getString("name")), this.getX() + xOffset + PADDING, this.getY() + yOffset + PADDING + this.scrollY, SONG_COLOR_AVAILABLE);
            } else {
                drawTextWithShadow(matrices, textRenderer, Text.literal(this.song.getString("name")), this.getX() + xOffset + PADDING, this.getY() + yOffset + PADDING + this.scrollY, SONG_COLOR);
            }
        }

        @Override
        public void displayHovered(MatrixStack matrices) {
        }

        @Override
        public void displayInactive(MatrixStack matrices) {
            drawTextWithShadow(matrices, textRenderer, Text.literal(this.song.getString("name")), this.getX() + xOffset + PADDING, this.getY() + yOffset + PADDING + this.scrollY, SONG_COLOR_UNAVAILABLE);
        }

        @Override
        protected void display(MatrixStack matrices, int mouseX, int mouseY) {
            if(this.y + this.scrollY > 110)
                return;
            if(this.y + this.scrollY < 13 - this.height)
                return;
            super.display(matrices, mouseX, mouseY);
        }

        public String getPath() {
            return this.path;
        }
        public int getIndexh() {
            return this.index;
        }

        @Override
        protected boolean touching(int x, int y) {
            if(y > 110 || y < 16)
                return false;
            return (x > this.x && x < this.x + this.width) && (y > this.y+this.scrollY && y < this.y+this.scrollY + this.height);
        }

        public NbtCompound getSong() {
            return this.song;
        }
    }

    public interface ClickListener {
        boolean onMouseDown();

        boolean onMouseUp();
    }

    public interface HoverListener {
        void onHover(boolean isHovering);
    }

    private class ScrollButton extends Button {
        protected int color, scrolled, maxScrolled, _y, _height;
        protected float ratio;
        protected int scrollStart = -1;

        public ScrollButton(int x, int y, int width, int height, float ratio, int color) {
            super(x, y, width, (int)(height*ratio), -1, true);
            this._y = y;
            this._height = height;
            this.ratio = Math.min(ratio, 1);
            this.ratio = Math.max(this.ratio, 0);
            this.color = color;
            this.maxScrolled = (int)((1-this.ratio)*this._height);
        }

        @Override
        public void displayActive(MatrixStack matrices) {
            drawColor(matrices, xOffset + this.x, yOffset + this.y, this.width, this.height, this.color);
        }

        @Override
        protected void displayClicked(MatrixStack matrices) {
            drawTransparent(matrices, xOffset + this.x, yOffset + this.y, this.width, this.height, 0x22);
        }

        @Override
        protected void displayHovered(MatrixStack matrices) {}

        @Override
        protected void display(MatrixStack matrices, int mouseX, int mouseY) {
            int mouseDistance;
            if(this.isClicked()) {
                if(this.scrollStart == -1)
                    this.scrollStart = this.scrolled;
                mouseDistance = mouseY - this.dragY;
                this.scrolled = this.scrollStart + mouseDistance;
                this.scrolled = Math.min(this.scrolled, this.maxScrolled);
                this.scrolled = Math.max(this.scrolled, 0);
            } else {
                this.scrollStart = -1;
            }
            this.y = this._y + this.scrolled;
            super.display(matrices, mouseX+xOffset, mouseY+yOffset);
        }
        public float getScrolled() {
            return (float)this.scrolled / (float)this.maxScrolled;
        }
    }
}
