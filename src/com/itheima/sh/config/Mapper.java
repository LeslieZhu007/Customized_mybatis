package com.itheima.sh.config;
/*
映射文件类----和UserMapper.xml文件对应
 */
public class Mapper {
    //namespace,id,resultType,sql语句
    //属性
    private String namespace; //名称空间，存放接口的全路径
    private String id;//select标签的id，存放接口中的方法名
    private String resultType;//结果集，接收sql语句返回的结果，存放类的全路径
    private String sql;//存储sql语句

    //只需无参构造

    public Mapper() {
    }

    public Mapper(String namespace, String id, String resultType, String sql) {
        this.namespace = namespace;
        this.id = id;
        this.resultType = resultType;
        this.sql = sql;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return "Mapper{" +
                "namespace='" + namespace + '\'' +
                ", id='" + id + '\'' +
                ", resultType='" + resultType + '\'' +
                ", sql='" + sql + '\'' +
                '}';
    }
}
