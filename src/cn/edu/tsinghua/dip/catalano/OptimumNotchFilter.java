package cn.edu.tsinghua.dip.catalano;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.FourierTransform;
import Catalano.Imaging.Filters.GaussianNoise;

import javax.swing.*;
import java.lang.reflect.Field;

public class OptimumNotchFilter {

    public static void main(String[] args) throws Exception {
        FastBitmap image = new FastBitmap("img/original.png");

        System.out.println(image.isGrayscale());

        FourierTransform ft = new FourierTransform(image);
        ft.Forward();
        // showImage(ft.toFastBitmap());
        ft.toFastBitmap().saveAsPNG("img/catalano/image_original_fourier.png");

        // 1. add periodic noise and gaussian noise
        GaussianNoise gaussianNoise = new GaussianNoise(20);
        gaussianNoise.applyInPlace(image);
        image.saveAsPNG("img/catalano/image_gaussian_noise.png");
        PeriodicNoise periodicNoise = new PeriodicNoise();
        periodicNoise.applyInPlace(image);
        image.saveAsPNG("img/catalano/image_periodic_noise.png");

        // 2. fourier transform
        ft = new FourierTransform(image);
        ft.Forward();
        // showImage(ft.toFastBitmap());
        ft.toFastBitmap().saveAsPNG("img/catalano/image_noise_fourier.png");

        // 3. mark the point
        // 4. get the noise image
        // 5. image - w * noise

        showImage(ft.toFastBitmap());

    }

    public static void showImage(FastBitmap image) {
        JOptionPane.showMessageDialog(null, image.toIcon());
    }

}
