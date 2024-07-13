package dev.scarday.granter.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import dev.scarday.granter.database.entities.GrantEntity;
import org.bukkit.entity.Player;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.UUID;

public class Database {
    private final Dao<GrantEntity, UUID> grantDao;

    public Database(String path) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);
        TableUtils.createTableIfNotExists(connectionSource, GrantEntity.class);
        grantDao = DaoManager.createDao(connectionSource, GrantEntity.class);
    }

    public Database(String url, String username, String password) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource(url, username, password); //"jdbc:mysql://localhost:3306/mydb" "user" "password"
        TableUtils.createTableIfNotExists(connectionSource, GrantEntity.class);
        grantDao = DaoManager.createDao(connectionSource, GrantEntity.class);
    }

    public void givePlayer(Player player, String groupName) throws SQLException {
        GrantEntity entity = new GrantEntity();
        entity.setId(null);
        entity.setUuid(player.getUniqueId());
        entity.setGroup(groupName);
        grantDao.create(entity);
    }

    public long countGrantEntities(Player player, String groupName) throws SQLException {
        QueryBuilder<GrantEntity, UUID> queryBuilder = grantDao.queryBuilder();
        Where<GrantEntity, UUID> where = queryBuilder.where();

        where.eq("uuid", player.getUniqueId())
                .and()
                .eq("group", groupName);

        return where.countOf();
    }
}
