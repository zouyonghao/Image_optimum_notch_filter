package cn.edu.tsinghua.dip.catalano;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.FourierTransform;
import Catalano.Imaging.Filters.GaussianNoise;
import Catalano.Math.ComplexNumber;

import javax.swing.*;

public class OptimumNotchFilter2 {

    public static final String IMG_CATALANO_FOUDER = "img/catalano/2/";

    public static void main(String[] args) throws Exception {
        FastBitmap image = new FastBitmap("img/original.png");

        // System.out.println(image.isGrayscale());

        FourierTransform ft = new FourierTransform(image);
        ft.Forward();
        // showImage(ft.toFastBitmap());
        ft.toFastBitmap().saveAsPNG(IMG_CATALANO_FOUDER + "image_original_fourier.png");

        // 1. add periodic noise and gaussian noise
        GaussianNoise gaussianNoise = new GaussianNoise(20);
        gaussianNoise.applyInPlace(image);
        image.saveAsPNG(IMG_CATALANO_FOUDER + "image_gaussian_noise.png");
        PeriodicNoise periodicNoise = new PeriodicNoise();
        periodicNoise.applyInPlace(image);
        image.saveAsPNG(IMG_CATALANO_FOUDER + "image_periodic_noise.png");

        // 2. fourier transform
        ft = new FourierTransform(image);
        ft.Forward();
        // showImage(ft.toFastBitmap());
        ft.toFastBitmap().saveAsPNG(IMG_CATALANO_FOUDER + "image_noise_fourier.png");

        // 4. get the noise image
        FastBitmap noiseMap = periodicNoise.getNoiseMap(image);
        FourierTransform noiseFT = new FourierTransform(noiseMap);
        noiseFT.Forward();
        noiseFT.toFastBitmap().saveAsPNG(IMG_CATALANO_FOUDER + "image_noise_filtered1.png");
        noiseFT.Backward();
        noiseFT.toFastBitmap().saveAsPNG(IMG_CATALANO_FOUDER + "image_noise_filtered2.png");

        // 5. image - w * noise
        noiseMap = noiseFT.toFastBitmap();

        FastBitmap imageMulNoise = new FastBitmap(image.getWidth(), image.getHeight(), FastBitmap.ColorSpace.Grayscale);
        FastBitmap noiseMulNoise = new FastBitmap(image.getWidth(), image.getHeight(), FastBitmap.ColorSpace.Grayscale);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int tmp = image.getGray(i, j) * noiseMap.getGray(i, j);
                imageMulNoise.setGray(i, j, tmp);
                tmp = noiseMap.getGray(i, j) * noiseMap.getGray(i, j);
                noiseMulNoise.setGray(i, j, tmp);
            }
        }

        FastBitmap finalImage = new FastBitmap(image.getWidth(), image.getHeight(), FastBitmap.ColorSpace.Grayscale);
        FastBitmap finalImageWithW = new FastBitmap(image.getWidth(), image.getHeight(), FastBitmap.ColorSpace.Grayscale);
        int filterSize = 31;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                double w = (getLocalMean(imageMulNoise, i, j, filterSize)
                        - getLocalMean(image, i, j, filterSize) * getLocalMean(noiseMap, i, j, filterSize))
                        / (getLocalMean(noiseMulNoise, i, j, filterSize)
                        - getLocalMean(noiseMap, i, j, filterSize) * getLocalMean(noiseMap, i, j, filterSize));

                finalImage.setGray(i, j, (int) (image.getGray(i, j) - noiseMap.getGray(i, j)));
                int tmp = Math.min(255,  (int) (image.getGray(i, j) - w * noiseMap.getGray(i, j)));
                tmp = Math.max(0, tmp);
                finalImageWithW.setGray(i, j, tmp);
            }
        }

        finalImage.saveAsPNG(IMG_CATALANO_FOUDER + "image_final.png");
        finalImageWithW.saveAsPNG(IMG_CATALANO_FOUDER + "image_final_w.png");
        // showImage(ft.toFastBitmap());

    }

    private static ComplexNumber[][] deepCopy(ComplexNumber[][] data) {
        ComplexNumber[][] newData = new ComplexNumber[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                newData[i][j] = new ComplexNumber();
                newData[i][j].real = data[i][j].real;
                newData[i][j].imaginary = data[i][j].imaginary;
            }
        }
        return newData;
    }

    private static double getLocalMean(FastBitmap image, int i, int j, int filterSize) {
        int halfSize = filterSize / 2;
        if (halfSize < 2) {
            return image.getGray(i, j);
        }
        int left = i - halfSize;
        int right = i + halfSize;
        int top = j - halfSize;
        int bottom = j + halfSize;
        double sum = 0;
        int count = 0;
        for (int a = left; a < right; a++) {
            for (int b = top; b < bottom; b++) {
                if (a > 0 && b > 0 && a < image.getWidth() && b < image.getHeight()) {
                    sum += image.getGray(a, b);
                    count++;
                } else {
                    count++;
                }
            }
        }
        return sum / count;
    }

    public static void showImage(FastBitmap image) {
        JOptionPane.showMessageDialog(null, image.toIcon());
    }

}
