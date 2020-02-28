package cc.xpbootcamp.code_smell_kit.$07_divergent_change;


/**
 * 删除了大量内容.<br>
 * UserService 具有添加/删除/更新用户的功能.<br>
 * 由于OTR的user分为dealerUser和dealerGroupUser, 而两种user的逻辑并不相同. 因此需要使用各自独立的方法.<br>
 * <br>
 * 另外, 有一个疑惑是: 按照发散式变化的定义, 绝大多数service类都可以被归类到其中.<br>
 * 由于三层架构的存在, 开发者一旦遇到需要调用spring bean的功能第一个会想到将其放到service中, 不可避免的会导致service膨胀, <br>
 * 在不改变三层架构的前提下, 有没有方法能避免/改善这个问题呢?
 */
public class UserService {

    public void updateUser(UserDTO updateUser) { }
    public Integer addUser(UserDTO user) { return 1; }

    public void updateDealerGroupUser(UserDTO updateUser) { }
    public Integer addDealerGroupUser(UserDTO user) { return 1; }

    //...省略其他方法
}

class UserDTO {
}
