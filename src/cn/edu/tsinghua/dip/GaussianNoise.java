package cn.edu.tsinghua.dip;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GaussianNoise {

    private static final int SIGMA = 20;

    public static void main(String[] args) throws IOException {
        String imageName = "img/gaussian_noise.png";

        BufferedImage destImg = new BufferedImage(Constant.WIDTH, Constant.HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < Constant.WIDTH; i++) {
            for (int j = 0; j < Constant.HEIGHT; j++) {
                int pixel = (int) (getPixel() * 255);
                if (pixel < 0) {
                    pixel = 0;
                }
                if (pixel > 255) {
                    pixel = 255;
                }

                destImg.setRGB(i, j, new Color(pixel, pixel, pixel).getRGB());
            }
        }

        ImageIO.write(destImg, "png", new File(imageName));
    }

    public static double getPixel() {
        double randNum1 = Math.random();
        double randNum2 = Math.random();
        return Math.sqrt(-2 * Math.log(randNum1)) * Math.cos(2 * Math.PI * randNum2) * SIGMA;
    }
}
