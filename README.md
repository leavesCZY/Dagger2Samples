# Dagger2Samples
Dagger2 入门笔记

网上对 Dagger2 进行介绍的文章也已经很多了，一开始看的时候却总是有种从入门到放弃的感觉，因为 Dagger2 中注解的配套使用是需要一定规则的，而文章介绍得并不算太详细，如果搭配不当，Dagger2 是不会为我们生成相应的文件的，这就导致应用在编译时总是遇到各种报错，然后就一脸蒙蔽，所以这就需要很多的实践操作了

这里我就将本人在学习 Dagger2 的过程中的实践记录下来，希望对你有所帮助

### 一、配置

```java
dependencies {
    implementation 'com.google.dagger:dagger:2.16'
    annotationProcessor 'com.google.dagger:dagger-compiler:2.16'
}
```

### 二、@Inject

假设当前有一个 Person 类，其声明如下所示

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:21
 * 描述：
 */
public class Person {

    private String name;

    public Person() {
        name = "person default name";
    }

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
```

在一般情况下，如果我们要使用到一个 Person 变量，就需要以如下方式来声明

```java
	Person person = new Person();
```

而这隐藏着一个问题，就是没当 Person 类的构造函数发生了变化时（参数变多或变少），所有使用到 Person 的代码就需要都修改一遍，这对于较大的项目来说是一件很耗时耗力的工作，Dagger2 就是用来解决这一问题的依赖注入框架

首先为 Person 的构造函数添加 **@Inject** 注解，指定 Dagger2 在为我们初始化 Person 变量时要调用的构造函数

```java
public class Person {
	
    @Inject
    public Person() {
        name = "person default name";
    }
    
	···
        
}
```

此外，还需要一个接口来作为 Person 和需要进行依赖注入的类之间的桥梁

此处即为 **PersonComponent** 接口，该接口需要使用 **@Component** 进行注解，且包含一个方法用于将需要使用到依赖注入的类对象传递进来，此外为 **MainActivity**，注意此处需要是确切的对象，而不能是任何父类对象

此外，接口名和方法名没有硬性规定

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
@Component
public interface PersonComponent {

    void inject(MainActivity mainActivity);

}
```

接下来就可以在 MainActivity 中进行依赖注入了

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Inject
    Person person1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerPersonComponent.builder().build().inject(this);

        Log.e(TAG, "person1: " + person1);
        Log.e(TAG, "person1 name : " + person1.getName());
    }

}
```

在运行前需要先 build 工程，这样 DaggerPersonComponent 类才会生成，运行结果如下所示

```java
person1: com.leavesc.dagger2samples.test1.Person@420a5ee0
person1 name : person default name
```

使用 **@Inject** 进行注解的 person1 变量我们并没有对其进行初始化，但是应用在运行时并没有报空指针异常，说明 Dagger2 在后台为我们进行初始化操作了

实际进行初始化操作的是以下代码

```java
	DaggerPersonComponent.builder().build().inject(this);
```

**DaggerPersonComponent** 是 Dagger2 依照 **PersonComponent** 的命名而生成的文件，可以点进去看下其源码

DaggerPersonComponent 实现了 PersonComponent 接口，在为 **person1** 赋值时是直接调用了 Person 类的无参构造函数。因为 MainActivity_MembersInjector 是依靠 MainActivity 对象引用到 person1 变量，因此在 person1 不能声明为私有的，否则引用不到 person1 也就无法实现依赖注入了

```java
public final class DaggerPersonComponent implements PersonComponent {
  private DaggerPersonComponent(Builder builder) {}

  public static Builder builder() {
    return new Builder();
  }

  public static PersonComponent create() {
    return new Builder().build();
  }

  @Override
  public void inject(MainActivity mainActivity) {
    injectMainActivity(mainActivity);
  }

  private MainActivity injectMainActivity(MainActivity instance) {
    MainActivity_MembersInjector.injectPerson1(instance, new Person());
    return instance;
  }

  public static final class Builder {
    private Builder() {}

    public PersonComponent build() {
      return new DaggerPersonComponent(this);
    }
  }
}

```

```java
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
    
  ···

  public static void injectPerson1(MainActivity instance, Person person1) {
    instance.person1 = person1;
  }
}
```

### 三、@Module、@Provides

**@Inject** 注解在用于工程中自己建立的类时是可行的，但面对工程中依赖到的各种开源库却无能为力了，因为我们无法修改它们的构造函数，此时就需要用到 **@Module** 与 **@Provides** 注解

假设当前有个 User 类来自于项目中依赖到的开源库中，此时该类的构造函数并没有添加 **@Inject** 注解

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:11
 * 描述：
 */
public class User {

    private String name;

    public User() {
        name = "user default name";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
```

新建 **UserModule** 类用于对外部提供 User 类的实例，**@Provides** 注解用于告诉 Dagger2 ，如果需要 User 类的实例就调用此方法来获取

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:12
 * 描述：
 */
@Module
public class UserModule {

