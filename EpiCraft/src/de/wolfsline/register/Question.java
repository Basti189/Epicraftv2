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
		sql.queryUpdate("CREATE TABLE IF NOT EXISTS Fragebogen (UUID VARCHAR(36), Frage1 SMALLINT, Frage2 SMALLINT, Frage3 SMALLINT, Frage4 SMALLINT, Frage5 SMALLINT, Frage6 SMALLINT, Frage7 SMALLINT, Frage8 SMALLINT, Frage9 SMALLINT, Frage10 SMALLINT, Startzeit VARCHAR(10), Startdatum VARCHAR(10), Endzeit VARCHAR(10), Enddatum VARCHAR(10) )");
	}
	
	public void start(Player p){
		if(isPlayerinDatabase(p)){
			return;
		}
		MySQL sql = this.plugin.getMySQL();
		String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
		String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
		String update = "INSERT INTO Fragebogen (UUID, Frage1, Frage2, Frage3, Frage4, Frage5, Frage6, Frage7, Frage8, Frage9, Frage10, Startzeit, Startdatum) VALUES ('" + p.getUniqueId() + "', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '" + time + "', '" + date + "')"; 
		sql.queryUpdate(update);
	}
	
	public void update(Player p, int q, boolean state){
		MySQL sql = this.plugin.getMySQL();
		int wert = 0;
		if(state)
			wert = 1;
		String update = "UPDATE Fragebogen SET Frage" + String.valueOf(q) + "='" + String.valueOf(wert) + "' WHERE UUID='" + p.getUniqueId() + "'";
		sql.queryUpdate(update);
	}
	
	public boolean questionAllRight(Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM Fragebogen WHERE UUID='" + p.getUniqueId() + "'");
			rs = st.executeQuery();
			rs.next();
			for(int i = 2 ; i <= 11 ; i++){
				if(rs.getInt(i) == 0){
					sql.closeRessources(rs, st);
					return false;
				}
			}
			sql.closeRessources(rs, st);
			String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
			String date = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
			String update = "UPDATE Fragebogen SET Endzeit='" + time + "', Enddatum='" + date + "' WHERE UUID='" + p.getUniqueId() + "'";
			sql.queryUpdate(update);
			return true;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean isPlayerinDatabase(Player p){
		MySQL sql = this.plugin.getMySQL();
		Connection conn = sql.getConnection();
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT UUID FROM Fragebogen WHERE UUID='" + p.getUniqueId() + "'");
			rs = st.executeQuery();
			if(rs.next()){
				sql.closeRessources(rs, st);
				return true;
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
