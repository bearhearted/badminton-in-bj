package cc.bearvalley.badminton.common;

/**
 * 出现错误的枚举类
 */
public enum ErrorEnum {
    UNKNOWN_ERROR("出现未知错误"),
    DATA_NOT_FOUND("未找到数据"),
    EVENT_NOT_FOUND("未找到活动"),
    ITEM_NOT_FOUND("未找到商品"),
    LOGIN_STATUS_ERROR("登录状态错误"),
    ADMIN_ACCOUNT_NOT_FOUND("未找到账号"),
    ROLE_NOT_FOUND("未找到角色"),
    USER_NOT_FOUND("未找到用户"),
    DATE_FORMAT_ERROR("日期格式错误"),
    DATA_ERROR("数据格式错误"),
    ORIGINAL_PASSWORD_ERROR("原始密码错误"),
    NAME_CONFLICT("名称重复"),
    FILE_SIZE_EXCEED("文件大小超过限制"),
    POINT_NOT_ENOUGH("积分不足");

    ErrorEnum(String message) {
        this.message = message;
    }

    private final String message;

    public String getMessage() {
        return this.message;
    }
}