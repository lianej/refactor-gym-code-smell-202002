
全局变量: 在大多数spring-based的java工程中, 都存在类似的工具类: 

将spring context保存到全局静态字段, 以便于在非spring管理的类中获取spring bean.

```java
@Component
public class SpringApplicationContext implements ApplicationContextAware {
    private static ApplicationContext CONTEXT;

    @Override
    public void setApplicationContext(final ApplicationContext context)
            throws BeansException {
        CONTEXT = context;
    }

    public static ApplicationContext getContext(){
        return CONTEXT;
    }

    public static <T> T getBean(Class<T> clazz) {
        return CONTEXT.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return CONTEXT.getBean(name, clazz);
    }
}
```