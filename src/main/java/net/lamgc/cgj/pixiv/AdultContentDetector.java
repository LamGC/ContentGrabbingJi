package net.lamgc.cgj.pixiv;

public interface AdultContentDetector {

    /**
     * 检查某一作品的成人内容判断指数
     * @param illustId 作品Id
     * @param isUgoira 是否为动图
     * @param pageIndex 指定页数, 设为0或负数则视为单页面作品
     * @return 返回成人作品判断指数(0 ~ 1), 需按照情况设置阀值.
     */
    double detect(int illustId, boolean isUgoira, int pageIndex) throws Exception;

    /**
     * 检查某一作品是否为成人内容
     * @param illustId 作品Id
     * @param isUgoira 是否为动图
     * @param pageIndex 指定页数, 设为0或负数则视为单页面作品
     * @return 如果为true则为成人作品, 该方法将由检测器决定如何为成人作品.
     */
    boolean isAdultContent(int illustId, boolean isUgoira, int pageIndex) throws Exception;

    /**
     * 检查某一作品是否为成人内容
     * @param illustId 作品Id
     * @param isUgoira 是否为动图
     * @param pageIndex 指定页数, 设为0或负数则视为单页面作品
     * @param threshold 指数阀值, 当等于或大于该阀值时返回true.
     * @return 如果为true则为成人作品, 该方法将由 threshold 参数决定是否为成人作品(如果超过阈值, 则为true).
     */
    boolean isAdultContent(int illustId, boolean isUgoira, int pageIndex, double threshold) throws Exception;

}
