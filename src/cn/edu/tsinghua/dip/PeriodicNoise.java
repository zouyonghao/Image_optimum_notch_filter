package cn.edu.tsinghua.dip;

class PeriodicNoise {

    static double getPixel(int i, int j) {
        return 20 * Math.sin(20 * i) + 20 * Math.sin(20 * j);
    }
}
