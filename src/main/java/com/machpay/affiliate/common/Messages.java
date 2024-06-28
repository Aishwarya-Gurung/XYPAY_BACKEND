package com.machpay.affiliate.common;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class Messages {
    private final MessageSourceAccessor accessor;

    public Messages(MessageSource messageSource) {
        this.accessor = new MessageSourceAccessor(messageSource, LocaleContextHolder.getLocale());
    }

    public String get(String code) {
        return accessor.getMessage(code);
    }

    public String get(String code, String variable) {
        return accessor.getMessage(code, new Object[]{variable});
    }

    public String get(String code, String variable1, String variable2) {
        return accessor.getMessage(code, new Object[]{variable1, variable2});
    }
}
