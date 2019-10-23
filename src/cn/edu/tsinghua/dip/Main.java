package cn.edu.tsinghua.dip;

import Catalano.Math.Matrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        File file = new File("img/original.png");
        BufferedImage img = ImageIO.read(file);
        double[][] pixels = new double[Constant.WIDTH][Constant.HEIGHT];
        double[][] gaussianNoise = new double[Constant.WIDTH][Constant.HEIGHT];
        double[][] periodicNoise = new double[Constant.WIDTH][Constant.HEIGHT];
        Raster raster = img.getData();

        BufferedImage destImg = new BufferedImage(Constant.WIDTH, Constant.HEIGHT, BufferedImage.TYPE_INT_RGB);
        double[][] imageMulNoise = new double[Constant.WIDTH][Constant.HEIGHT];
        double[][] noiseMulNoise = new double[Constant.WIDTH][Constant.HEIGHT];

        for (int i = 0; i < Constant.WIDTH; i++) {
            for (int j = 0; j < Constant.HEIGHT; j++) {
                pixels[i][j] = raster.getSample(i, j, 0);
                gaussianNoise[i][j] = GaussianNoise.getPixel();
                periodicNoise[i][j] = PeriodicNoise.getPixel(i, j);
                //
                // imageMulNoise[i][j] = pixels[i][j] * gaussianNoise[i][j];
                // noiseMulNoise[i][j] = gaussianNoise[i][j] * gaussianNoise[i][j];

                // noiseMulNoise[i][j] = periodicNoise[i][j] * periodicNoise[i][j];

                // add noise
                pixels[i][j] += (gaussianNoise[i][j] + periodicNoise[i][j]);

                // imageMulNoise[i][j] = pixels[i][j] * periodicNoise[i][j];

                if (pixels[i][j] > 255) {
                    pixels[i][j] = 255;
                }
                if (pixels[i][j] < 0) {
                    pixels[i][j] = 0;
                }

                // imageMulNoise[i][j] = pixels[i][j] * periodicNoise[i][j];

                // pixels[i][j] -= periodicNoise[i][j];
                // if (pixels[i][j] > 255) {
                //     pixels[i][j] = 255;
                // }
                // if (pixels[i][j] < 0) {
                //     pixels[i][j] = 0;
                // }

                destImg.setRGB(i, j, new Color((int) pixels[i][j], (int) pixels[i][j], (int) pixels[i][j]).getRGB());
            }
        }

        ImageIO.write(destImg, "png", new File("img/processed1.png"));

        noiseMulNoise = Matrix.DotProduct(periodicNoise, periodicNoise);
        imageMulNoise = Matrix.DotProduct(pixels, periodicNoise);

        int[][] newPixels = new int[pixels.length][pixels[0].length];

        for (int i = 0; i < Constant.WIDTH; i++) {
            for (int j = 0; j < Constant.HEIGHT; j++) {
                // newPixels[i][j] = (int) (pixels[i][j] - periodicNoise[i][j]);
                newPixels[i][j] = filter(i, j, pixels, periodicNoise, imageMulNoise, noiseMulNoise);
                if (newPixels[i][j] > 255) {
                    newPixels[i][j] = 255;
                }
                if (newPixels[i][j] < 0) {
                    newPixels[i][j] = 0;
                }

                destImg.setRGB(i, j, new Color(newPixels[i][j], newPixels[i][j], newPixels[i][j]).getRGB());
            }
        }

        ImageIO.write(destImg, "png", new File("img/processed2.png"));
    }

    private static int filter(int i, int j, double[][] pixels, double[][] noise, double[][] imageMulNoise, double[][] noiseMulNoise) {
        int filterSize = 31;
        double imageMean = getLocalMean(i, j, pixels, filterSize);
        double noiseMean = getLocalMean(i, j, noise, filterSize);
        double imageNoiseMean = getLocalMean(i, j, imageMulNoise, filterSize);
        double noiseNoiseMean = getLocalMean(i, j, noiseMulNoise, filterSize);

        double w = (imageNoiseMean - imageMean * noiseMean) / (noiseNoiseMean - noiseMean * noiseMean);

        return (int) (pixels[i][j] - w * noise[i][j]);
    }

    private static double getLocalMean(int i, int j, double[][] pixels, int filterSize) {
        double sum = 0;
        int count = 0;
        for (int a = i; a < i + filterSize; a++) {
            for (int b = j; b < j + filterSize; b++) {
                if (a > 0 && b > 0 && a < pixels.length && b < pixels[0].length) {
                    sum += pixels[a][b];
                    count++;
                } else {
                    // count++;
                }
            }
        }
        return sum / count;
    }
}
