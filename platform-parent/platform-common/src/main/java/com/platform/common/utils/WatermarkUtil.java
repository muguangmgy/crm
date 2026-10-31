package com.platform.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图片水印工具类
 */
@Slf4j
public class WatermarkUtil {

    private static final String DEFAULT_FONT_NAME = "微软雅黑";
    private static final Color WATERMARK_COLOR = new Color(255, 255, 255, 180); // 白色半透明
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 100); // 阴影颜色

    /**
     * 为图片添加文字水印
     *
     * @param imageBytes    原图字节数组
     * @param extension     图片扩展名（如 jpg, png）
     * @param watermarkText 水印文字（多行用 \n 分隔）
     * @return 添加水印后的图片字节数组
     */
    public static byte[] addWatermark(byte[] imageBytes, String extension, String watermarkText) {
        if (imageBytes == null || watermarkText == null || watermarkText.isEmpty()) {
            return imageBytes;
        }

        try {
            // 读取原图
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage originalImage = ImageIO.read(bis);
            if (originalImage == null) {
                log.warn("无法读取图片，跳过水印处理");
                return imageBytes;
            }

            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            // 创建新图片（支持透明度）
            BufferedImage watermarkedImage = new BufferedImage(
                    width, height,
                    extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("gif")
                            ? BufferedImage.TYPE_INT_ARGB
                            : BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g2d = watermarkedImage.createGraphics();

            // 设置渲染质量
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // 绘制原图
            g2d.drawImage(originalImage, 0, 0, null);

            // 计算字体大小（根据图片尺寸自适应）
            int fontSize = Math.max(12, Math.min(width, height) / 30);
            Font font = new Font(DEFAULT_FONT_NAME, Font.PLAIN, fontSize);

            // 分割水印文字
            String[] lines = watermarkText.split("\n");
            FontMetrics fontMetrics = g2d.getFontMetrics(font);

            // 计算水印区域
            int padding = Math.max(10, width / 50);
            int lineHeight = fontMetrics.getHeight();
            int totalHeight = lines.length * lineHeight;
            int maxWidth = 0;
            for (String line : lines) {
                int lineWidth = fontMetrics.stringWidth(line);
                if (lineWidth > maxWidth) {
                    maxWidth = lineWidth;
                }
            }

            // 水印位置：右下角
            int x = width - maxWidth - padding;
            int y = height - totalHeight - padding;

            // 确保坐标为正
            x = Math.max(padding, x);
            y = Math.max(padding + lineHeight, y);

            g2d.setFont(font);

            // 绘制水印文字（带阴影效果）
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                int lineY = y + i * lineHeight;

                // 绘制阴影
                g2d.setColor(SHADOW_COLOR);
                g2d.drawString(line, x + 1, lineY + 1);

                // 绘制文字
                g2d.setColor(WATERMARK_COLOR);
                g2d.drawString(line, x, lineY);
            }

            g2d.dispose();

            // 输出图片
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            String formatName = getFormatName(extension);
            ImageIO.write(watermarkedImage, formatName, bos);

            log.info("水印添加成功: 水印文字={}, 字体大小={}px", watermarkText.replace("\n", " / "), fontSize);
            return bos.toByteArray();

        } catch (IOException e) {
            log.error("添加水印失败", e);
            return imageBytes;
        }
    }

    /**
     * 检查是否需要添加水印的图片类型
     */
    public static boolean isWatermarkableImage(String extension) {
        if (extension == null) {
            return false;
        }
        String ext = extension.toLowerCase();
        return ext.equals("jpg") || ext.equals("jpeg") || ext.equals("png") || ext.equals("webp");
    }

    /**
     * 获取图片格式名称
     */
    private static String getFormatName(String extension) {
        if (extension == null) {
            return "jpg";
        }
        String ext = extension.toLowerCase();
        if (ext.equals("jpg") || ext.equals("jpeg")) {
            return "jpg";
        }
        return ext;
    }
}