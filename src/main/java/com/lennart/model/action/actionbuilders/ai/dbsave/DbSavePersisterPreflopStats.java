package com.lennart.model.action.actionbuilders.ai.dbsave;

import com.lennart.model.action.actionbuilders.ai.ContinuousTable;

import java.sql.*;
import java.util.List;

/**
 * Created by LennartMac on 26/05/2019.
 */
public class DbSavePersisterPreflopStats {

    private Connection con;

    public void doDbSaveUpdate(ContinuousTable continuousTable) throws Exception {
        List<DbSave> dbSaveList = continuousTable.getDbSaveList();

        initializeDbConnection();

        double totalPre2betCountLastHand = 0;
        double totalPre3betCountLastHand = 0;
        double totalPre4bet_up_countLastHand = 0;
        double totalTotalCountLastHand = 0;

        double totalPreCall2betCountLastHand = 0;
        double totalPreCall3betCountLastHand = 0;
        double totalPreCall4bet_up_countLastHand = 0;
        double totalCallTotalCountLastHand = 0;

        String opponentName = null;

        for(DbSave dbSave : dbSaveList) {
            if(dbSave instanceof DbSavePreflopStats) {
                DbSavePreflopStats dbSavePreflopStats = (DbSavePreflopStats) dbSave;

                totalPre2betCountLastHand = totalPre2betCountLastHand + dbSavePreflopStats.getOppPre2betCount();
                totalPre3betCountLastHand = totalPre3betCountLastHand + dbSavePreflopStats.getOppPre3betCount();
                totalPre4bet_up_countLastHand = totalPre4bet_up_countLastHand + dbSavePreflopStats.getOppPre4bet_up_count();
                totalTotalCountLastHand = totalTotalCountLastHand + dbSavePreflopStats.getOppPreTotalCount();

                totalPreCall2betCountLastHand = totalPreCall2betCountLastHand + dbSavePreflopStats.getOppPreCall2betCount();
                totalPreCall3betCountLastHand = totalPreCall3betCountLastHand + dbSavePreflopStats.getOppPreCall3betCount();
                totalPreCall4bet_up_countLastHand = totalPreCall4bet_up_countLastHand + dbSavePreflopStats.getOppPreCall4bet_up_count();
                totalCallTotalCountLastHand = totalCallTotalCountLastHand + dbSavePreflopStats.getOppPreCallTotalCount();

                System.out.println("GGA, lasthand: " + totalTotalCountLastHand);

                if(opponentName == null) {
                    opponentName = dbSavePreflopStats.getOpponentName();
                }
            }
        }

        if(totalTotalCountLastHand > 0 || totalPre2betCountLastHand > 0 || totalPre3betCountLastHand > 0 || totalPre4bet_up_countLastHand > 0
                || totalCallTotalCountLastHand > 0 || totalPreCall2betCountLastHand > 0
                || totalPreCall3betCountLastHand > 0 || totalPreCall4bet_up_countLastHand > 0) {
            addOpponentToDbIfNecessary(opponentName);

            Statement st = con.createStatement();

            st.executeUpdate(
                    "UPDATE opponentidentifier_2_0_preflopstats SET pre2bet = pre2bet + " + totalPre2betCountLastHand +
                            ", pre3bet = pre3bet + " + totalPre3betCountLastHand +
                            ", pre4bet_up = pre4bet_up + " + totalPre4bet_up_countLastHand +
                            ", preTotal = preTotal + " + totalTotalCountLastHand +
                            ", pre_call2bet = pre_call2bet + " + totalPreCall2betCountLastHand +
                            ", pre_call3bet = pre_call3bet + " + totalPreCall3betCountLastHand +
                            ", pre_call4bet_up = pre_call4bet_up + " + totalPreCall4bet_up_countLastHand +
                            ", preCallTotal = preCallTotal + " + totalCallTotalCountLastHand +
                            " WHERE playerName = '" + opponentName + "'");

            st.close();
            closeDbConnection();
        }
    }

    private void addOpponentToDbIfNecessary(String opponentName) throws Exception {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM opponentidentifier_2_0_preflopstats WHERE playerName = '" + opponentName + "';");

        if(!rs.next()) {
            Statement st2 = con.createStatement();
            st2.executeUpdate("INSERT INTO opponentidentifier_2_0_preflopstats (playerName) VALUES ('" + opponentName + "')");
            st2.close();

            System.out.println("Opponent: " + opponentName + " was not present yet in preflopstats db. Added the opponent");
        }

        rs.close();
        st.close();
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/pokertracker?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
