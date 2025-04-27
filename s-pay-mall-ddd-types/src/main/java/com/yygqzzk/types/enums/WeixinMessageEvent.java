package com.yygqzzk.types.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author zzk
 * @version 1.0
 * @since 2025/4/26
 */
@NoArgsConstructor
@Getter
public enum WeixinMessageEvent {
    SCAN("scan", "扫码登陆"), SUBSCRIBE("subscribe", "关注公众号"), UNSUBSCRIBE("unsubscribe", "取消关注公众号");

    private String code;
    private String desc;
    private WeixinMessageEvent(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
