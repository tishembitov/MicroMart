package ru.tishembitov.authservice.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ContextHolder {

    public static final String CORRELATION_ID = "correlationId";
    private static final InheritableThreadLocal<ContextData> holder = new InheritableThreadLocal<>(); // Изменено на InheritableThreadLocal

    public void set(ContextData context) {
        holder.set(context);
    }

    public ContextData get() {
        return holder.get();
    }

    public void remove() {
        holder.remove();
    }

    public boolean isAuthenticated() {
        var customContext = this.get();
        if (customContext == null) {
            return false;
        }
        var isUsernameEmptyOrNull = StringUtils.isEmpty(customContext.getUsername());
        return !isUsernameEmptyOrNull && customContext.getUserId() != null;
    }

    public String getCorrelationId() {
        var context = this.get();
        return context != null ? context.getCorrelationId() : null;
    }

    public Long getUserId() {
        var context = this.get();
        return context != null ? context.getUserId() : null;
    }

    public String getUsername() {
        var context = this.get();
        return context != null ? context.getUsername() : null;
    }
}