package cc.bearvalley.badminton.common;

/**
 * 常量类
 */
public interface Const {
    /**
     * 错误的页面文件名
     */
    String ERROR_PAGE = "error";

    /**
     * 错误页面上的信息提示变量名
     */
    String ERROR_PAGE_MESSAGE = "msg";

    /**
     * 默认后台页每页显示的数量
     */
    int DEFAULT_ADMIN_PAGE_SIZE = 10;

    /**
     * 默认前台挑战一行显示的投票数
     */
    int DEFAULT_VOTER_NUMBER_IN_LINE = 6;

    /**
     * 黑色字体颜色色号值
     */
    String COLOR_BLACK = "000000";

    /**
     * 灰色字体颜色色号值
     */
    String COLOR_GREY = "999999";

    /**
     * 图片上传的临时保存目录
     */
    String UPLOAD_TEMP_DIR = "/tmp/";
}
