import java.sql.*;
import java.util.Random;

public class Customers {

    private String name;
    private String phone;
    private String accountID;
    private final Connection connection;
    private final Statement statement;

    public Customers(String accountID, Connection connection, Statement statement) throws SQLException {
        this.connection=connection;
        this.statement=statement;
        if(queryID(accountID)){
            String sql_customer="select * from customer where accountID='"+ accountID +"';";
            ResultSet rs = statement.executeQuery(sql_customer);
            while (rs.next()){
                System.out.println("查询用户成功");
                this.accountID =rs.getString(1);
                this.name=rs.getString(2);
                this.phone=rs.getString(3);
            }
            rs.close();
            sayHello();
        }
    }

    public Customers(String name,String phone,Connection connection, Statement statement) {
        this.connection=connection;
        this.statement=statement;
        createCustomer(name,phone);
    }

    public void createCustomer(String name, String phone){
        this.accountID =generateID();
        setName(name);
        setPhone(phone);
        try {
            writeInfo();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        welcomeNew();
    }

    private void setName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }

    private void setPhone(String phone){
        this.phone=phone;
    }
    public String getPhone(){
        return phone;
    }

    public String getAccountID(){
        return accountID;
    }

    public void deleteCustomer(){
        String sql_delete_finance="delete from finance where accountID='"+accountID+"';";
        String sql_delete_credit="delete from credit where accountID='"+accountID+"';";
        String sql_delete_customer="delete from customer where accountID='"+accountID+"';";
        PreparedStatement ps;
        try {
            ps=connection.prepareStatement(sql_delete_finance);
            ps.execute(sql_delete_finance);
            ps=connection.prepareStatement(sql_delete_credit);
            ps.execute(sql_delete_credit);
            ps=connection.prepareStatement(sql_delete_customer);
            ps.execute(sql_delete_customer);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("您的账户已删除！");
    }

    //将客户数据写入数据库
    private void writeInfo() throws SQLException {
        String sql_info="insert into customer (accountID, name, phone) VALUES (?,?,?);";
        PreparedStatement ps=connection.prepareStatement(sql_info);
        try {
            ps.setString(1, accountID);
            ps.setString(2,name);
            ps.setString(3,phone);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //欢迎老用户
    private void sayHello(){
        System.out.println("登录成功！");
        System.out.println("你好，"+name);
    }

    //欢迎新用户
    private void welcomeNew(){
        System.out.println("新用户已注册");
        System.out.println("你好，"+name);
        System.out.println("您的用户名为："+getAccountID());
    }

    //查询ID是否存在
    private boolean queryID(String id) throws SQLException {
        String sql_info="select accountID from customer where accountID='"+id+"'";
        ResultSet rs = statement.executeQuery(sql_info);
        while (rs.next()){
            return true;
            }
        return false;
    }

    //自动生成新ID
    private String generateID(){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<10;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