    @Provides
    public User provideUser() {
        return new User();
    }

}
```

此时一样需要一个 Component 类来作为依赖注入的入口，并为 **@Component** 注解提供注解值 **UserModule.class**

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:14
 * 描述：
 */
@Component(modules = {UserModule.class})
public interface UserComponent {

    void inject(Main2Activity mainActivity);

}

```

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @Inject
    User user1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerUserComponent.builder().build().inject(this);
        Log.e(TAG, "user1: " + user1);
        Log.e(TAG, "user1 name : " + user1.getName());
    }

}
```

运行结果如下所示

```java
user1: com.leavesc.dagger2samples.test2.User@420cb218
user1 name : user default name
```

### 四、带有参数的依赖对象

修改 User 类，为之添加一个带有参数的构造函数

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:11
 * 描述：
 */
public class User {

    private String name;

    public User() {
        name = "user default name";
    }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
```

假设我们在 **UserModule** 中要调用的是 User 的有参构造函数，那此时就需要通过 UserModule 的构造函数从外部向它传入字符串参数了

此处也不直接将成员变量 **name** 传给 `provideUser()` 方法，而是新建一个 `provideName()` 方法用于实现依赖注入，这也是为了尽量解耦

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:12
 * 描述：
 */
@Module
public class UserModule {

    private String name;

    public UserModule(String name) {
        this.name = name;
    }

    @Provides
    public String provideName() {
        return name;
    }

    @Provides
    public User provideUser(String name) {
        return new User(name);
    }

}
```

由于之前 UserModule 只有无参构造函数，所以在使用 DaggerUserComponent 进行注入时无需显式传入 UserModule 对象，此时 UserModule 的构造函数需要传入参数了，所以现在只能显示调用 **userModule()** 方法传入 UserModule 对象

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @Inject
    User user1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerUserComponent.builder().userModule(new UserModule("leavesC")).build().inject(this);
        Log.e(TAG, "user1: " + user1);
        Log.e(TAG, "user1 name : " + user1.getName());
    }

}
```

运行结果如下所示

```java
user1: com.leavesc.dagger2samples.test2.User@420c4a30
user1 name : leavesC
```

### 五、@Singleton

假设在 Main2Activity 中有两个 User 对象需要进行实例化，按照以上的使用方式，在依赖注入时是会为每个不同的变量重新 new 一个实例的

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @Inject
    User user1;

    @Inject
    User user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerUserComponent.builder().userModule(new UserModule("leavesC")).build().inject(this);
        Log.e(TAG, "user1: " + user1);
        Log.e(TAG, "user1 name : " + user1.getName());
        Log.e(TAG, "user2: " + user2);
        Log.e(TAG, "user2 name : " + user2.getName());
    }

}
```

运行结果如下所示，可以看到 user1 和 user2 的内存地址并不相同

```java
user1: com.leavesc.dagger2samples.test2.User@420cb780
user1 name : leavesC
user2: com.leavesc.dagger2samples.test2.User@420cba90
user2 name : leavesC
```

而为了实现单例模式，此处需要使用到 **@Singleton** 注解

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:12
 * 描述：
 */
@Module
public class UserModule {

    private String name;

    public UserModule(String name) {
        this.name = name;
    }

    @Provides
    public String provideName() {
        return name;
    }

    @Provides
    public User provideUser(String name) {
        return new User(name);
    }

}
```

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:14
 * 描述：
 */
@Component(modules = {UserModule.class})
public interface UserComponent {

    void inject(Main2Activity mainActivity);

}
```

此处重新运行应用，就可以看到 user1 和 user2 的内存地址是相同的了

```java
user1: com.leavesc.dagger2samples.test2.User@420c86d8
user1 name : leavesC
user2: com.leavesc.dagger2samples.test2.User@420c86d8
user2 name : leavesC
```

### 六、＠Named

由于 User 类有两个构造函数，有时候我们也需要指定要由哪个构造函数来初始化 User，此时就需要用到 **＠Named** 注解

修改 UserModule 类，增加 `provideUser2()` 方法，并为 `provideUser2()` 和 `provideUser2()` 方法声明 **＠Named** 注解，注解值用于配对需要实现依赖注入的成员变量，只要成员变量声明的 **＠Named** 注解的属性值与这两个方法的某个注解值相等，就会依赖该方法来初始化成员变量

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 17:12
 * 描述：
 */
@Module
public class UserModule {

    private String name;

    public UserModule(String name) {
        this.name = name;
    }

    @Provides
    public String provideName() {
        return name;
    }

    @Provides
    @Singleton
    @Named("no empty")
    public User provideUser(String name) {
        return new User(name);
    }

    @Provides
    @Singleton
    @Named("empty")
    public User provideUser2() {
        return new User();
    }

}

```

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @Inject
    @Named("no empty")
    User user1;

    @Inject
    @Named("empty")
    User user2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerUserComponent.builder().userModule(new UserModule("leavesC")).build().inject(this);
        Log.e(TAG, "user1: " + user1);
        Log.e(TAG, "user1 name : " + user1.getName());
        Log.e(TAG, "user2: " + user2);
        Log.e(TAG, "user2 name : " + user2.getName());
        startActivity(new Intent(this, Main3Activity.class));
    }

}
```

运行结果如下所示

```java
user1: com.leavesc.dagger2samples.test2.User@420cd128
user1 name : leavesC
user2: com.leavesc.dagger2samples.test2.User@420cd438
user2 name : user default name
```

### 七、@Qualifier

先看下注解 **@Named** 的声明，该注解就使用到了 **@Qualifier**

```java
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface Named {

    /** The name. */
    String value() default "";
}
```

由于注解 **@Named** 是**通过比较字符串的相等性来实现配对**的，出错的可能性并不算低，而且也不够优雅，此时就可以通过 **@Qualifier** 来自己实现同样的功能

声明两个注解，用来表示在初始化 User 变量时是调用哪个构造函数

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 20:34
 * 描述：
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface UserWithoutParameter {

}
```

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 20:34
 * 描述：
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface UserWithParameter {

}
```

然后直接替换 @Named 即可实现与之相同的功能

```java
    @Provides
    @Singleton
	//@Named("no empty")
    @UserWithParameter
    public User provideUser(String name) {
        return new User(name);
    }

    @Provides
    @Singleton
	//@Named("empty")
    @UserWithoutParameter
    public User provideUser2() {
        return new User();
    }
