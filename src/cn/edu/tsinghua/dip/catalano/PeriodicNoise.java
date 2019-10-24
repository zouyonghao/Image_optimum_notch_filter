package cn.edu.tsinghua.dip.catalano;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.IApplyInPlace;

public class PeriodicNoise implements IApplyInPlace {

    public PeriodicNoise() {
    }

    @Override
    public void applyInPlace(FastBitmap fastBitmap) {

        if (fastBitmap.isGrayscale()) {

            for (int i = 0; i < fastBitmap.getWidth(); i++) {
                for (int j = 0; j < fastBitmap.getHeight(); j++) {
                    int g = fastBitmap.getGray(i, j);
                    g += 20 * Math.sin(20 * j);

                    g = Math.min(g, 255);
                    g = Math.max(g, 0);

                    fastBitmap.setGray(i, j, g);
                }
            }
        } else {
            throw new IllegalArgumentException("Periodic noise only works in grayscale images.");
        }
    }

    public FastBitmap getNoiseMap(FastBitmap fastBitmap) {
        FastBitmap result = new FastBitmap(fastBitmap.getWidth(), fastBitmap.getHeight(), FastBitmap.ColorSpace.Grayscale);
        if (fastBitmap.isGrayscale()) {
            for (int i = 0; i < fastBitmap.getWidth(); i++) {
                for (int j = 0; j < fastBitmap.getHeight(); j++) {
                    int g = (int) (20 * Math.sin(20 * j));
                    // g = Math.min(g, 255);
                    // g = Math.max(g, 0);
                    result.setGray(i, j, g);
                }
            }

        } else {
            throw new IllegalArgumentException("Periodic noise only works in grayscale images.");
        }
        return result;
    }
}