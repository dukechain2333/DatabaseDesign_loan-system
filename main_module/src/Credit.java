import java.sql.*;

public class Credit {

    private int credit;
    private String accountID;
    private final Connection connection;
    private final Statement statement;

    public Credit(String ID,Connection connection,Statement statement) throws SQLException {
        this.connection=connection;
        this.statement=statement;
        if (queryID(ID)){
            String sql_credit="select * from credit where accountID='"+ID+"';";
            ResultSet rs = statement.executeQuery(sql_credit);
            while (rs.next()){
                this.accountID=rs.getString(1);
                this.credit=rs.getInt(2);
            }
            rs.close();
        }
        else {
            this.accountID=ID;
            credit=generateCredit();
            writeInfo();
        }
    }

    private void writeInfo()  {
        String sql_info="insert into credit (accountID, credit) VALUES (?,?);";
        try {
            PreparedStatement ps=connection.prepareStatement(sql_info);
            ps.setString(1,accountID);
            ps.setInt(2,credit);
            ps.executeUpdate();
            ps.close();
        } catch (SQLIntegrityConstraintViolationException e){
            System.out.println("您未开户，请先开户!");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getCredit(){
        return credit;
    }

    //生成一个10000到99999999的额度
    private int generateCredit(){
        System.out.println("正在评估您的信用额度，请稍等");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (int)(Math.random()*(99999999-10000+1)+99999999);
    }

    private boolean queryID(String id) throws SQLException {
        String sql_info="select accountID from credit where accountID='"+id+"'";
        ResultSet rs = statement.executeQuery(sql_info);
        while (rs.next()){
            return true;
        }
        return false;
    }

}
