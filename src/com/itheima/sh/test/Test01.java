package com.itheima.sh.test;
//复习dom4j和xpath
//1.Node是Element的父接口
import com.itheima.sh.config.Configuration;
import com.itheima.sh.config.SqlSession;
import com.itheima.sh.dao.UserMapper;
import com.itheima.sh.pojo.User;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class Test01 {
    @Test
    public void test01() throws Exception {
        //创建核心配置类的对象
        Configuration config = new Configuration();
        System.out.println("config = " + config);
    }

    @Test
    public void test02() throws Exception {
        //使用dom4j技术解析核心配置文件
       //1.获取document对象
        SAXReader reader = new SAXReader();
        InputStream is = Test01.class.getClassLoader().getResourceAsStream("mybatis-config.xml");
        Document document = reader.read(is);

        //2.获取根标签
        Element rootElement = document.getRootElement();
        System.out.println("rootElement = " + rootElement);

        //3.获取property标签
        List<Element> list = document.selectNodes("//property"); //   //properties即xpath
        //List<Node> list = document.selectNodes("//property"); //   //properties即xpath

        //4.遍历list
//        for (Node node : list) {
//            //强制转换 Node是Element父接口
//            //Element e = (Element)node;
//            //4.获取name属性值
//
//        }

        //4.遍历list
        for (Element element : list) {
            //4.获取name属性值
            String name_value = element.attributeValue("name");
            //5.获取value属性值
            String value = element.attributeValue("value");
            System.out.println("name_value="+name_value+",value = " + value);
        }

        //获取username标签
        Element e2 = (Element)document.selectSingleNode("//username");

        //7.获取username标签的文本值
        System.out.println(e2.getText()); //没有去掉前后空格
        System.out.println(e2.getTextTrim()); //去掉前后空格


    }


    @Test
    public void test03() {
        //1.创建SqlSession类的对象
        SqlSession sqlSession = new SqlSession();
        //2.使用会话对象调用方法获取传递的接口UserMapper的代理对象
        //mapper是userMapper接口的代理对象
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);//mapperProxy
        //3.使用代理对象调用查询方法
        List<User> list = mapper.queryAllUsers();
        System.out.println("list = " + list);
    }
}
