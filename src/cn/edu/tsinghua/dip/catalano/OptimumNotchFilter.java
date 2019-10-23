package cn.edu.tsinghua.dip.catalano;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.FourierTransform;
import Catalano.Imaging.Filters.GaussianNoise;
import Catalano.Math.ComplexNumber;

import javax.swing.*;

public class OptimumNotchFilter {

    public static final String IMG_CATALANO = "img/catalano/";

    public static void main(String[] args) throws Exception {
        FastBitmap image = new FastBitmap("img/original.png");

        // System.out.println(image.isGrayscale());

        FourierTransform ft = new FourierTransform(image);
        ft.Forward();
        // showImage(ft.toFastBitmap());
        ft.toFastBitmap().saveAsPNG(IMG_CATALANO + "image_original_fourier.png");

        // 1. add periodic noise and gaussian noise
        GaussianNoise gaussianNoise = new GaussianNoise(20);
        gaussianNoise.applyInPlace(image);
        image.saveAsPNG(IMG_CATALANO + "image_gaussian_noise.png");
        PeriodicNoise periodicNoise = new PeriodicNoise();
        periodicNoise.applyInPlace(image);
        image.saveAsPNG(IMG_CATALANO + "image_periodic_noise.png");

        // 2. fourier transform
        ft = new FourierTransform(image);
        ft.Forward();
        // showImage(ft.toFastBitmap());
        ft.toFastBitmap().saveAsPNG(IMG_CATALANO + "image_noise_fourier.png");

        // 3. mark the point
        // BufferedImage subimage = ft.toFastBitmap().toBufferedImage().getSubimage(186, 148, 200, 200);
        // new FastBitmap(subimage).saveAsPNG("img/catalano/image_mark_point.png");
        int noisePointCount = 6;
        FourierTransform[] noiseFTs = new FourierTransform[noisePointCount];
        int[][] points = new int[][]{
                {0, 5, 170, 55},
                {0, 10, 228, 150},
                {0, 20, 287, 248},
                {0, 20, 403, 441},
                {0, 10, 462, 537},
                {0, 5, 518, 634}
        };
        for (int i = 0; i < noisePointCount; i++) {
            FourierTransform noiseFT = new FourierTransform(image);
            noiseFT.Forward();
            noiseFT.setData(deepCopy(ft.getData()));
            CenteredFrequencyFilter frequencyFilter = new CenteredFrequencyFilter(points[i][0], points[i][1], points[i][2], points[i][3]);
            frequencyFilter.ApplyInPlace(noiseFT);
            noiseFTs[i] = noiseFT;
        }
        FourierTransform noiseFT = new FourierTransform(image);
        noiseFT.Forward();
        ComplexNumber[][] data = noiseFT.getData();
        for (int i = 0; i < data.length; i++) {
            ComplexNumber[] row = data[i];
            for (int j = 0; j < row.length; j++) {
                data[i][j].imaginary = 0;
                data[i][j].real = 0;
                for (int k = 0; k < noisePointCount; k++) {
                    ComplexNumber tmp = noiseFTs[k].getData()[i][j];
                    // if (i == 229 && j == 152) {
                    //     System.out.println(tmp);
                    // }
                    if (tmp.real > 0 || tmp.imaginary > 0 || tmp.real < 0 || tmp.imaginary < 0) {
                        data[i][j].imaginary = tmp.imaginary;
                        data[i][j].real = tmp.real;
                    }
                }
            }
        }

        // 4. get the noise image
        noiseFT.toFastBitmap().saveAsPNG(IMG_CATALANO + "image_noise_filtered1.png");
        noiseFT.Backward();
        noiseFT.toFastBitmap().saveAsPNG(IMG_CATALANO + "image_noise_filtered2.png");

        // 5. image - w * noise
        FastBitmap noiseMap = noiseFT.toFastBitmap();

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
        int filterSize = 11;
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

        finalImage.saveAsPNG(IMG_CATALANO + "image_final.png");
        finalImageWithW.saveAsPNG(IMG_CATALANO + "image_final_w.png");
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
