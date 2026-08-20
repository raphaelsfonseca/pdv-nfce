package com.pdv.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ControllerAdvice
public class CurrentPathAdvice {

    @ModelAttribute
    public void addCurrentPath(java.util.Map<String, Object> model) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            model.put("currentPath", request.getRequestURI());
        }
    }
}
