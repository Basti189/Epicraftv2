package de.wolfsline.register;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.entity.Player;

import de.wolfsline.Epicraft.Epicraft;
import de.wolfsline.data.MySQL;

public class Question {
	private Epicraft plugin;

	public Question(Epicraft plugin) {
		this.plugin = plugin;
		MySQL sql = this.plugin.getMySQL();
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS questionnaire (id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(16), question1 SMALLINT, question2 SMALLINT, question3 SMALLINT, question4 SMALLINT, question5 SMALLINT, question6 SMALLINT, question7 SMALLINT, question8 SMALLINT, question9 SMALLINT, question10 SMALLINT, starttime VARCHAR(10), startdate VARCHAR(10), endtime VARCHAR(10), enddate VARCHAR(10) )");
	}
	
	public void start(Player p){
		if(isPlayerinDatabase(p)){
			return;
		}
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO questionnaire (username, question1, question2, question3, question4, question5, question6, question7, question8, question9, question10, starttime, startdate) VALUES ('" + p.getName() + "', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '" + time + "', '" + date + "')"; 
		sql.queryUpdate(update);
	}
	
	public void update(Player p, int q, boolean state){
		MySQL sql = this.plugin.getMySQL();
		int wert = 0;
		if(state)
			wert = 1;
		String update = "UPDATE questionnaire SET question" + String.valueOf(q) + "='" + String.valueOf(wert) + "' WHERE username='" + p.getName() + "'";
		sql.queryUpdate(update);
	}
	
	public boolean questionAllRight(Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM questionnaire WHERE username='" + p.getName() + "'");
			rs = st.executeQuery();
			rs.next();
			for(int i = 3 ; i <= 12 ; i++){
				if(rs.getInt(i) == 0){
					return false;
				}
			}
			sql.closeRessources(rs, st);
			String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
			String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
			String update = "UPDATE questionnaire SET endtime='" + time + "', enddate='" + date + "' WHERE username='" + p.getName() + "'";
			sql.queryUpdate(update);
			return true;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isPlayerinDatabase(Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT username FROM questionnaire WHERE username='" + p.getName() + "'");
			rs = st.executeQuery();
			while(rs.next()){
				if(rs.getString(1).equalsIgnoreCase(p.getName())){
					sql.closeRessources(rs, st);
					return true;
				}
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			sql.closeRessources(rs, st);
		}
		return false;
	}
}
