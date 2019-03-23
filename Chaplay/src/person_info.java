import java.util.*;

enum Gender {male, female};

//将会从数据库中读取信息
public class person_info{
	private String name;
	private String password;
	private int gender=0;
	private String signature;
	private int pic = 0;//0,1,2代表头像
	private int friends_num = 0;//好友的数量
	private List<String> friends_name = new ArrayList<String>();
	private int boxscore=0;
	private int thunderscore=0;
	private int touchscore=0;
	private int ddtankscore=0;
	
	person_info(){}
	person_info(String name, int gender, String signature, int pic){
		setname(name);
		setgender(gender);
		setsignature(signature);
		setpic(pic);
	}
	
	public String getname() {
		return name;
	}
	
	public void setname(String name) {
		this.name = name;
	}
	
	public String getpassword() {
		return password;
	}
	
	public void setpassword(String password) {
		this.password=password;
	}
	
	public int getgender() {
		return gender;
	}
	
	public void setgender(int gender) {
		this.gender = gender;
	}
	
	public String getsignature() {
		return signature;
	}
	
	public void setsignature(String signature) {
		this.signature = signature;
	}
	
	public int getpic() {
		return pic;
	}
	
	public void setpic(int pic) {
		this.pic = pic;
	}
	
	public int getfriends_num() {
		return friends_num;
	}
	
	public void setfriends_num(int friends_num) {
		this.friends_num=friends_num;
	}
	
	public String getfriends_name(int index)
	{
		return friends_name.get(index);
	}
	
	public List<String> getfriends_nameall()
	{
		return this.friends_name;
	}
	
	public String getstrfriends_name()//数据库用
	{
		String[] names=friends_name.toArray(new String[friends_name.size()]);
		String str = (friends_num>0)?names[0]:null;
		for(int i=1;i<friends_num;i++)
			str+= ","+names[i];
		return str;
	}
	
	public void setstrfriends_name(String str)//数据库用
	{
		if(str==null)
			return;
		friends_name = new ArrayList<String>(Arrays.asList(str.split(",")));
	}
	
	public void addfriends_name(String name)
	{
		friends_name.add(name);
		setfriends_num(friends_num+1);
	}
	
	public void deletefriends_name(String name)
	{
		friends_name.remove(name);
		setfriends_num(friends_num-1);
	}
	
	public int getboxscore()
	{
		return boxscore;
	}
	
	public void setboxscore(int boxscore)
	{
		this.boxscore=boxscore;
	}
	
	public int getthunderscore()
	{
		return thunderscore;
	}
	
	public void setthunderscore(int thunderscore)
	{
		this.thunderscore=thunderscore;
	}
	
	public int gettouchscore()
	{
		return touchscore;
	}
	
	public void settouchscore(int touchscore)
	{
		this.touchscore=touchscore;
	}
	
	public int getddtankscore()
	{
		return ddtankscore;
	}
	
	public void setddtankscore(int ddtankscore)
	{
		this.ddtankscore=ddtankscore;
	}
}