package main.java.cc.xpbootcamp.code_smell_kit.$06_mutable_data;

@Data
@Entity
public class OrderEntity {
    private String orderId;
    private String orderName;
    private String status;
    private int version;

    /**
     *  更新order的version,但是orderEntity有@Data注解，可以set值，version 有updateVersion（）方法改变，还有set方法
     */
    public void updateVersion(){
        version += 1;
    }
}
