package org.thymeleaf.spring.support;

import org.springframework.util.Assert;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.thymeleaf.spring.support.Layout;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThymeleafLayoutInterceptor extends HandlerInterceptorAdapter {

    private static final String DEFAULT_LAYOUT = "layouts/default";
    private static final String DEFAULT_VIEW_ATTRIBUTE_NAME = "view";

    private String defaultLayout = DEFAULT_LAYOUT;
    private String viewAttributeName = DEFAULT_VIEW_ATTRIBUTE_NAME;

    public void setDefaultLayout(String defaultLayout) {
        Assert.hasLength(defaultLayout);
        this.defaultLayout = defaultLayout;
    }

    public void setViewAttributeName(String viewAttributeName) {
        Assert.hasLength(defaultLayout);
        this.viewAttributeName = viewAttributeName;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView == null || !modelAndView.hasView() || !useLayout(handler)) {
            return;
        }

        String originalViewName = modelAndView.getViewName();
        if (isRedirectOrForward(originalViewName)) {
            return;
        }
        String layoutName = getLayoutName(handler);
        modelAndView.setViewName(layoutName);
        modelAndView.addObject(this.viewAttributeName, originalViewName);
    }

    private boolean isRedirectOrForward(String viewName) {
        return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
    }

    private boolean useLayout(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        org.thymeleaf.spring.support.Layout layout = getMethodOrTypeAnnotation(handlerMethod);
        if (layout == null) {
            return true;
        } else {
            return layout.enabled();
        }
    }

    private String getLayoutName(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        org.thymeleaf.spring.support.Layout layout = getMethodOrTypeAnnotation(handlerMethod);
        if (layout == null) {
            return this.defaultLayout;
        } else {
            return layout.value();
        }
    }

    private org.thymeleaf.spring.support.Layout getMethodOrTypeAnnotation(HandlerMethod handlerMethod) {
        org.thymeleaf.spring.support.Layout layout = handlerMethod.getMethodAnnotation(org.thymeleaf.spring.support.Layout.class);
        if (layout == null) {
            return handlerMethod.getBeanType().getAnnotation(Layout.class);
        }
        return layout;
    }
}
