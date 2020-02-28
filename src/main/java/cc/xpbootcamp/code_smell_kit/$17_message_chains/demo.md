```
//有2个类，人员和部门表，类如下：
class Person {
	Department dept;
	//getter 和setter
}
class Department {
	private String code;
	private Person manager;
	//getter & setter;
}

//实际使用,如需要获取john的经理是谁
manager = john.getDepartment().getManager();
```