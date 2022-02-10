在上一个版本中，关于parse或者to_value代码是这么实现的
```
public void parse(Set check_variables_set){
    Optional.ofNullable(this.name).ifPresent( n -> n.parse(check_variables_set));
    Optional.ofNullable(this.base_url).ifPresent( b -> b.parse(check_variables_set));
    Optional.ofNullable(this.variables).ifPresent( v -> v.parse(check_variables_set));
    Optional.ofNullable(this.setup_hooks).ifPresent( h -> h.parse(check_variables_set));
    Optional.ofNullable(this.teardown_hooks).ifPresent( t -> t.parse(check_variables_set));
}
```
这种写法比较土，如果有五十个组件，岂不是要调用五十次parse方法？

暂时有一个idea，可以在每一个组件上加注解，比如
```
@Component
private LazyString name;
```
Component注解能做什么？可以将注解的每一个成员变量放入一个list中，在parse方法中，循环处理该list中每个组成部分，这样就可以以一个循环来处理每一个组件了。



