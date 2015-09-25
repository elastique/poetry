package nl.elastique.poetry.data.test;

import android.test.AndroidTestCase;

import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import nl.elastique.poetry.data.json.JsonPathResolver;
import nl.elastique.poetry.data.json.JsonPersister;
import nl.elastique.poetry.data.test.data.DatabaseHelper;
import nl.elastique.poetry.data.test.data.JsonLoader;
import nl.elastique.poetry.data.test.data.models.Duck;
import nl.elastique.poetry.data.test.data.models.Group;
import nl.elastique.poetry.data.test.data.models.User;

public class JsonTestCase extends AndroidTestCase
{
    public void testJsonMapper() throws Exception
    {
        DatabaseHelper helper = DatabaseHelper.getHelper(getContext());

        // Load JSON
        JSONObject json = JsonLoader.loadObject(getContext(), R.raw.test);

        // Get child arrays from JSON
        JSONArray users_json = JsonPathResolver.resolveArray(json, "users");
        JSONArray groups_json = JsonPathResolver.resolveArray(json, "groups");
		JSONObject duck_json = JsonPathResolver.resolveObject(json, "duck");

        // Persist arrays to database
        JsonPersister persister = new JsonPersister(helper.getWritableDatabase());
        persister.persistArray(User.class, users_json);
		persister.persistArray(Group.class, groups_json);
		persister.persistObject(Duck.class, duck_json);

        Dao<User, Integer> user_dao = helper.getDao(User.class);
		Dao<Group, Integer> group_dao = helper.getDao(Group.class);
		Dao<Duck, Integer> duck_dao = helper.getDao(Duck.class);

        List<User> users = user_dao.queryForAll();
        assertEquals(users.size(), 2);

        List<Group> groups = group_dao.queryForAll();
        assertEquals(groups.size(), 3);

		Duck duck = duck_dao.queryForId(1);
		assertNotNull(duck);
		assertEquals("Quack", duck.getName());

        DatabaseHelper.releaseHelper();
    }
}