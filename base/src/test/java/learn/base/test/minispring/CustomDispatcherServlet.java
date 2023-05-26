package learn.base.test.minispring;

import learn.base.test.minispring.annotation.CustomAutowired;
import learn.base.test.minispring.annotation.CustomController;
import learn.base.test.minispring.annotation.CustomRequestMapping;
import learn.base.test.minispring.annotation.CustomRequestParam;
import learn.base.test.minispring.annotation.CustomService;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Zephyr
 * @since 2022-1-3.
 */
public class CustomDispatcherServlet extends HttpServlet {
    // key: 类的引用路径,  value: 对应类的一个实例
    private static Map<String,Object> beans = new HashMap<>();
    //保存url和Method的对应关系
    private static Map<String,Object> handlerMappings = new HashMap<>();
    //xx.properties配置文件中的内容
    private static Properties contextConfig;

    public CustomDispatcherServlet() {
        super();
    }


    /**
     * 使用配置文件初始化
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            // for test
            contextConfig = contextConfig != null ? contextConfig :
                    this.loadProperties(config.getInitParameter("contextConfigLocation"));

            // "扫描指定路径下的类，将类名put到map中
            doScan(contextConfig.getProperty("scanPackage"));

            // 实例化带有相关注解的bean
            doInstanceByAnnotation();

            // 根据RequestMapping注解初始化HandlerMapping
            initHandlerMappings();

            // 依赖注入
            doAutowired();

            System.out.println("Custom MVC Framework initialized ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 读取配置文件并加载为Properties
    private Properties loadProperties(String resourceName) throws IOException {
        Properties props = new Properties();
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourceName)) {
            // 读取配置文件并加载为Properties
            props.load(is);
        }
        return props;
    }

    private void initHandlerMappings() {
        for (Object beanInstance : beans.values()) {
            if (beanInstance == null) {
                continue;
            }
            Class<?> clazz = beanInstance.getClass();
            if (clazz.isAnnotationPresent(CustomController.class)) {
                // 处理Controller类上面的RequestMapping注解作为baseUrl
                String baseUrl = "";
                if (clazz.isAnnotationPresent(CustomRequestMapping.class)) {
                    CustomRequestMapping requestMapping = clazz.getAnnotation(CustomRequestMapping.class);
                    baseUrl = requestMapping.value();
                }
                // 处理方法上面的RequestMapping注解，组装为完整的url
                for (Method method : clazz.getDeclaredMethods()) {
                    CustomRequestMapping requestMapping;
                    if ((requestMapping = method.getAnnotation(CustomRequestMapping.class)) != null) {
                        String url = "".equals(requestMapping.value()) ? baseUrl : baseUrl + "/" + requestMapping.value();
                        handlerMappings.put(url.replaceAll("/+", "/"), method);
                    }
                }
            }
        }
    }

    private void doAutowired() {
        System.out.println("开始处理@Autowired注解");
        for (Object beanInstance : beans.values()) {
            if (beanInstance == null) {continue;}
            Class<?> clazz = beanInstance.getClass();
            if (clazz.isAnnotationPresent(CustomController.class) || clazz.isAnnotationPresent(CustomService.class)) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (!field.isAnnotationPresent(CustomAutowired.class)) {
                        continue;
                    }
                    CustomAutowired autowired = field.getAnnotation(CustomAutowired.class);
                    Object fieldValue;
                    if ("".equals(autowired.value())) {
                        fieldValue = beans.get(field.getType().getName());
                    } else {
                        fieldValue = beans.get(autowired.value());
                    }
                    try {
                        field.setAccessible(true);
                        field.set(beanInstance, fieldValue);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void doInstanceByAnnotation() {
        System.out.println("开始针对不同的注解实例化bean");
        try {
            Iterator<Map.Entry<String, Object>> beansIterator = beans.entrySet().iterator();
            while (beansIterator.hasNext()){
                Map.Entry<String, Object> entry = beansIterator.next();
                String className = entry.getKey();
                //if (!className.contains(".")) {
                //    beansIterator.remove();
                //    continue;
                //}

                Class<?> clazz = Class.forName(className);
                // 类上面存在Controller注解
                if (clazz.isAnnotationPresent(CustomController.class)) {
                    // 初始化controller实例并放入mappings
                    beans.put(className, clazz.newInstance());
                }
                // 类上面存在Service注解
                else if (clazz.isAnnotationPresent(CustomService.class)) {
                    CustomService serviceAnnotation = clazz.getAnnotation(CustomService.class);
                    Object instance = clazz.newInstance();
                    // 根据Service注解的定义保存一个映射关系
                    String beanName = "".equals(serviceAnnotation.value()) ? clazz.getName() : serviceAnnotation.value();
                    beans.put(beanName, instance);
                    // 根据Service的接口名保存一个bean的映射关系，其他bean中的字段声明一般都使用接口而不是实现类
                    for (Class<?> i : clazz.getInterfaces()) {
                        beans.put(i.getName(), instance);
                    }
                // 不是任何类型的bean，移除该"bean"
                } else {
                    if (entry.getValue() == null) {
                        beansIterator.remove();
                    }
                }
            }
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            System.err.println("Bean init error...");
            e.printStackTrace();
        }
    }

    /**
     * 递归扫描指定路径下的类
     */
    private void doScan(String packageName) {
        String dirPath = packageName.replaceAll("\\.", "/");
        // 只能读取main.java下的文件资源，不能读test.java下的资源
        URL resource = this.getClass().getClassLoader().getResource(dirPath);
        File packageFile = new File(Objects.requireNonNull(resource).getFile());
        for (File file : packageFile.listFiles()) {
            if (file.isFile()) {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String classPathWithoutSuffix = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                beans.put(classPathWithoutSuffix, null);
            } else {
                doScan(packageName + "." + file.getName());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.processRequest(req, resp);
        } catch (Exception e) {
            // 将异常信息格式化为适合在浏览器上显示的格式
            String errorResp = "500 Internal Error.\n" + Arrays.toString(e.getStackTrace())
                    .replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n");
            resp.getWriter().write(errorResp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.processRequest(req, resp);
    }

    /**
     * 处理http请求
     */
    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        Method method = (Method) handlerMappings.get(url);
        if (method == null) {
            resp.getWriter().write("404 Not Found");
            return;
        }

        // 解析Controller类中的方法参数列表，填充对应参数值
        Map<String, String[]> parameterMap = req.getParameterMap();
        Parameter[] parameters = method.getParameters();
        Object[] reqParams = new Object[parameters.length];
        CustomRequestParam requestParam;
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getType().isAssignableFrom(HttpServletRequest.class)) {
                reqParams[i] = req;
            } else if (parameter.getType().isAssignableFrom(HttpServletResponse.class)) {
                reqParams[i] = resp;
            } else {
                String paramName = parameter.getName();
                if ((requestParam = parameter.getAnnotation(CustomRequestParam.class)) != null) {
                    paramName = "".equals(requestParam.value()) ? paramName : requestParam.value();
                }
                //xxx 这里，如果请求参数里没有对应参数值，会出现空指针异常
                reqParams[i] = Arrays.toString(parameterMap.get(paramName))
                        .replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
            }
        }

        // 获取Controller类
        Object controllerInstance = beans.get(method.getDeclaringClass().getName());
        try {
            method.invoke(controllerInstance, reqParams);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ServletException("controller method invoke error.");
        }
    }

