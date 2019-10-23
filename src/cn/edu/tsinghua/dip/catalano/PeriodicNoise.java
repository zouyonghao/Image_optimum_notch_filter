package cn.edu.tsinghua.dip.catalano;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.IApplyInPlace;

public class PeriodicNoise implements IApplyInPlace {

    public PeriodicNoise() {
    }

    @Override
    public void applyInPlace(FastBitmap fastBitmap) {

        if (fastBitmap.isGrayscale()) {

            int size = fastBitmap.getSize();

            for (int i = 0; i < size; i++) {
                int g = fastBitmap.getGray(i);
                g += 100 * Math.sin(100 * i);

                g = Math.min(g, 255);
                g = Math.max(g, 0);

                fastBitmap.setGray(i, g);
            }

        } else {
            throw new IllegalArgumentException("Periodic noise only works in grayscale images.");
        }
    }

    public FastBitmap getNoiseMap(FastBitmap fastBitmap) {
        FastBitmap result = new FastBitmap(fastBitmap.getWidth(), fastBitmap.getHeight(), FastBitmap.ColorSpace.Grayscale);
        if (fastBitmap.isGrayscale()) {
            int size = fastBitmap.getSize();
            for (int i = 0; i < size; i++) {
                int g = (int) (100 * Math.sin(100 * i));

                // g = Math.min(g, 255);
                // g = Math.max(g, 0);

                result.setGray(i, g);
            }

        } else {
            throw new IllegalArgumentException("Periodic noise only works in grayscale images.");
        }
        return result;
    }
}