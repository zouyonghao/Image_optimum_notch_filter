package cn.edu.tsinghua.dip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaltPepperNoise {
    public static void main(String[] args) throws IOException {
        String imageName = "img/salt_pepper_noise.png";
        BufferedImage destImg = new BufferedImage(Constant.WIDTH, Constant.HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < Constant.WIDTH; i++) {
            for (int j = 0; j < Constant.HEIGHT; j++) {
                int pixel = getPixel();
                destImg.setRGB(i, j, new Color(pixel, pixel, pixel).getRGB());
            }
        }

        ImageIO.write(destImg, "png", new File(imageName));
    }

    public static int getPixel() {
        double ps = 0.03;
        double pp = 0.03;
        double randNum1 = Math.random();
        int pixel = 0;
        if (randNum1 < ps) {
            pixel = 500;
        }
        if (randNum1 > (1 - pp)) {
            pixel = -500;
        }
        return pixel;
    }
}
