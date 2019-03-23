import java.sql.*;
import java.util.*;
public class user_data extends data_base {
	//获得用户列表（全部）
	public List<person_info> getUser(){
        List<person_info>userList = new ArrayList<person_info>();
        person_info user = new person_info();
        String sql = "SELECT * FROM user_table";
        try{
            ResultSet rs = this.executeQuery(sql,null);
            while(rs.next()){
                user.setname(rs.getString("name"));
                user.setpassword(rs.getString("password"));
                user.setgender(rs.getInt("gender"));
                user.setsignature(rs.getString("signature"));
                user.setpic(rs.getInt("pic"));
                user.setfriends_num(rs.getInt("friends_num"));
                user.setstrfriends_name(rs.getString("friends_name"));
                user.setboxscore(rs.getInt("boxscore"));
                user.setthunderscore(rs.getInt("thunderscore"));
                user.settouchscore(rs.getInt("touchscore"));
                user.setddtankscore(rs.getInt("ddtankscore"));
                userList.add(user);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return userList;
    }
	
	//获得单个用户
	public person_info getUserByName(String name){
        person_info user = null;
        String sql = "SELECT * FROM user_table WHERE name = ?";
        try{            
            ResultSet rs = this.executeQuery(sql, new String[]{name});
            if(rs.next()){
                user = new person_info();
                user.setname(rs.getString("name"));
                user.setpassword(rs.getString("password"));
                user.setgender(rs.getInt("gender"));
                user.setsignature(rs.getString("signature"));
                user.setpic(rs.getInt("pic"));
                user.setfriends_num(rs.getInt("friends_num"));
                user.setstrfriends_name(rs.getString("friends_name"));
                user.setboxscore(rs.getInt("boxscore"));
                user.setthunderscore(rs.getInt("thunderscore"));
                user.settouchscore(rs.getInt("touchscore"));
                user.setddtankscore(rs.getInt("ddtankscore"));
            }
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return user;
    }
	
	//添加用户
    public boolean addUser(person_info user){
        boolean r = false;
        String sql = "INSERT INTO user_table(name,password,gender,pic,friends_num,signature,friends_name,boxscore,thunderscore,touchscore,ddtankscore)VALUES(?,?,?,?,?,?,?,?,?,?,?) ";
        try{
            int num = this.executeUpdate(sql,new String[]{user.getname(),user.getpassword(),""+user.getgender(),""+user.getpic(),""+user.getfriends_num(),user.getsignature(),user.getstrfriends_name(),""+user.getboxscore(),""+user.getthunderscore(),""+user.gettouchscore(),""+user.getddtankscore()});
            if(num > 0){
                r = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return r;
    }
    
  //修改用户信息
    public boolean editUser(person_info user){
        boolean r = false;
        String sql = "UPDATE user_table SET password = ?,gender = ?,pic = ?,friends_num = ?,signature = ?,friends_name=?,boxscore=?,thunderscore=?,touchscore=?,ddtankscore=? WHERE name = ?";
        
        try{
            int num = this.executeUpdate(sql, new String[]{user.getpassword(),""+user.getgender(),""+user.getpic(),""+user.getfriends_num(),user.getsignature(),user.getstrfriends_name(),""+user.getboxscore(),""+user.getthunderscore(),""+user.gettouchscore(),""+user.getddtankscore(),user.getname()});
            if(num > 0){
                r = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return r;
    }
    
  //删除指定用户
    public boolean delUser(String name){
        boolean r = false;
        String sql = "DELETE FROM user_table WHERE name = ?";
        try{
            int num = this.executeUpdate(sql,new String[]{name});
            if(num > 0){
                r = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            this.closeAll();
        }
        return r;
    }
}
