package com.virgilsecurity.demo.purekit.server.db;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PureTablesTest {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Test
	void allTablesExists() throws MetaDataAccessException {
		GetTableNames getTableNames = new GetTableNames();
		@SuppressWarnings("unchecked")
		ArrayList<String> tables = (ArrayList<String>) JdbcUtils
				.extractDatabaseMetaData(this.jdbcTemplate.getDataSource(), getTableNames);
		assertTrue(tables.containsAll(Arrays.asList("LAB_TESTS", "PATIENTS", "PHYSICIANS", "PHYSICIAN_ASSIGNMENTS",
				"PRESCRIPTIONS", "VIRGIL_GRANT_KEYS", "VIRGIL_KEYS", "VIRGIL_ROLES", "VIRGIL_ROLE_ASSIGNMENTS",
				"VIRGIL_USERS")));
	}

	class GetTableNames implements DatabaseMetaDataCallback {

		public Object processMetaData(DatabaseMetaData dbmd) throws SQLException {
			ResultSet rs = dbmd.getTables(null, null, null, new String[] { "TABLE" });
			ArrayList<String> l = new ArrayList<>();
			while (rs.next()) {
				l.add(rs.getString(3));
			}
			return l;
		}
	}
}
