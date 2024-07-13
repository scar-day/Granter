package dev.scarday.granter.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@DatabaseTable(tableName = "give_users")
public class GrantEntity {
    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(canBeNull = false)
    private UUID uuid;

    @DatabaseField(canBeNull = false)
    private String group;
}
