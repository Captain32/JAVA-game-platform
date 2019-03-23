import java.sql.*;
public class data_base {
	Connection conn=null;
	PreparedStatement ps=null;
	ResultSet rs=null;
	
	//打开连接
	public Connection getConn() {
		String DRIVER="com.mysql.cj.jdbc.Driver";
		String URL="jdbc:mysql://localhost:3306/javaproject?user=root"
				+ "&password=java2018&useUnicode=true&characterEncoding=utf8&serverTimezone=GMT";
		try {
			Class.forName(DRIVER);
			conn=DriverManager.getConnection(URL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	//关闭连接
	public void closeAll()
	{
		try{
            if(rs!= null){
                rs.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            try{
                if(ps!= null){
                    ps.close();
                }
            }catch(SQLException e){
                e.printStackTrace();
            }finally{
            try{
                if(conn!= null){
                    conn.close();
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
            }
        }
	}
	
	//查询
	public ResultSet executeQuery(String prepareSql,String []param) {
		try {
			ps=conn.prepareStatement(prepareSql);
			if(param!=null) {
				for(int i=0;i<param.length;i++)
					ps.setString(i+1,param[i]);
			}
			rs=ps.executeQuery();
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	//修改
	public int executeUpdate(String preparedSql,String[]param){
        int num = 0;
        try{
            ps = conn.prepareStatement(preparedSql);
            if(ps != null){
                for (int i = 0; i < param.length; i++) {
                    ps.setString(i + 1, param[i]);
                }
            }
            num = ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return num;
    }
}