    //将类名首字母改为小写
    private String toLowerFirstCase(String simpleName) {
        return Character.toUpperCase(simpleName.charAt(0)) + simpleName.substring(1);
    }


    public static void main(String[] args) throws IOException, ServletException {
        CustomDispatcherServlet dispatcherServlet = new CustomDispatcherServlet();
        contextConfig = dispatcherServlet.loadProperties("spring-mini.properties");
        dispatcherServlet.init(new HttpServlet() {});

        String requestURI = "/custom/say";
        Map<String, String[]> parameterMap = new HashMap<String, String[]>() {{
            put("name", new String[]{"from...you"});
            put("NAME", new String[]{"from...you", " and...me..."});
        }};
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(requestURI);
        request.setParameterMap(parameterMap);
        dispatcherServlet.doGet(request, null);
    }

    private static class MockHttpServletRequest implements HttpServletRequest {
        private String requestURI;
        private Map<String, String[]> parameterMap;

        public void setRequestURI(String requestURI) {
            this.requestURI = requestURI;
        }

        public void setParameterMap(Map<String, String[]> parameterMap) {
            this.parameterMap = parameterMap;
        }

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return new Cookie[0];
        }

        @Override
        public long getDateHeader(String s) {
            return 0;
        }

        @Override
        public String getHeader(String s) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(String s) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public int getIntHeader(String s) {
            return 0;
        }

        @Override
        public String getMethod() {
            return null;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getContextPath() {
            return "";
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String s) {
            return false;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return requestURI;
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public String getServletPath() {
            return null;
        }

        @Override
        public HttpSession getSession(boolean b) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }

        @Override
        public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
            return false;
        }

        @Override
        public void login(String s, String s1) throws ServletException {

        }

        @Override
        public void logout() throws ServletException {

        }

        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return null;
        }

        @Override
        public Part getPart(String s) throws IOException, ServletException {
            return null;
        }

        @Override
        public <
        T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
            return null;
        }

        @Override
        public Object getAttribute(String s) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

        }

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public String getParameter(String s) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String s) {
            return new String[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return parameterMap;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String s, Object o) {

        }

        @Override
        public void removeAttribute(String s) {

        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String s) {
            return null;
        }

        @Override
        public String getRealPath(String s) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        @Override
        public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
        }
    }
}
