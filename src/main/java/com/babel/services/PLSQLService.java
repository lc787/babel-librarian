package com.babel.services;

import java.sql.*;

public class PLSQLService {
    private final Connection conn;

    public PLSQLService() {
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "babel", "babel");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public int callFunction(int id1, int id2) throws SQLException {
        CallableStatement cstmt = conn.prepareCall("{? = call getAccountAgeDifference(?, ?)}");
        cstmt.registerOutParameter(1, Types.INTEGER);
        cstmt.setInt(2, id1);
        cstmt.setInt(3, id2);
        cstmt.executeUpdate();
        int dateDif = cstmt.getInt(1);
        System.out.println(dateDif);
        return dateDif;
    }
}
