package com.itheima.sh.config;

import com.itheima.sh.pojo.User;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("all")
public class SqlSession {
    //定义方法，使用动态代理生成传递过来的接口对象
    /*
    使用的时候  UserMapper mapper = sqlSession.getMapper(UserMapper.class);
     */
    //getMapper方法表示生成传递的接口的代理对象
    public <T> T getMapper(Class<T> clazz) {// Class<T> classz = UserMapper.class;
        /*
        使用动态代理技术生成传递的接口的代理对象
        Static Object newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)
               参数：
               1.loader类加载器，负责加载代理类到内存中
               2.interfaces:代理接口即classz===UserMapper.class
               3.h:属于调用处理器，处理代理的方法List<User> queryAllUsers();
         */
        //1.loader类加载器，负责加载代理类到内存中
        ClassLoader classLoader = SqlSession.class.getClassLoader();
        //2.interfaces:代理接口即classz===UserMapper.class
        // Class[] interfaces = { UserMapper.class}
        Class[] interfaces = {clazz};
        T mapperProxy = (T) Proxy.newProxyInstance(classLoader, interfaces, new InvocationHandler() {
            /*
            InvocationHandler属于接口，我们使用匿名内部类实现该接口的方法：
                   invoke(Object proxy, Method method, Object[] args)
                   说明：
                      1.使用代理对象调用一次方法queryAllUsers就会执行一次invoke

             */
            //这里的返回值返回给queryAllUsers方法的调用者即  List<User> list = mapper.queryAllUsers();
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //1.创建核心配置类对象
                Configuration config = new Configuration();
                //2.使用对象config获取数据源即数据库连接池对象
                DataSource ds = config.getDs();
                //3.从数据库连接池中获取连接
                Connection conn = ds.getConnection();
                //4.获取map集合对象
                HashMap<String, Mapper> map = config.getHm();
                //5.获取map集合的key
                //5.1.获取namespace即接口的全名
                String namespace = clazz.getName();
                //System.out.println("namespace = " + namespace); namespace = com.itheima.sh.dao.UserMapper
                //5.2获取id即接口的方法名 method表示调用的方法queryAllUsers
                String id = method.getName();
                //System.out.println("id = " + id); //id=queryAllUsers
                //6根据key获取map集合的value
                String key = namespace + "." + id;
                Mapper mapper = map.get(key);
                //7.获取Mapper类的属性sql
                String sql = mapper.getSql();
                //8.获取结果集resultType
                String resultType = mapper.getResultType();
                //resultType= "com.itheima.sh.pojo.User"
                //Class<?> aClass = Class.forName(resultType);


                //调用方法获取数据
                List list = queryForList(conn,sql,resultType);
                return list;
            }
        });
        //这里的代理对象返回给getMapper的调用者  UserMapper mapper = sqlSession.getMapper(UserMapper.class);//mapperProxy
        return mapperProxy;
    }

    public List queryForList(Connection conn, String sql, String resultType) throws Exception {
        //1.创建集合保存所有的User对象
        List users = new ArrayList();
        //2.使用连接获取预编译对象
        PreparedStatement pst = conn.prepareStatement(sql);
        //3,使用预编译对象pst调用方法执行sql语句
        ResultSet rs = pst.executeQuery();
        //4.将传递的类resultType变为Class类型
        Class clazz = Class.forName(resultType);
        //5.处理结果集，处理每一行数据
        while (rs.next()) {
            //6.使用反射技术创建resultType表示的类的对象，即User类对象
            Object obj = clazz.newInstance();//调用User类的无参
            //7。获取User实体类的所有属性
            Field[] fields = clazz.getDeclaredFields();
            //8.遍历数组中的每个属性,并取出数据表当前行的每列数据，封装到实体类对象obj中
            for (Field field : fields) {//field表示User实体类中的每个属性:id，username,birthday,sex,address
                //使用暴力反射
                field.setAccessible(true);
                //9.获取属性名
                String fieldName = field.getName();//fieldName=id，username,birthday,sex,address
                //10.根据获取的属性名结合结果集rs中的getObject方法到数据表中获取数据
                Object table_value = rs.getObject(fieldName);
                //11.将取出的每个数据封装到实体类对象obj中
                field.set(obj,table_value);
            }
            //12.将封装好的实体类对象放到集合users中
            users.add(obj);
        }
        //返回集合
        return users;
    }
}