```

```java
    @Inject
    //@Named("no empty")
    @UserWithParameter
    User user1;

    @Inject
    //@Named("empty")
    @UserWithoutParameter
    User user2;
```

### 八、延迟加载

Dagger2 也支持延迟加载，在需要的时候才对成员变量进行初始化，需要依赖于泛型接口 **Lazy** 

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @Inject
    Lazy<User> user3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerUserComponent.builder().userModule(new UserModule("leavesC")).build().inject(this);
      
        User user = user3.get();
        Log.e(TAG, "user3-1: " + user);
        Log.e(TAG, "user3 name-1 : " + user.getName());
    }

}

```

### 九、强制加载

Dagger2 支持在每次获取成员变量值时都返回一个重新初始化的对象，除非你使用了 **@Singleton** 注解要求只实例化一次

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @Inject
    Provider<User> user4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerUserComponent.builder().userModule(new UserModule("leavesC")).build().inject(this);

        User user = user4.get();
        Log.e(TAG, "user4-1: " + user);
        Log.e(TAG, "user4 name-1 : " + user.getName());
        user = user4.get();
        Log.e(TAG, "user4-2: " + user);
        Log.e(TAG, "user4 name-2 : " + user.getName());
        user = user4.get();
        Log.e(TAG, "user4-2: " + user);
        Log.e(TAG, "user4 name-2 : " + user.getName());
    }

}

```

运行结果如下所示，可以看到 user 变量的内存地址每次各不相同

```java
    user4-1: com.leavesc.dagger2samples.test2.User@420cad48
    user4 name-1 : Hello
    user4-2: com.leavesc.dagger2samples.test2.User@420cb148
    user4 name-2 : Hello
    user4-2: com.leavesc.dagger2samples.test2.User@420cb548
    user4 name-2 : Hello
```

### 十、组件间的依赖

假设现在有个需求，在多个地方中都需要获取系统服务 LocationManager，而获取 LocationManager 是需要通过 Context 来获取的，为了避免需要重复传递 Context 对象，此时就可以选择通过组件间的依赖将 Context 的获取方法移交给另外的 Component

```java
LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
```

首先，通过 **ApplicationModule **和 **ApplicationComponent** 来统一对外提供 Context

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:25
 * 描述：
 */
@Module
public class ApplicationModule {

    private Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

}
```

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:26
 * 描述：
 */
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    Context getContext();

}
```

然后在 Application 类中实现依赖注入，使得对外提供的 Context 对象统一都是 ApplicationContext

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:45
 * 描述：
 */
public class RealApplication extends Application {

    public static ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
    }

}
```

通过注解值 **dependencies** 来指定 ActivityComponent 需要的 Context 要从哪个 Component 中获取

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:33
 * 描述：
 */
@Component(dependencies = {ApplicationComponent.class}, modules = {ActivityModule.class})
public interface ActivityComponent {

    void inject(Main3Activity mainActivity);

}
```

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 19:27
 * 描述：
 */
@Module
public class ActivityModule {

    @Provides
    LocationManager provideLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

}
```

然后，在需要 LocationManager 的地方就可以通过 DaggerActivityComponent 来间接获取，而无需直接依赖于 Context 对象

```java
/**
 * 作者：叶应是叶
 * 时间：2018/7/8 16:35
 * 描述：
 */
public class Main3Activity extends AppCompatActivity {

    private static final String TAG = "Main3Activity";

    @Inject
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DaggerActivityComponent.builder().applicationComponent(RealApplication.applicationComponent).build().inject(this);
        Log.e(TAG, "locationManager: " + locationManager);
    }

}
```

