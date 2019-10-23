package cn.edu.tsinghua.dip.catalano;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.IApplyInPlace;

import java.util.Random;

public class PeriodicNoise implements IApplyInPlace {

    public PeriodicNoise() {
    }

    @Override
    public void applyInPlace(FastBitmap fastBitmap) {

        if (fastBitmap.isGrayscale()) {

            int size = fastBitmap.getSize();

            Random r = new Random();
            for (int i = 0; i < size; i++) {
                int g = fastBitmap.getGray(i);
                g += 20 * Math.sin(100 * i);

                g = Math.min(g, 255);
                g = Math.max(g, 0);

                fastBitmap.setGray(i, g);
            }

        } else {
            throw new IllegalArgumentException("Periodic noise only works in grayscale images.");
        }
    }
}