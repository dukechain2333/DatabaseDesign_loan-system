import java.sql.*;

public class Finance {
    private String accountID;
    private int WayPay;
    private float money;
    private int credit;
    private float times_left;
    private final Connection connection;
    private final Statement statement;

    public Finance(String ID,Connection connection,Statement statement) throws SQLException {
        this.connection=connection;
        this.statement=statement;
        if (queryID(ID)){
            String sql_finance="select * from finance where accountID='"+ID+"';";
            ResultSet rs = statement.executeQuery(sql_finance);
            while (rs.next()){
                this.accountID=rs.getString(1);
                this.money=rs.getInt(2);
                this.WayPay=rs.getInt(3);
                this.times_left=rs.getInt(4);
            }
            this.credit=getYourCredit();
            rs.close();
        }
        else {
            System.out.println("请先办理业务");
        }
    }

    public Finance(String ID,float money,int WayPay, Connection connection,Statement statement) throws SQLException {
        this.accountID=ID;
        this.connection=connection;
        this.statement=statement;
        this.money=generateMoney(money);
        this.WayPay=generateWayPay(WayPay);
        this.times_left=WayPay;
        this.credit=getYourCredit();
        writeInfo();
    }

    public void showInfo(){
        System.out.println("您的信用额度："+credit);
        System.out.println("您还欠老子："+money);
        System.out.println("您还剩下的还钱机会:"+times_left);
    }

    private boolean queryID(String id) throws SQLException {
        String sql_info="select accountID from finance where accountID='"+id+"'";
        ResultSet rs = statement.executeQuery(sql_info);
        while (rs.next()){
            return true;
        }
        return false;
    }

    private void writeInfo()  {
        String sql_info="insert into finance (accountID, money,WayPay,times_left) VALUES (?,?,?,?);";
        try {
            PreparedStatement ps=connection.prepareStatement(sql_info);
            ps.setString(1,accountID);
            ps.setFloat(2,money);
            ps.setInt(3,WayPay);
            ps.setFloat(4,WayPay);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e){
//            System.out.println("您未开户，请先开户!");
            e.printStackTrace();
        }
    }


    private int getYourCredit(){
        int credit=0;
        String sql_credit="select credit from credit where accountID='"+accountID+"'";
        try {
            ResultSet rs=statement.executeQuery(sql_credit);
            while (rs.next()){
                credit=rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return credit;
    }

    private float generateMoney(float money){
        return money;
    }

    private int generateWayPay(int choice){
        if (choice==3){
            return 3;
        }
        else if (choice==6){
            return 6;
        }
        else if (choice==12){
            return 12;
        }
        return 0;
    }

    public void payBack() {
        if (money == 0) {
            System.out.println("您的欠款已还清！");
        } else {
            float payMoney = money / times_left;
            float moneyTopay = money - payMoney;
            times_left--;
            String sql_pay = "update finance set money=" + moneyTopay + " where accountID='" + accountID + "';";
            String sql_pay2 = "update finance set times_left=" + times_left + " where accountID='" + accountID + "';";
            this.money = moneyTopay;
            try {
                PreparedStatement ps = connection.prepareStatement(sql_pay);
                ps.execute(sql_pay);
                ps.executeUpdate();
                ps = connection.prepareStatement(sql_pay2);
                ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
