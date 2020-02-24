package db.migration.mysql;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import com.virgilsecurity.purekit.pure.storage.MariaDbPureStorage;

/**
 * Flyway migration which tables required for {@linkplain MariaDbPureStorage}.
 */
public class V2__Init_purekit extends BaseJavaMigration {

	@Override
	public void migrate(Context context) throws Exception {
		if (!"HSQL Database Engine".equals(
				context.getConfiguration().getDataSource().getConnection().getMetaData().getDatabaseProductName())) {
			MariaDbPureStorage pureStorage = new MariaDbPureStorage(context.getConfiguration().getDataSource());
			pureStorage.initDb(3600);
		}
	}

}
