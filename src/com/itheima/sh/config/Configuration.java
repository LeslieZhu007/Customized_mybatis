package com.itheima.sh.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/*
   核心配置文件类---------------和mybatis-config.xml对应
 */
public class Configuration {
    //1.定义属性保存核心配置文件中的四大连接参数
    private String driver;//驱动
    private String url;//地址
    private String username;//用户名
    private String password;//密码

    //2.定义属性保存保存数据库连接池
    private DataSource ds;

    //3.定义属性保存多个Mapper类的对象，还得需要一个键即key是唯一的
    //需要找对应的Mapper
    //key属于String类型，有namespace+id组成 --namespace只有一个，但是id可以有多个，这样可以形成唯一组合
    //value属于Mapper类型
    HashMap<String,Mapper> hm = new HashMap<>();

    public Configuration() throws Exception {
        try {
            //1.创建configuration对象时给4大参数赋值
            //1.1 创建document对象关联核心配置文件
            SAXReader reader = new SAXReader();
            InputStream is = Configuration.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
            Document doc = reader.read(is);
            //1.2调用自定义方法将核心配置文件中的数据封装到Configuration类的四大参数属性中
            loadMybatisConfigXml(doc);

            //调用自己定义方法初始化数据库连接池对象
            createDataSource();
            //调用自己定义方法加载映射文件
            loadMybatisMapperXml(doc);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
    //加载映射文件
    private void loadMybatisMapperXml(Document doc) throws Exception {//doc关联的是核心类
        //1.获取核心配置文件中的mapper标签
        List<Element> list = doc.selectNodes("//mapper");
        //2.遍历集合取出每个mapper
        for (Element element : list) {
            //获取resource属性值
            String mapperXml = element.attributeValue("resource");
            //根据mapperXml创建document对象
            SAXReader reader = new SAXReader();
            //doc2关联映射文件
            Document doc2 = reader.read(Configuration.class.getClassLoader().getResourceAsStream(mapperXml));
            //6.根据doc2获取映射文件中的mapper标签
            Element rootElementMapper = doc2.getRootElement();
            //7.获取namespace
            String namespace = rootElementMapper.attributeValue("namespace");
            //8.获取id
            //8.1获取mapper标签的子标签select
            List<Element> selectList = rootElementMapper.elements("select");
            //8.2遍历集合
            for (Element select : selectList) {
                //获取id
                String id = select.attributeValue("id");
                //获取resultType
                String resultType = select.attributeValue("resultType");
                //获取sql语句
                String sql = select.getTextTrim();
                //创建Mapper对象
                Mapper mapper = new Mapper();
                //封装数据
                mapper.setId(id);
                mapper.setNamespace(namespace);
                mapper.setResultType(resultType);
                mapper.setSql(sql);
                //将mapper对象存储到map集合中
                hm.put(namespace+"."+id, mapper);
            }
        }

    }

    //初始化数据库连接池对象
    private void createDataSource() throws Exception {
        //1.创建c3p0核心类对象
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        //2.使用对象调用方法将四大连接参数给数据库连接池
        cpds.setDriverClass(driver);
        cpds.setJdbcUrl(url);
        cpds.setUser(username);
        cpds.setPassword(password);
        //3.将cpds赋值给成员变量ds
        this.ds=cpds;

    }

    //将核心配置文件中的数据封装到Configuration类的四大参数属性中
    private void loadMybatisConfigXml(Document doc) {
        //使用document对象调用方法获取property标签  <property name="driver" value="com.mysql.jdbc.Driver"/>
        List<Element> list = doc.selectNodes("//property");
        for (Element element : list) {
            //获取property标签的name属性值
            String name2 = element.attributeValue("name");
            //获取property标签的value属性值
            String value2 = element.attributeValue("value");

            //将value2的值赋值给成员变量
            switch (name2) {
                case "driver":
                    this.driver=value2;
                    break;
                case "url":
                    this.url=value2;
                    break;
                case "username":
                    this.username=value2;
                    break;
                case "password":
                    this.password=value2;
                    break;
            }

        }
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public DataSource getDs() {
        return ds;
    }

    public void setDs(DataSource ds) {
        this.ds = ds;
    }

    public HashMap<String, Mapper> getHm() {
        return hm;
    }

    public void setHm(HashMap<String, Mapper> hm) {
        this.hm = hm;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", ds=" + ds +
                ", hm=" + hm +
                '}';
    }
}
