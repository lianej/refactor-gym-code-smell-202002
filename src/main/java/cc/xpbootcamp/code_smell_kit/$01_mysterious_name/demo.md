//代码中喜欢使用缩写
//如addr desc val msg err等

```java
public class Person {
  
  private String name;
  private String addr;
  private String desc;

  public Person(String name, String addr) {
    this.name = name;
    this.addr = addr;
  }
}
```

```typescript
export const validDate = (val: unknown) => {
  if (!val) {
    return undefined;
  }
  return isMoment(val);
};
```

`getPDA` means`getPremiumDiscountAmount`
```java
public class Order {
    private final double totalAmount;
    private final boolean isPremium;

    public Order(double totalAmount, boolean isPremium) {
        this.totalAmount = totalAmount;
        this.isPremium = isPremium;
    }

    public double getPDA() {
        return isPremium ? totalAmount * .15 : 0;
    }
}
```
