import java.sql.*;
import java.util.Scanner;

public class loan_system {
    public static Connection connection;

    static {
        try {
            connection = DBcon();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static Statement statement;

    static {
        try {
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String choice_one;
        String choice_two;
        String choice_three;
        String choice_four;
        Customers customers = null;
        Credit credit=null;
        Finance finance=null;
        int creditSize;
        float applymoney=0;
//        int wayPay=1;
        System.out.println("欢迎来到立信银行，请问老子有啥能帮你这个瘪三的？");
        while (true){
            System.out.println("a.登录\nb.创建账户");
            Scanner sc=new Scanner(System.in);
            choice_one=sc.next();
            if (choice_one.equals("a")||choice_one.equals("b")){
                break;
            }
            else {
                System.out.println("输入有误，请再试一次！");
            }
        }
        if (choice_one.equals("a")){
            System.out.println("请输入您的用户名：");
            String accountID;
            while (true){
                Scanner sc=new Scanner(System.in);
                accountID=sc.next();
                if (queryID(accountID)){
                    break;
                }
                else {
                    System.out.println("您输入的用户名错误，请再试一次");
                }
            }
            try {
                customers=new Customers(accountID,connection,statement);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            Scanner sc=new Scanner(System.in);
            System.out.println("请输入您的姓名：");
            String name=sc.next();
            System.out.println("请输入您的电话号码");
            String phone=sc.next();
            customers=new Customers(name,phone,connection,statement);
        }
        assert customers != null;
        String accountID=customers.getAccountID();

        while (true){
            System.out.println("请选择您要办理的业务：\na.申请(查询)贷款\nb.还款\nc.删除账户");
            Scanner sc=new Scanner(System.in);
            choice_two=sc.next();
            if (choice_two.equals("a")||choice_two.equals("b")||choice_two.equals("c")){
                break;
            }
            else {
                System.out.println("选择错误，请重新选择！");
            }
        }
        if (choice_two.equals("a")){
            try {
                credit=new Credit(accountID,connection,statement);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            assert credit != null;
            creditSize=credit.getCredit();
            System.out.println("您的信用额度为(可申请贷款金额)："+creditSize);
            if (choice_one.equals("b")){
                while (true){
                    System.out.println("请输入您要申请的金额：");
                    Scanner sc=new Scanner(System.in);
                    try {
                        applymoney=sc.nextFloat();
                    }
                    catch (Exception e){
                        System.out.println("请输入正确金额");
                    }
                    if (applymoney<=creditSize){
                        System.out.println("您申请的贷款额度为："+applymoney);
                        break;
                    }
                    else {
                        System.out.println("您申请的贷款金额不能大于信用额度，请重新输入");
                    }
                }
                while (true) {
                    System.out.println("请输入还款方式：\na.分3期\nb.分6期\nc.分12期");
                    Scanner sc=new Scanner(System.in);
                    choice_three=sc.next();
                    if (choice_three.equals("a") || choice_three.equals("b") || choice_three.equals("c")) {
                        break;
                    }
                    else {
                        System.out.println("请重新选择！");
                    }
                }
                try {
                    if (choice_three.equals("a")){
                        finance=new Finance(accountID,applymoney,3,connection,statement);
                    }
                    else if (choice_three.equals("b")){
                        finance=new Finance(accountID,applymoney,6,connection,statement);
                    }
                    else {
                        finance=new Finance(accountID,applymoney,12,connection,statement);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("贷款申请已成功");
            }
            else {
                try {
                    finance = new Finance(accountID, connection, statement);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            assert finance != null;
            finance.showInfo();
        }
        if (choice_two.equals("b")){
            System.out.println("您是否要选择还款？(y/n)");
            try {
                finance = new Finance(accountID, connection, statement);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            while (true){
                Scanner sc=new Scanner(System.in);
                choice_four=sc.next();
                if (choice_four.equals("y")||choice_four.equals("n")){
                    break;
                }
                else {
                    System.out.println("请重新选择");
                }
            }
            if(choice_four.equals("y")){
                assert finance != null;
                finance.payBack();
                finance.showInfo();
            }
            else {
                System.out.println("欢迎下次光临！");
            }
        }
        else if (choice_two.equals("c")){
            System.out.println("请输入您的ID以授权删除账户");
            while (true){
                Scanner sc=new Scanner(System.in);
                String auth=sc.next();
                if (auth.equals(accountID)){
                    break;
                }
                else {
                    System.out.println("ID错误，请重新输入！");
                }
            }
            customers.deleteCustomer();
        }

    }

    private static Connection DBcon () throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/loan_system?serverTimezone=Asia/Shanghai";
        String user = "root";
        String password = "qian258046";
        Connection connection = DriverManager.getConnection(url, user, password);
        if (!connection.isClosed())
            System.out.println("连接数据库成功");
        return connection;
    }

    public static boolean queryID(String id)  {
        String sql_info="select accountID from customer where accountID='"+id+"'";
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(sql_info);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        while (true){
            try {
                assert rs != null;
                if (!rs.next()) break;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
