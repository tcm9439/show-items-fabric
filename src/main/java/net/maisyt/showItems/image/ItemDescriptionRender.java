package net.maisyt.showItems.image;

import net.maisyt.minecraft.util.text.Text;
import net.maisyt.showItems.ShowItemsMod;
import net.maisyt.showItems.message.itemInfo.SingleItemInfo;
import net.minecraft.text.TextColor;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ItemDescriptionRender extends ImageRender<SingleItemInfo> {
    private static final int FONT_SIZE = 16;
    private static final int FONT_SHIFT = FONT_SIZE/4;
    private static final int MARGIN = 4;
    private static final int DESCRIPTION_IMAGE_WIDTH = 270; // todo: make this configurable
    private static final int BACKGROUND_FRAME_SHIFT = 1;


    private SingleItemInfo singleItemInfo;

    public ItemDescriptionRender(SingleItemInfo singleItemInfo) {
        this.singleItemInfo = singleItemInfo;
    }

    @Override
    public BufferedImage render() {
        if (!singleItemInfo.hasTooltips()){
            return null;
        }

        return renderBackground().renderItemName().renderTooltips().getFinalRenderedImage();
    }

    private int calculateImageHeight() {
        int lineCount = singleItemInfo.getTooltips().size() + 1;
        return (FONT_SIZE + FONT_SHIFT) * lineCount - FONT_SHIFT + MARGIN * 2 + BACKGROUND_FRAME_SHIFT * 2;
    }

    private ItemDescriptionRender renderBackground() {
        ShowItemsMod.LOGGER.trace("Rendering background image");
        int imageHeight = calculateImageHeight();
        renderedImage = createBackgroundImage(DESCRIPTION_IMAGE_WIDTH, imageHeight);

        g2d.setColor(new Color(14, 3, 15));
        g2d.fillRect(0, 0, DESCRIPTION_IMAGE_WIDTH, imageHeight);
        g2d.setColor(new Color(25, 0, 61));
        g2d.drawRect(BACKGROUND_FRAME_SHIFT, BACKGROUND_FRAME_SHIFT, DESCRIPTION_IMAGE_WIDTH - BACKGROUND_FRAME_SHIFT * 3, imageHeight - BACKGROUND_FRAME_SHIFT * 3);

        return this;
    }

    private ItemDescriptionRender renderItemName() {
        ShowItemsMod.LOGGER.trace("Rendering item name");

        // set color
        drawText(singleItemInfo.getItemName(), MARGIN, MARGIN + FONT_SIZE);
        g2d.drawString(singleItemInfo.getItemName().getDisplayString(), MARGIN, MARGIN + FONT_SIZE);
        return this;
    }

    private Color getTextColor(Text text){
        if (text.getStyle() == null || text.getStyle().getColor() == null){
            return Color.WHITE;
        } else {
            TextColor textColor = text.getStyle().getColor();
            return new Color(textColor.getRgb());
        }
    }

    private void setPenColor(Text text){
        g2d.setColor(getTextColor(text));
    }

    private ItemDescriptionRender renderTooltips() {
        ShowItemsMod.LOGGER.trace("Rendering tooltips, size: {}", singleItemInfo.getTooltips().size());

        for (int i = 0; i < singleItemInfo.getTooltips().size(); i++) {
            setPenColor(singleItemInfo.getTooltips().get(i));
            drawText(singleItemInfo.getTooltips().get(i), MARGIN, MARGIN + FONT_SIZE + (FONT_SIZE + FONT_SHIFT) * (i + 1));
        }

        return this;
    }

    private void drawText(Text text, int xPosition, int yPosition){
        setPenColor(text);

        Map<TextAttribute, Object> textAttributes = new HashMap<>();
        if (text.getStyle() != null){
            if (text.getStyle().isBold()){
                textAttributes.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
            }
            if (text.getStyle().isItalic()){
                textAttributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
            }
            if (text.getStyle().isUnderlined()){
                textAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            }
            if (text.getStyle().isStrikethrough()){
                textAttributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            }
        }

        g2d.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE).deriveFont(textAttributes));
        g2d.drawString(text.getDisplayString(), xPosition, yPosition);
    }
}
